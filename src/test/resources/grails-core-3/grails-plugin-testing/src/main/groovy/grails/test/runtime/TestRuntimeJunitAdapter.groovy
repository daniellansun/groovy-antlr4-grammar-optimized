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

package grails.test.runtime;

import groovy.transform.TypeChecked

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Internal class that's used as an adapter between JUnit @ClassRule / @Rule fields
 * and the TestRuntime system
 * 
 * @author Lari Hotari
 * @since 2.4.0
 *
 */
//@CompileStatic
@TypeChecked
class TestRuntimeJunitAdapter {

    public TestRule newRule(final Object testInstance) {
        return new TestRule() {
            Statement apply(Statement statement, Description description) {
                return new Statement() {
                    public void evaluate() throws Throwable {
                        Class testClass = description.getTestClass() ?: testInstance.getClass()
                        TestRuntime runtime = TestRuntimeFactory.getRuntimeForTestClass(testClass)
                        before(runtime, testInstance, description)
                        try {
                            statement.evaluate()
                        } catch (Throwable t) {
                            try {
                                after(runtime, testInstance, description, t)
                            } catch (Throwable t2) {
                                // ignore
                            } finally {
                                // throw original exception
                                throw t
                            }
                        }
                        after(runtime, testInstance, description, null)
                    }
                }
            }
        }
    }

    public TestRule newClassRule(final Class definedInTestClass) {
        return new TestRule() {
            Statement apply(Statement statement, Description description) {
                return new Statement() {
                    public void evaluate() throws Throwable {
                        Class testClass = description.getTestClass() ?: definedInTestClass
                        TestRuntime runtime = TestRuntimeFactory.getRuntimeForTestClass(testClass)
                        beforeClass(runtime, testClass, description)
                        try {
                            statement.evaluate()
                        } catch (Throwable t) {
                            try {
                                afterClass(runtime, testClass, description, t)
                            } catch (Throwable t2) {
                                // ignore
                            } finally {
                                // throw original exception
                                throw t
                            }
                        }
                        afterClass(runtime, testClass, description, null)
                    }
                }
            }
        }
    }
    
    public void setUp(Object testInstance) {
        TestRuntime runtime = TestRuntimeFactory.getRuntimeForTestClass(testInstance.getClass())
        beforeClass(runtime, testInstance.getClass(), Description.createSuiteDescription(testInstance.getClass()))
        before(runtime, testInstance, Description.createTestDescription(testInstance.getClass(), "setUp", testInstance.getClass().getAnnotations()))
    }
    
    public void tearDown(Object testInstance) {
        TestRuntime runtime = TestRuntimeFactory.getRuntimeForTestClass(testInstance.getClass())
        after(runtime, testInstance, Description.createTestDescription(testInstance.getClass(), "tearDown", testInstance.getClass().getAnnotations()), null)
        afterClass(runtime, testInstance.getClass(), Description.createSuiteDescription(testInstance.getClass()), null)
    }
    
    protected void before(TestRuntime runtime, Object testInstance, Description description) {
        def eventArguments = [testInstance: testInstance, description: description]
        handleFreshContextAnnotation(runtime, description, eventArguments)
        runtime.publishEvent("before", eventArguments, [immediateDelivery: true])
    }

    protected void after(TestRuntime runtime, Object testInstance, Description description, Throwable throwable) {
        runtime.publishEvent("after", [testInstance: testInstance, description: description, throwable: throwable], [immediateDelivery: true, reverseOrderDelivery: true])
        handleDirtiesRuntimeAnnotation(runtime, description, testInstance)
    }

    protected void beforeClass(TestRuntime runtime, Class testClass, Description description) {
        def eventArguments = [testClass: testClass, description: description]
        handleFreshContextAnnotation(runtime, description, eventArguments)
        runtime.publishEvent("beforeClass", eventArguments, [immediateDelivery: true])
    }

    protected void afterClass(TestRuntime runtime, Class testClass, Description description, Throwable throwable) {
        runtime.publishEvent("afterClass", [testClass: testClass, description: description, throwable: throwable], [immediateDelivery: true, reverseOrderDelivery: true])
        if(!runtime.shared) {
            runtime.requestClose()
        }
    }
    
    protected handleDirtiesRuntimeAnnotation(TestRuntime runtime, Description description, testInstance) {
        if(description?.getAnnotation(DirtiesRuntime)) {
            def eventArguments = [testClass: testInstance.getClass(), description: description]
            runtime.publishEvent('requestFreshRuntime', eventArguments, [immediateDelivery: true])
        }
    }
    
    protected handleFreshContextAnnotation(TestRuntime runtime, Description description, Map eventArguments) {
        if(doesRequireFreshContext(description)) {
            runtime.publishEvent('requestFreshRuntime', eventArguments, [immediateDelivery: true])
        }
    }

    protected boolean doesRequireFreshContext(Description testDescription) {
        if(testDescription?.getAnnotation(FreshRuntime) || testDescription?.getTestClass()?.isAnnotationPresent(FreshRuntime)) {
            return true
        }
        return false
    }
}
