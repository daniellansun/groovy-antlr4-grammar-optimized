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
package org.codehaus.groovy.transform

/**
 * @author Danno.Ferrin
 * @author Alex Tkachman
 */
class GlobalTransformTest extends GroovyShellTestCase {

    URL transformRoot = new File(getClass().classLoader.
            getResource("org/codehaus/groovy/transform/META-INF/services/org.codehaus.groovy.transform.ASTTransformation").
            toURI()).parentFile.parentFile.parentFile.toURL()

    void testGlobalTransform() {
        shell.classLoader.addURL(transformRoot)
        shell.evaluate("""
            import org.codehaus.groovy.control.CompilePhase

            if (org.codehaus.groovy.transform.TestTransform.phases == [CompilePhase.CONVERSION, CompilePhase.CLASS_GENERATION]) {
               println "Phase sync bug fixed"
            } else if (org.codehaus.groovy.transform.TestTransform.phases == [CompilePhase.CONVERSION, CompilePhase.INSTRUCTION_SELECTION]) {
               println "Phase sync bug still present"
            } else {
               assert false, "FAIL"
            }
        """)
    }
}