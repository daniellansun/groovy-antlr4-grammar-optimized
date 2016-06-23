package org.grails.web.taglib
/**
 * @author Graeme Rocher
 * @since 1.0
 */
class NamespacedTagAndActionConflictTests extends AbstractGrailsTagTests {

    protected void onSetUp() {
        gcl.parseClass '''
class FeedsTagLib {
    static namespace = "feed"
    def rss = {
        out << "rss feed"
    }
}
@grails.artefact.Artefact('Controller')
class TestController {
    def feed = {
        "foo"
    }
    def test = {
        println "FEED IS $feed"
        // should favour local action of feed tag
        assert feed instanceof Closure
        render feed()
    }
}
'''
    }

    void testTagLibNamespaceAndActionConflict() {
        def controllerClass = ga.getControllerClass("TestController").clazz

        def controller = controllerClass.newInstance()

        controller.test()

        assertEquals "foo", response.contentAsString
    }
}
