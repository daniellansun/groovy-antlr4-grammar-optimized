println withPool {

}

println aa("bb", "cc") {

}

println this.aa("bb", "cc") {

}

println aa("bb", {println 123;}, "cc") {

}

aa("bb", "cc") {
    println 1
} { println 2 }

cc  {
    println 1
} {
    println 2
}

dd {
    println 3
}

obj.cc  {
    println 1
} {
    println 2
}

bb 1, 2, {println 123;}

obj."some method" (groovy.xml.dom.DOMCategory) {
}

obj."some ${'method'}" (groovy.xml.dom.DOMCategory) {
}
obj.someMethod (groovy.xml.dom.DOMCategory) {
}

use (groovy.xml.dom.DOMCategory) {
}

switch (1) {
    case 1+2:
        println(2);
        break;
    case 1+3: println(3);
        break;
    case 1+4: println(4);
    default: println('5'); break;
}


