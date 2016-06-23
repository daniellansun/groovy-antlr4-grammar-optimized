
class A {
    private def method() {
        23 * 12 + 2 || '23' + !23 && 4 + 2 / 2 - 3 || 23
    }

    private def method2() {
        !5
        ~5
        -5
        +5
        // -a FIXME Return unary operators.
        // +a

        5 + 10
        5 - 10
        5 * 10
        5 / 10
        5 % 10
        5 ** 10

        5--
        5++
        --5
        ++5

        5 >> 10
        5 >>> 10
        5 << 10
        5 > 10
        5 < 10

        5 ^ 10

        5 | 10
        5 & 10

        5 || 10
        5 && 10
        5 ==  10
        5 !=  10
        5 <=>  10

        5..10
        5..10
        5..<10

        5 in null
        5 as Integer
        (Integer)5
        5 instanceof Integer

        5.properties
        5*.properties
        5?.properties
        5.@properties
        5.&method1

        5 =~ 'pattern'
        5 ==~ 'pattern'

    }

    def $testMethodCall() {
        a.method1()
        a."$method3"()
        a."method2"()
        a.'$method4'()

        a?.method1()
        a?."$method3"()
        a?."method2"()
        a?.'$method4'()

        a*.method1()
        a*."$method3"()
        a*."method2"()
        a*.'$method4'()
    }

    def testMemberAccess() {
        5.'properties'
        5*.'properties'
        5?.'properties'
        5.@'properties'
        5.&'method1'

        5."properties"
        5*."properties"
        5?."properties"
        5.@"properties"
        5.&"method1"

        left >>>= 16
        left >>= 8
        left <<= 2
        sum += sup
        cantlive -= you
        times *= me

        def a = { slash /= nstore }

        your |= me
        here %= sometimes
        water ^= oil
        this &= that

        5."$properties"
        5*."$properties"
        5?."$properties"
        5.@"$properties"
        5.&"$method1"
    }

    def testElvis() {
        assert !'elvis' ? 'presley' : false : 1 ? 2 : 3
        int c = 1 + 1 / 3 ? 23 / 32 : false
    }
}
