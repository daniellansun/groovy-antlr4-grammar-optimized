package org.grails.plugins.web

import org.springframework.mock.web.*

import javax.servlet.ServletContext
import javax.servlet.http.HttpSession;

import org.springframework.web.util.*

class ServletsExtensionTests extends AbstractGrailsPluginTests {

    protected void onSetUp() {

        pluginsToLoad << gcl.loadClass("org.grails.plugins.CoreGrailsPlugin")

        def remove = GroovySystem.metaClassRegistry.&removeMetaClass

        remove MockServletContext
        remove MockHttpSession
        remove MockHttpServletResponse
        remove MockHttpServletRequest
    }

    void testIsXhrRequest() {
        def request = new MockHttpServletRequest()
        assert !request.xhr : "This should not be an XHR request"

        request.addHeader "X-Requested-With", "XMLHttpRequest"
        assert request.xhr : "This should be an XHR request"

    }

    void testServletContextObject() {
        def context = new MockServletContext()

        println context.metaClass.getMetaMethod("getProperty")
        println ServletContext.metaClass.getMetaMethod("getProperty")
        context["foo"] = "bar"
        assertEquals "bar", context["foo"]

        context.foo = "fred"
        assertEquals "fred", context.foo
        assertEquals "fred", context.getAttribute('foo')

        context.removeAttribute("foo")
    }

    void testHttpSessionObject() {
        def session = new MockHttpSession()
        def httpSessionMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(HttpSession)
        def metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(session.getClass())
        assert session.getProperty("creationTime")

        session["foo"] = "bar"
        assertEquals "bar", session["foo"]

        session.foo = "fred"
        assertEquals "fred", session.foo

        session.removeAttribute("foo")

        assert !session.foo
    }

    void testResponseWrite() {
        def response = new MockHttpServletResponse()

        response << "foo"
        response << 10

        assertEquals "foo10", response.contentAsString
    }

    void testGetForwardURI() {
        def request = new MockHttpServletRequest()
        request.requestURI = "/foo/bar"
        assertEquals "/foo/bar", request.forwardURI

        request[WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE] = "/bar/foo"
        assertEquals "/bar/foo", request.forwardURI
    }

    void testHttpMethodMethods() {
        def request = new MockHttpServletRequest()

        request.method = "POST"

        assert request.isPost()
        assert !request.isGet()
        assert request.post
        assert !request.get

        request.method = 'GET'

        assert request.get
        assert !request.post
    }

    void testAccessRequestAttributes() {
        def request = new MockHttpServletRequest()

        request["foo"] = "bar"
        assertEquals "bar", request["foo"]
        assertEquals "bar", request.foo

        request.foo = "foo"

        assertEquals "foo", request["foo"]
        assertEquals "foo", request.foo
    }

    void testEachMethod() {
        def request = new MockHttpServletRequest()

        request["foo"] = "bar"
        request["bar"] = "foo"

        def list = []
        request.each { k,v -> list << v }
        assert list.contains("bar")
        assert list.contains("foo")
    }

    void testFindAllMethod() {
        def request = new MockHttpServletRequest()

        request["foo"] = "bar"
        request["bar"] = "foo"
        request["foobar"] = "yes!"

        def results = request.findAll {
            it.key.toString().startsWith("foo")
        }

        assertEquals([foo:"bar",foobar:"yes!"], results)
    }

    void testFindMethod() {
        def request = new MockHttpServletRequest()

        request["foo"] = "bar"
        request["bar"] = "foo"
        request["foobar"] = "yes!"

        def results = request.find { it.key.toString().startsWith("bar") }

        assertEquals([bar:"foo"], results)
    }
}
