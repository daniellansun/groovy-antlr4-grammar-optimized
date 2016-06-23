/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package groovy
// TODO: GROOVY-435

class SingletonBugTest extends GroovyTestCase {

    public void testPrivate() {
        def x = SingletonBugPrivate.getInstance()
        def y = SingletonBugPrivate.getInstance()
        println "Get one private instance: $x"
        println "Get another private instance: $y"
        assert x == y

         println(SingletonBugPrivateSecond.getInstanceSecond())
         println(SingletonBugPrivateSecond.doTestSecond())
        // shouldFail { println(SingletonBugPrivateSecond.getInstanceSecond()) }
        // shouldFail { println(SingletonBugPrivateSecond.doTestSecond()) }
    }

    public void testProtected() {
        def x = SingletonBugProtected.getInstance()
        def y = SingletonBugProtected.getInstance()
        println "Get one protected instance: $x"
        println "Get another protected instance: $y"
        assert x == y

        println(SingletonBugProtectedSecond.getInstanceSecond())
        println(SingletonBugProtectedSecond.doTestSecond())
        x = SingletonBugProtectedSecond.getInstanceSecond()
        y = SingletonBugProtectedSecond.doTestSecond()
        assert x != y
    }

}


class SingletonBugPrivate {

    private static SingletonBugPrivate instance1 = null

    private SingletonBugPrivate() {
    }
    
    static SingletonBugPrivate getInstance() {
        if (instance1 == null)
            instance1 = new SingletonBugPrivate()
        return instance1
    }
    
    // private static SingletonBugPrivate getInstance2() {
    //     if (instance1 == null)
    //         instance1 = new SingletonBugPrivate()
    //     return instance1
    // }
}


class SingletonBugProtected {

    private static SingletonBugProtected instance1 = null

    protected SingletonBugProtected() {
    }
    
    static SingletonBugProtected getInstance() {
        if (instance1 == null)
            instance1 = new SingletonBugProtected()
        return instance1
    }
    
    // private static SingletonBugProtected getInstance2() {
    //     if (instance1 == null)
    //         instance1 = new SingletonBugProtected()
    //     return instance1
    // }
}


class SingletonBugPrivateSecond extends SingletonBugPrivate {

    static SingletonBugPrivate getInstanceSecond() {
        return doTestSecond()
    }

    static SingletonBugPrivate doTestSecond() {
        return new SingletonBugPrivate()
    }
}


class SingletonBugProtectedSecond extends SingletonBugProtected {

    static SingletonBugProtected getInstanceSecond() {
        return doTestSecond()
    }

    static SingletonBugProtected doTestSecond() {
        return new SingletonBugProtected()
    }
}
