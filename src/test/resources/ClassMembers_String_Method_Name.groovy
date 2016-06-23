class A {
    def "hello world"(p1, p2) {
        println "$p1, $p2"
    }

    def run() {
	    this."hello world"('ab', 'bc')
    }
}


