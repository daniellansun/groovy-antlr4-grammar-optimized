package org.grails.gsp.compiler.tags

class GroovyGrepTagTests extends GroovyTestCase {

    void testDoStartTag() {
        def sw = new StringWriter()

        def tag = new GroovyGrepTag()
        tag.init(out: new PrintWriter(sw))

        try {
            tag.doStartTag()
            fail("Should throw exception for required attributes")
        }
        catch(Exception e) {
            // expected
        }

        tag.setAttributes('"in"': 'test', '"filter"':'\${~/regex/}')

        tag.doStartTag()

        assertEquals("for( "+tag.getForeachRenamedIt()+" in test.grep(~/regex/) ) {"+System.getProperty("line.separator")+ "changeItVariable(" + tag.getForeachRenamedIt() + ")" + System.getProperty("line.separator"), sw.toString())
    }

    void testWithStatus() {
        def sw = new StringWriter()

        def tag = new GroovyGrepTag()
        tag.init(out: new PrintWriter(sw))

        tag.setAttributes('"in"': 'test', '"filter"':'\${~/regex/}','"status"':"i",'"var"':"t")

        tag.doStartTag()

        assertEquals("loop:{int i = 0for( t in test.grep(~/regex/) ) {", sw.toString().replaceAll('[\r\n]', ''))
    }
}
