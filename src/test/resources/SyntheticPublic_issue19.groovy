class A {
    public void m1() {}
    void m2() {}
    final String m3() {}
    static String m4() {}
    final m5() {}
    static m6() {}
    private m7() {}
    private String m8() {}
    synchronized m9() {}

    @Override
    public String m10() {}

    @Override
    String m11() {}

    @Override
    protected String m12() {}

}

@interface B {
    public void m13();
    abstract String m14();
    String m15();
}

interface C {
    public void m16();
    abstract String m17();
    String m18();
}