
class A {
    def a() {
        a()
        a(12)
        Integer.method('0xff')
        Integer.someDummyProperty.method('0xff')
        Integer.someDummyProperty*.spreadMethod('0xff')
        Integer.someDummyProperty?.safeMethod('0xff', 12)

        //FIXME check it after bug was fixed.
        // Integer.someDummyProperty.@attributeMethod('0xff', 12)
        Integer.some.dummy.property.path.method('0xff', 12)
    }

    def b() {
        [1, 2, 3].each { print it }
    }

    def a(p1, p2) {
        println "$p1, $p2"

        a 1, {print 'abc'}

        a(1) {
            print 'abc'
        }

        this.a(1) {
            print 'abc'
        }
    }
}
