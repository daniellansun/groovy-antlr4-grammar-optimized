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

class ImportTest extends GroovyTestCase {

    void testImportAll() {
        def file = new File("foo.txt")
        assert file instanceof File
        assert file.getClass().name == "java.io.File"
    }
    
    void testImportByName() {
        def x = [:]
        assert x instanceof Map
        /**
         * For maps, map.getClass() should be used instead of map.class,
         * when map has no member, named as "class"
         */
        assert x.getClass() != null
        assert x.getClass().name.startsWith("java.util.")
        
        def y = [1, 2, 3]
        assert y instanceof List
        assert y.getClass().name.startsWith("java.util.")
    }
}
