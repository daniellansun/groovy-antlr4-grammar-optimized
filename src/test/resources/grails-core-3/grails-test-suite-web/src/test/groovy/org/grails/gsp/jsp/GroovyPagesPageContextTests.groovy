package org.grails.gsp.jsp

import grails.util.GrailsWebMockUtil

import javax.servlet.jsp.PageContext

import org.springframework.web.context.request.RequestContextHolder

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class GroovyPagesPageContextTests extends GroovyTestCase {

    protected void setUp() {
        GrailsWebMockUtil.bindMockWebRequest()
    }

    protected void tearDown() {
        RequestContextHolder.resetRequestAttributes()
    }

    void testPageContextState() {

        def pageContext = new GroovyPagesPageContext()

        assert pageContext.getServletConfig()
        assert pageContext.getServletContext()
        assert pageContext.getRequest()
        assert pageContext.getResponse()
        assert pageContext.getPage()
    }

    void testPageContextScopes() {
        def pageContext = new GroovyPagesPageContext()

        pageContext.setAttribute "foo", "bar"

        assertEquals "bar", pageContext.getAttribute("foo")
        assertEquals "bar", pageContext.getAttribute("foo", PageContext.PAGE_SCOPE)

        assertNull pageContext.getAttribute("foo", PageContext.REQUEST_SCOPE)

        assertTrue "Variable name 'foo' does not appear in list of names in page scope",
            pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE).toList().contains('foo')

        assertEquals PageContext.PAGE_SCOPE, pageContext.getAttributesScope("foo")
        assertEquals "bar", pageContext.findAttribute("foo")

        pageContext.setAttribute "foo", "diff", PageContext.SESSION_SCOPE

        assertEquals "bar", pageContext.getAttribute("foo")
        assertEquals "bar", pageContext.getAttribute("foo", PageContext.PAGE_SCOPE)
        assertEquals "bar", pageContext.findAttribute("foo")
        assertEquals "diff", pageContext.getAttribute("foo", PageContext.SESSION_SCOPE)

        pageContext.removeAttribute "foo"

        assertEquals "diff", pageContext.findAttribute("foo")
        assertEquals "diff", pageContext.getAttribute("foo", PageContext.SESSION_SCOPE)
        assertNull pageContext.getAttribute("foo")
        assertNull pageContext.getAttribute("foo", PageContext.PAGE_SCOPE)
    }
}
