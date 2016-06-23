// call expressions
((int) 1 / 2)(1, 2) {} {} (2, 3, 4) {}
(((int) 1 / 2))(1, 2) {} {} (2, 3, 4) {}
(m())()
((Integer)m())()

// TODO SUPPORT CALL ON class name. e.g. (int)()
'm'() + /aa/() + $/bb/$() + "$m"() + /a${'x'}a/() + $/b${'x'}b/$() + 1.2('b') + 1('a') + 2() + null() + true() + false() + a() + {a,b->}(1, 2) + [1, 2]() + [a:1, b:2]() + new int[0]() + new Integer(1)()

// cast expressions
(int)(1 / 2)
(Integer)(1 / 2)
(java.lang.Integer)(1 / 2)
