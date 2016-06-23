def "hi world"(p1, p2) {
    println "$p1, $p2"
}

// This must be a method declaration, not an invocation of "String(m() {})"
String m() {}
