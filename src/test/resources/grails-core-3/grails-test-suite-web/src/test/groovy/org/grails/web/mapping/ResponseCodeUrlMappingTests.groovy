package org.grails.web.mapping

import grails.web.mapping.UrlMappingsHolder
import org.grails.web.mapping.DefaultUrlCreator
import org.grails.web.mapping.DefaultUrlMappingsHolder
import org.springframework.core.io.ByteArrayResource

/**
 * @author mike
 */
class ResponseCodeUrlMappingTests extends AbstractGrailsMappingTests {
    def topLevelMapping = '''
mappings {
    "404"{
        controller = "errors"
        action = "error404"
    }

    "500"(controller:"errors", action:"custom", exception:IllegalArgumentException)
    "500"(controller:"errors", action:"error500")
}
'''
    UrlMappingsHolder holder

    void setUp() {
        super.setUp()
        def res = new ByteArrayResource(topLevelMapping.bytes)

        def mappings = evaluator.evaluateMappings(res)

        // use un-cached holder for testing
        holder = new DefaultUrlMappingsHolder(mappings,null,true)
        holder.setUrlCreatorMaxWeightedCacheCapacity(0)
        holder.initialize()
    }

    void testParse() {
        assertNotNull holder
    }

    void testMatch() {
        assertNull holder.match("/")
    }

    void testMatchStatusCodeAndException() {
        def info = holder.matchStatusCode(500)

        assertEquals "error500", info.actionName

        info = holder.matchStatusCode(500, new IllegalArgumentException())

        assertEquals "custom", info.actionName
    }

    void testForwardMapping() {
        def info = holder.matchStatusCode(404)
        assertNotNull info
        assertEquals("errors", info.getControllerName())
        assertEquals("error404", info.getActionName())
    }

    void testForwardMappingWithNamedArgs() {
        def info = holder.matchStatusCode(500)
        assertNotNull info
        assertEquals("errors", info.getControllerName())
        assertEquals("error500", info.getActionName())
    }

    void testMissingForwardMapping() {
        def info = holder.matchStatusCode(501)
        assertNull info
    }

    void testNoReverseMappingOccures() {
        def creator = holder.getReverseMapping("errors", "error404", null)

        assertTrue ("Creator is of wrong type: " + creator.class, creator instanceof DefaultUrlCreator)
    }
}
