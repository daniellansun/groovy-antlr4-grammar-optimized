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
package org.codehaus.groovy.util

class ListHashMapTest extends GroovyTestCase {
    ListHashMap list


    public void setUp() throws Exception {
        super.setUp();
        list = new ListHashMap(2)
    }

    public void testEmptyWhenCreated() {
        assert list.isEmpty()
        assert list.@maxListFill == 2
    }

    public void testInsertElement() {
        list.put("a", "a")
        assert list.size() == 1
        assert list.@innerMap == null
    }

    public void testInsertTwoElements() {
        list.put("a", "a")
        list.put("b", "b")
        assert list.size() == 2
        assert list.@innerMap == null
    }

    public void testInsertWithSameKey() {
        list.put("a", "a")
        list.put("a", "b")
        assert list.size() == 1
        assert list.@innerMap == null
        assert list.get("a") == "b"
    }

    public void testSwitchToInnerMap() {
        list.put("a", "a")
        list.put("b", "b")
        list.put("c", "c")
        assert list.size() == 3
        assert list.@innerMap != null
        assert list.@innerMap.size() == 3
    }

    public void testSwitchToInnerMapThenFallbackToList() {
        list.put("a", "a")
        list.put("b", "b")
        list.put("c", "c")
        assert list.size() == 3
        assert list.@innerMap != null
        assert list.@innerMap.size() == 3
        list.remove("c")
        assert list.size() == 2
        assert list.@innerMap == null
        assert list.keySet() == ['a','b'] as Set
    }

    public void testPutNullValue() {
        list.put("a", null)
        assert list.size() == 1
        assert list.a == null
    }

    public void testRemoveNullValue() {
        list.put("a", null)
        assert list.size() == 1
        assert list.a == null
        list.remove("a")
        assert list.size() == 0
        assert list.a == null
    }

    public void testPutAll() {
        list.putAll([a: '1', b: '2', c: '3'])
        assert list.size() == 3
        assert list.@innerMap != null
        assert list.@innerMap.size() == 3
        assert list.keySet() == ['a','b','c'] as Set
        assert list.values()  as Set == ['1','2','3'] as Set
    }

    public void testPutAllTwice() {
        list.putAll([a: '1', b: '2', c: '3'])
        list.putAll([a: '1', b: '2', c: '3'])
        assert list.size() == 3
        assert list.@innerMap != null
        assert list.@innerMap.size() == 3
        assert list.keySet()  == ['a','b','c'] as Set
        assert list.values() as Set  == ['1','2','3'] as Set
    }

    public void testRemoveAll() {
        list.putAll([a: '1', b: '2', c: '3'])
        assert list.size() == 3
        assert list.@innerMap != null
        assert list.@innerMap.size() == 3
        assert list.keySet() == ['a','b','c'] as Set
        assert list.values() as Set == ['1','2','3'] as Set
        list.remove('a')
        list.remove('b')
        list.remove('c')
        assert list.isEmpty()
        assert list.@innerMap == null
    }

    public void testRemoveFirstShiftsKeyValuesAndClearsArraySlot() {
        list.putAll([a: '1', b: '2'])
        assert list.size() == 2
        assert list.@innerMap == null
        list.remove('a')
        assert list.size() == 1
        assert list.@listKeys[0] == 'b'
        assert list.@listValues[0] == '2'
        assert list.@listKeys[1] == null
        assert list.@listValues[1] == null

        list.put('c', '3')
        assert list.size() == 2
        assert list.@listKeys[0] == 'b'
        assert list.@listValues[0] == '2'
        assert list.@listKeys[1] == 'c'
        assert list.@listValues[1] == '3'
    }

    public void testRemoveLastClearsLastArraySlot() {
        list.putAll([a: '1', b: '2'])
        assert list.size() == 2
        assert list.@innerMap == null
        list.remove('b')
        assert list.size() == 1
        assert list.@listKeys[0] == 'a'
        assert list.@listValues[0] == '1'
        assert list.@listKeys[1] == null
        assert list.@listValues[1] == null

        list.put('c', '3')
        assert list.size() == 2
        assert list.@listKeys[0] == 'a'
        assert list.@listValues[0] == '1'
        assert list.@listKeys[1] == 'c'
        assert list.@listValues[1] == '3'
    }

    public void testSwitchToInnerMapClearsArrays() {
        list.putAll([a: '1', b: '2'])
        assert list.size() == 2
        assert list.@innerMap == null
        assert list.@listKeys[0] == 'a'
        assert list.@listKeys[1] == 'b'

        list.put('c', '3')
        assert list.size() == 3
        assert list.@innerMap != null
        assert list.@listKeys[0] == null
        assert list.@listKeys[1] == null
        assert list.@listValues[0] == null
        assert list.@listValues[1] == null
    }

    public void testContainsKey() {
        list.putAll([a: '1', b: '2'])
        assert list.containsKey('b')
    }

    public void testContainsValue() {
        list.putAll([a: '1', b: '2'])
        assert list.containsValue('2')
    }
}
