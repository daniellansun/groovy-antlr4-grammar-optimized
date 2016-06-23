
/*
 * Copyright 2011 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.test.mixin.web

import grails.artefact.TagLibrary
import grails.core.GrailsTagLibClass
import groovy.text.Template
import org.grails.buffer.GrailsPrintWriter
import org.grails.core.artefact.TagLibArtefactHandler
import org.grails.gsp.GroovyPagesTemplateEngine
import org.grails.taglib.TagLibraryLookup
import org.junit.Assert

/**
 * <p>A unit testing mixing that add behavior to support the testing of tag libraries
 * and GSP pages. Can be used in combination with
 * {@link grails.test.mixin.domain.DomainClassUnitTestMixin} to support the testing of
 * tag libraries and GSPs</p>
 *
 * <p>Views and templates can be mocked via the {@link ControllerUnitTestMixin#getGroovyPages()}
 * map where the keys are the names of the pages and the values are the contents of the GSP page</p>
 *
 * <p>Alternatively, within a Grails project the unit test will fallback to loading views and templates
 * from grails-app/views</p>
 *
 * <p>For mocking tag libraries the {@link GroovyPageUnitTestMixin#mockTagLib(Class) } method allows you
 * to mock custom tag libraries. There is no need to mock core tag libraries like <g:createLink /> as these
 * will be loaded on demand </p>
 *
 * @author Graeme Rocher
 * @since 2.0
 */
class GroovyPageUnitTestMixin extends ControllerUnitTestMixin {
    private static final Set<String> REQUIRED_FEATURES = (["groovyPage"] as Set).asImmutable()
    
    public GroovyPageUnitTestMixin(Set<String> features) {
        super((REQUIRED_FEATURES + features) as Set)
    }
    
    public GroovyPageUnitTestMixin() {
        super(REQUIRED_FEATURES)
    }

    /**
     * Mocks a tag library, making it available to subsequent calls to controllers mocked via
     * {@link #mockController(Class) } and GSPs rendered via {@link #applyTemplate(String, Map) }
     *
     * @param tagLibClass The tag library class
     * @return The tag library instance
     */

    def mockTagLib(Class tagLibClass) {
        GrailsTagLibClass tagLib = grailsApplication.addArtefact(TagLibArtefactHandler.TYPE, tagLibClass)
        final tagLookup = applicationContext.getBean(TagLibraryLookup)


        defineBeans(true) {
            "${tagLib.fullName}"(tagLibClass) { bean ->
                bean.autowire = true
            }
        }

        tagLookup.registerTagLib(tagLib)

        def taglibObject = applicationContext.getBean(tagLib.fullName)
        if(taglibObject instanceof TagLibrary) {
            ((TagLibrary)taglibObject).setTagLibraryLookup(tagLookup)
        }

        return taglibObject
    }

    /**
     * Mimics the behavior of the render method in controllers but returns the rendered contents directly
     *
     * @param args The same arguments as the controller render method accepts
     * @return The resulting rendering GSP
     */
    String render(Map args) {
        def uri = null
        final attributes = webRequest.attributes
        if (args.template) {
            uri = attributes.getTemplateUri(args.template as String, request)
        }
        else if (args.view) {
            uri = attributes.getViewUri(args.view as String, request)
        }
        if (uri != null) {
            def engine = applicationContext.getBean(GroovyPagesTemplateEngine)
            final t = engine.createTemplate(uri)
            if (t != null) {
                def sw = new StringWriter()
                renderTemplateToStringWriter(sw, t, args.model ?: [:])
                return sw.toString()
            }
        }
        return null
    }
    /**
     * Renders a template for the given contents and model
     *
     * @param contents The contents
     * @param model The model
     * @return The rendered template
     */
    String applyTemplate(String contents, Map model = [:]) {
        def sw = new StringWriter()
        applyTemplate sw, contents, model
        return sw.toString()
    }

    protected void applyTemplate(StringWriter sw, template, params = [:]) {
        def engine = applicationContext.getBean(GroovyPagesTemplateEngine)

        def t = engine.createTemplate(template, "test_" + System.currentTimeMillis())
        renderTemplateToStringWriter(sw, t, params)
    }

    private renderTemplateToStringWriter(StringWriter sw, Template t, params) {
        if (!webRequest.controllerName) {
            webRequest.controllerName = 'test'
        }
        if (!webRequest.actionName) {
            webRequest.actionName = 'index'
        }
        def w = t.make(params)
        def previousOut = webRequest.out
        try {
            def out = new GrailsPrintWriter(sw)
            webRequest.out = out
            w.writeTo(out)

        }
        finally {
            webRequest.out = previousOut
        }
    }

    /**
    * Asserts the output of a given template against the specified expected value.
    *
    * @param expected The expected output
    * @param template A snippet of GSP
    * @param params An optional parameter that allows variables to be placed in the binding of the GSP
    * @param transform An optional parameter that allows the specification of a closure to transform the passed StringWriter
    */
   void assertOutputEquals(expected, template, params = [:], Closure transform = { it.toString() }) {
       def sw = new StringWriter()
       applyTemplate sw, template, params
       Assert.assertEquals expected, transform(sw)
   }

   /**
    * Asserts the output of a given template against the specified regex.
    *
    * @param expected The regex expression expected to match the output
    * @param template A snippet of GSP
    * @param params An optional parameter that allows variables to be placed in the binding of the GSP
    * @param transform An optional parameter that allows the specification of a closure to transform the passed StringWriter
    */
   void assertOutputMatches(regex, template, params = [:], Closure transform = { it.toString() }) {
       def sw = new StringWriter()
       applyTemplate sw, template, params
       Assert.assertTrue (transform(sw) ==~ regex)
   }
}
