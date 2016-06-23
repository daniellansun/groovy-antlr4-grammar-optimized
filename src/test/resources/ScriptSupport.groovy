/*
 * The Computer Language Shootout
 * http://shootout.alioth.debian.org/
 *
 * contributed by Jochen Hinrichsen
 */
/* More fun */

println "Script before classes"

def wazzup() {
}

int thisWorksMNow = 2

class B {
    String memberA = 'B', memberC, memberD

    private long one, two = 2
}

long one, two = 42
long _$one=3, $two=1

def A(x, y) {
    long three=5, six=2

    if (x == 0) return y+1
    if (y == 0) return A(x-1, 1)
    return A(x-1, A(x, y-1))
}

def n = args[0].toInteger()
def result = A(3, n)
println("Ack(3,${n}): ${result}")
