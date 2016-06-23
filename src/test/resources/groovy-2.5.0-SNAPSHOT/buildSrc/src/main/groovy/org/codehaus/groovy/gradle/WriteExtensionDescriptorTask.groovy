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
package org.codehaus.groovy.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile

/**
 * A Gradle task to generate module descriptor files for Groovy extension modules.
 *
 * @author Cedric Champeau
 */
class WriteExtensionDescriptorTask extends DefaultTask {
    String description = 'Generates the org.codehaus.groovy.runtime.ExtensionModule descriptor file of a module'
    @Input String extensionClasses = ''
    @Input String staticExtensionClasses = ''
    @OutputFile File descriptor = computeDescriptorFile()


    private File computeDescriptorFile() {
        def metaInfDir = new File("${project.buildDir}/classes/main/META-INF/services")
        return new File(metaInfDir, "org.codehaus.groovy.runtime.ExtensionModule")
    }

    @TaskAction
    def writeDescriptor() {
        descriptor.parentFile.mkdirs()
        descriptor.withWriter {
            it << """moduleName=${project.name}
moduleVersion=${project.version}
extensionClasses=${extensionClasses}
staticExtensionClasses=${staticExtensionClasses}"""
        }
    }

}

