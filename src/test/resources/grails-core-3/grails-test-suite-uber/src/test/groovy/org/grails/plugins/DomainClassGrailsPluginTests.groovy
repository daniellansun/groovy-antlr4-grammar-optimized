package org.grails.plugins

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit
import org.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.grails.compiler.injection.GrailsAwareClassLoader
import org.grails.plugins.web.AbstractGrailsPluginTests
import grails.compiler.ast.ClassInjector

class DomainClassGrailsPluginTests extends AbstractGrailsPluginTests {

    void onSetUp() {
        gcl = initClassLoader()

        def fs = File.separator
        gcl.parseClass(
                """class Test{ }""", "myapp${fs}grails-app${fs}domain${fs}Test.groovy")

        gcl.parseClass("""abstract class Parent {
  String name
} """, "myapp${fs}grails-app${fs}domai${fs}Parent.groovy")

        gcl.parseClass("""class Child extends Parent {
   Date someDate
} """, "myapp${fs}grails-app${fs}domain${fs}Child.groovy")

        gcl.parseClass("""package grails.test
class Parent2 {
    Date someDate
    String toString() {
        return 'my toString'
    }
} """, "myapp${fs}grails-app${fs}domain${fs}grails${fs}test${fs}Parent2.groovy")

        gcl.parseClass("""class Child2 extends grails.test.Parent2 {
    String someField
    String toString() {
        return 'my other toString'
    }

} """, "myapp${fs}grails-app${fs}domain${fs}Child2.groovy")

       gcl.parseClass("""class Child3 extends grails.test.Parent2 {
   String someField

} """, "myapp${fs}grails-app${fs}domain${fs}Child3.groovy")

        pluginsToLoad << gcl.loadClass("org.grails.plugins.domain.DomainClassGrailsPlugin")
    }

    private GrailsAwareClassLoader initClassLoader() {
        GrailsAwareClassLoader gcl
        gcl = new GrailsAwareClassLoader()
        gcl.classInjectors = [new DefaultGrailsDomainClassInjector() {
            @Override
            boolean shouldInject(URL url) {
                true
            }

        }] as ClassInjector[]
        return gcl
    }

    void testDomainClassesPlugin() {
        assert appCtx.containsBean("TestDomainClass")
        def instance = appCtx.getBean("TestDomainClass").newInstance()
        assertNull instance.id
        assertNull instance.version
    }

    void testToString() {
        def instance = appCtx.getBean("ChildDomainClass").newInstance()

        assertEquals('Child : (unsaved)', instance.toString())

        instance.id = 1
        assertEquals('Child : 1', instance.toString())

        instance = appCtx.getBean("TestDomainClass").newInstance()
        instance.id = 2
        assertEquals('Test : 2', instance.toString())

        instance = appCtx.getBean("grails.test.Parent2DomainClass").newInstance()
        instance.id = 3
        def parent2ToString = 'my toString'
        assertEquals(parent2ToString, instance.toString())

        instance = appCtx.getBean("Child2DomainClass").newInstance()
        instance.id = 4
        assertEquals('my other toString', instance.toString())

        instance = appCtx.getBean("Child3DomainClass").newInstance()
        instance.id = 5
        assertEquals(parent2ToString, instance.toString())
    }

    void testInjectIds() {
        def fs = File.separator
        def clz = gcl.parseClass('''
class IdTest {}
''', "myapp${fs}grails-app${fs}domain${fs}IdTest.groovy")

        def obj = clz.newInstance()
        obj.id = 10
        obj.version = 2

        assertEquals 10, obj.id
        assertEquals 2, obj.version
    }

    void testHasManyAssociationIsGenericized() {
        def fs = File.separator
        def idTestClass = gcl.parseClass('''
class IdTest {
    static hasMany = [others:AssocTest]
}
class AssocTest {}
''', "myapp${fs}grails-app${fs}domain${fs}IdTest.groovy")

        def othersField = idTestClass.getDeclaredField('others')
        assertNotNull 'others field was null', othersField

        def genericType = othersField.genericType
        assertNotNull 'genericType was null', genericType
        assertTrue 'genericType was not a ParameterizedType', genericType instanceof java.lang.reflect.ParameterizedType

        def typeArguments = genericType.actualTypeArguments
        assertEquals 'wrong number of type arguments', 1, typeArguments?.size()
        assertEquals 'type argument was of the wrong type', 'AssocTest', typeArguments[0].name
    }

    void testInjectHasManyAssociation() {
        def fs = File.separator
        def clz = gcl.parseClass('''
class IdTest {
    static hasMany = [others:AssocTest]
}
class AssocTest {}
''', "myapp${fs}grails-app${fs}domain${fs}IdTest.groovy")

        def obj = clz.newInstance()
        obj.id = 10
        obj.version = 2
        obj.others = [] as Set
        assertEquals 10, obj.id
        assertEquals 2, obj.version
        assertTrue obj.others instanceof Set
    }

    void testInjectBelongsToAssociation() {
        def fs = File.separator
        gcl.parseClass('''
class AssocTest {}
class IdTest {
    static belongsTo = [other:AssocTest]
}
''', "myapp${fs}grails-app${fs}domain${fs}IdTest.groovy")
        def clz = gcl.loadClass("IdTest")
        def obj = clz.newInstance()
        obj.id = 10
        obj.version = 2
        def assocTest = gcl.loadClass("AssocTest")
        obj.other = assocTest.newInstance()

        assertEquals 10, obj.id
        assertEquals 2, obj.version
        assertTrue assocTest.isInstance(obj.other)
    }

    void testSubclassProvidedIdWithDifferentType() {
        gcl.parseClass('''
class TheClass {
    String id
    String name
}
class TheSubClass extends TheClass {
    String secondName
}
''')

        def clz = gcl.loadClass("TheClass")
        def subClz = gcl.loadClass("TheSubClass")

        def obj = clz.newInstance()
        obj.id = "foo"
        obj.name = "bar"

        assertEquals "foo", obj.id
        assertEquals "bar", obj.name

        def sub  = subClz.newInstance()

        sub.id = "foo"
        sub.name = "bar"
        sub.secondName = "stuff"

        assertEquals "foo", sub.id
        assertEquals "bar", sub.name
        assertEquals "stuff", sub.secondName
    }
}

class AlwaysInjector extends DefaultGrailsDomainClassInjector {

    boolean shouldInject(URL url) { true }

    protected boolean isDomainClass(ClassNode classNode, SourceUnit sourceNode) { true  }

    protected boolean shouldInjectClass(ClassNode classNode) { true }
}
