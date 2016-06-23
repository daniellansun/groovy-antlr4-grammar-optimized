/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grails.web.binding

import org.grails.web.databinding.DataBindingLazyMetaPropertyMap;

class DataBindingLazyMetaPropertyMapTests extends GroovyTestCase {

    void testDataBindingWithSubmap() {
       def map = new DataBindingLazyMetaPropertyMap(new PropertyMapTest(name:"Bart", age:11, other:"stuff"))

        map['name', 'age'] = [name:"Homer", age:"45", other:"changed"]

        assertEquals 45,map.age
        assertEquals "Homer", map.name
        assertEquals "stuff", map.other
    }
}

class PropertyMapTest {
    String name
    Integer age
    String other
}
