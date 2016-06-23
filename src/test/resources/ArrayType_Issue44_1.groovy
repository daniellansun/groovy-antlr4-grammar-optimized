class A {
    private run() {
//        String a
//        String[] b
//        A<String[]> c

        A<String[], B<int[]>> d
    }
}


String[][] a = [
        ['1', '2'],
        ['3', '4']
]

List<String[][]> b = [[
                              ['1', '2'],
                              ['3', '4']
                      ],
                      [
                              ['a', 'b'],
                              ['c', 'd']
                      ]];

List<String>[][][] c = null;
int[][][][] d = null;

/*
   public java.lang.Object run() {
        a = {
            this.println(it)
        }
        b = 'Hello'
        this.a(b)
        A c =


a = { println(it) }
b = "Hello"
a b
helloWorld c // MethodCallExpression
HelloWorld d // DeclarationExpression

// Am I right, that style convention defines, how we should parse and resolve that ambiguity?
    }
* */
