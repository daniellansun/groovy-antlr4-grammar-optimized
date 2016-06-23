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
package json

class JsonBuilderTest extends GroovyTestCase {

    void testJsonBuilder() {
        assertScript """
            import groovy.json.*
            @Grapes([
                @Grab('com.google.code.gson:gson:2.3.1'), //required by json-unit
                @Grab('net.javacrumbs.json-unit:json-unit:1.5.5')])
            import net.javacrumbs.jsonunit.JsonAssert

            // tag::json_string[]
            String carRecords = '''
                {
                    "records": {
                    "car": {
                        "name": "HSV Maloo",
                        "make": "Holden",
                        "year": 2006,
                        "country": "Australia",
                        "record": {
                          "type": "speed",
                          "description": "production pickup truck with speed of 271kph"
                        }
                      }
                  }
                }
            '''
            // end::json_string[]
            
            // tag::json_builder[]
            JsonBuilder builder = new JsonBuilder()
            builder.records {
              car {
                    name 'HSV Maloo'
                    make 'Holden'
                    year 2006 
                    country 'Australia'
                    record {
                        type 'speed'
                        description 'production pickup truck with speed of 271kph'
                    }
              }
            }
            String json = JsonOutput.prettyPrint(builder.toString())
            // end::json_builder[]
            
            // tag::json_assert[]
            JsonAssert.assertJsonEquals(json, carRecords)
            // end::json_assert[]
       """
    }
}
