def (int a, long b) = [1, 2]
def (int c, d) = [1, 2]
def (e, long f) = [1, 2]
def (g, h) = [1, 2]
def (int i, long j, double k) = [1, 2, 3.3]
def (String str) = ['hello']
(k, j, i)   = [3.3, 2, 1]

def f(x, y) {
    return [x, y]
}
def (int x, int y) = f(1, 2)
(x, y) = f(2, 3)

final def (int q, int w) = [1, 2]


