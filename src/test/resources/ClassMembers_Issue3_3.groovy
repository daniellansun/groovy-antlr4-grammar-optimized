interface X {
    public <T> List<T> waitAll(List<Promise<T>> promises, final long timeout, final TimeUnit units);
}

class CC extends
        Specification
        implements
                XX,
                YY {
}

interface II
        extends
                XX,
                YY {

}
