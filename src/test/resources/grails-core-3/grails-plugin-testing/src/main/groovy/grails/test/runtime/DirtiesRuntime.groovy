/*
 * Copyright 2014 the original author or authors.
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
package grails.test.runtime

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.codehaus.groovy.transform.GroovyASTTransformationClass

/**
 * Annotation to be used in Junit tests and Spock specifications.  Marking
 * a test method with this annotation alerts the runtime that the test method
 * will cause the runtime to be modified in ways that might have unwanted
 * side effects on other tests. Test methods annotated with this annotation 
 * will cause a fresh TestRuntime instance to be created after the test runs.
 *  
 * @author Jeff Brown
 * @since 2.4.4
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@GroovyASTTransformationClass('org.grails.compiler.injection.test.DirtiesRuntimeTransformation')
public @interface DirtiesRuntime {
}
