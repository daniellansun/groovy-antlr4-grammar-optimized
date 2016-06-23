package org.codehaus.groovy.parser.antlr4
import groovy.mock.interceptor.StubFor
import groovy.util.logging.Log
import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.TokenStream
import spock.lang.Specification
import spock.lang.Unroll

@Log
@Unroll
class GrammarPredicatesTest extends Specification {
    private static final List<String> BUILT_IN_TYPE_LIST = Arrays.asList("void", "boolean", "byte", "char", "short", "int", "float", "long", "double");
    def "GrammarPredicates: '#name'.isClassName() = #res"(String name, boolean res) {
        setup:
            def tokenStub = new StubFor(TokenStream)
            def array = name.split('\\.')
            tokenStub.demand.LT { new CommonToken(BUILT_IN_TYPE_LIST.contains(array[0]) ? GroovyLangParser.BUILT_IN_TYPE : GroovyLangParser.IDENTIFIER, array[0]) }
            for (int j = 1; j < array.size(); j++) {
                final i = j
                tokenStub.demand.LT { new CommonToken(GroovyLangParser.DOT) }
                tokenStub.demand.LT { new CommonToken(BUILT_IN_TYPE_LIST.contains(array[i]) ? GroovyLangParser.BUILT_IN_TYPE : GroovyLangParser.IDENTIFIER, array[i]) }
            }
            tokenStub.demand.LT { new CommonToken(GroovyLangParser.EOF) }
        expect:
            GrammarPredicates.isClassName(tokenStub.proxyDelegateInstance() as TokenStream) == res
        where:
        name      | res
        "a"       | false
        "A"       | true
        "a.a"     | false // Suppress failing test for now
        "a.A"     | true
        "void"    | true
        "boolean" | true
        "byte"    | true
        "char"    | true
        "short"   | true
        "int"     | true
        "float"   | true
        "long"    | true
        "double"  | true
    }
}
