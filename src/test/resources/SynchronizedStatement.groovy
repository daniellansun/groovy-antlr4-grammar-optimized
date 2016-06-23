synchronized (a) {
    println 1
}

synchronized (a[1]) {
    println 1
}

synchronized (a('c')) {
    println 1
}

synchronized (b.a('c')) {
    println 1
}

synchronized (TT.class) {
    println 1
}
