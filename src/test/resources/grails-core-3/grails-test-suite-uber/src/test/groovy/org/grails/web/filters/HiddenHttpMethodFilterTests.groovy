package org.grails.web.filters

import org.grails.web.filters.HiddenHttpMethodFilter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import javax.servlet.FilterChain

/**
 * @author Graeme Rocher
 * @since 1.1
 */
class HiddenHttpMethodFilterTests extends GroovyTestCase {

    void testDefaultCase() {
        def filter = new HiddenHttpMethodFilter()
        def req = new MockHttpServletRequest()
        def res = new MockHttpServletResponse()
        req.setMethod("POST")
        String method
        filter.doFilter(req, res, { req2, res2 -> method = req2.method } as FilterChain)

        assertEquals "POST", method
    }

    void testWithParameter() {
        def filter = new HiddenHttpMethodFilter()
        def req = new MockHttpServletRequest()
        def res = new MockHttpServletResponse()
        req.addParameter("_method", "DELETE")
        req.setMethod("POST")
        String method
        filter.doFilter(req, res, { req2, res2 -> method = req2.method } as FilterChain)

        assertEquals "DELETE", method
    }

    void testWithHeader() {
        def filter = new HiddenHttpMethodFilter()
        def req = new MockHttpServletRequest()
        req.addHeader(HiddenHttpMethodFilter.HEADER_X_HTTP_METHOD_OVERRIDE, "DELETE")
        def res = new MockHttpServletResponse()
        // req.addParameter("_method", "DELETE")
        req.setMethod("POST")
        String method
        filter.doFilter(req, res, { req2, res2 -> method = req2.method } as FilterChain)

        assertEquals "DELETE", method
    }
}
