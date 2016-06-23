class 类A {
    private String 实例变量1 = '实例变量1内容';
    public static final String 类变量1 = '类变量1内容';

    public 类A() {}

    def 方法1(参数1, 参数2) {
        String 局部变量1 = '局部变量1的内容'
        方法1(局部变量1, '参数2的内容')
    }

    def 方法2(参数1, 参数2) {
        this."方法2"(参数1, 参数2)
    }

    def "方法3"(参数1, 参数2) {
        this."方法3"(参数1, 参数2)
    }

    def testGString() {
        return "a$变量"
    }

    def testSlashyGString() {
        a =~ /a$变量/
    }
}

