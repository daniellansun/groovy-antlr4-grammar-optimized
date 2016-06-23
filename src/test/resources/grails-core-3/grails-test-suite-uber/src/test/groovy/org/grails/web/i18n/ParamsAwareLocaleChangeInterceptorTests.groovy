package org.grails.web.i18n

import grails.util.GrailsWebMockUtil

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.i18n.SessionLocaleResolver

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class ParamsAwareLocaleChangeInterceptorTests extends GroovyTestCase {

    protected void tearDown() {
        RequestContextHolder.resetRequestAttributes()
    }

    void testSwitchLocaleWithStringArrayParamsObject() {

        def webRequest = GrailsWebMockUtil.bindMockWebRequest()

        def request = webRequest.getCurrentRequest()
        def response = webRequest.getCurrentResponse()

        SessionLocaleResolver localeResolver = new SessionLocaleResolver()

        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE,localeResolver)

        def localeChangeInterceptor = new ParamsAwareLocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "lang"

        def locale = localeResolver.resolveLocale(request)
        assert localeChangeInterceptor.preHandle(request, response, null)

        assertEquals locale, localeResolver.resolveLocale(request)

        webRequest.params.lang = ["de_DE", "en_GB"] as String[]

        assert localeChangeInterceptor.preHandle(request, response, null)

        assertNotSame locale, localeResolver.resolveLocale(request)

        locale = localeResolver.resolveLocale(request)

        assertEquals "de", locale.getLanguage()
        assertEquals "DE", locale.getCountry()
    }

    void testSwitchLocaleWithParamsObject() {

        def webRequest = GrailsWebMockUtil.bindMockWebRequest()

        def request = webRequest.getCurrentRequest()
        def response = webRequest.getCurrentResponse()

        SessionLocaleResolver localeResolver = new SessionLocaleResolver()

        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE,localeResolver)

        def localeChangeInterceptor = new ParamsAwareLocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "lang"

        def locale = localeResolver.resolveLocale(request)
        assert localeChangeInterceptor.preHandle(request, response, null)

        assertEquals locale, localeResolver.resolveLocale(request)

        webRequest.params.lang = "de_DE"

        assert localeChangeInterceptor.preHandle(request, response, null)

        assertNotSame locale, localeResolver.resolveLocale(request)

        locale = localeResolver.resolveLocale(request)

        assertEquals "de", locale.getLanguage()
        assertEquals "DE", locale.getCountry()
    }

    void testSwithLocaleWithRequestParameter() {

        def webRequest = GrailsWebMockUtil.bindMockWebRequest()

        MockHttpServletRequest request = webRequest.getCurrentRequest()
        def response = webRequest.getCurrentResponse()

        SessionLocaleResolver localeResolver = new SessionLocaleResolver()

        request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE,localeResolver)

        def localeChangeInterceptor = new ParamsAwareLocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "lang"

        def locale = localeResolver.resolveLocale(request)
        assert localeChangeInterceptor.preHandle(request, response, null)

        assertEquals locale, localeResolver.resolveLocale(request)

        request.addParameter "lang", "de_DE"

        assert localeChangeInterceptor.preHandle(request, response, null)

        assertNotSame locale, localeResolver.resolveLocale(request)

        locale = localeResolver.resolveLocale(request)

        assertEquals "de", locale.getLanguage()
        assertEquals "DE", locale.getCountry()
    }
}
