
class A {
    private def testAssert1() {
        assert true
        assert false : 'this is an error expression'
        assert 42
        assert 42 : 'Thanks for all the fish!'
        // This one is allowed in Groovy, too.
        assert 42, 'Thanks for all the fish!'
    }

    private def testAssert2() {
        assert 2+2 == 1+3
        assert 2+2 == 1+3 : cowabunga
        // How will it end?
    }

    private def testAssertFreeForm() {
        assert logic3 :
            'Error message'
        assert logic4
        if (1) { }
    }
}
