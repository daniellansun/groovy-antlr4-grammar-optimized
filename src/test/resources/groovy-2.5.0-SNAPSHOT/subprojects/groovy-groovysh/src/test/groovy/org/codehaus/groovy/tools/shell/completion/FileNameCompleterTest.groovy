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
package org.codehaus.groovy.tools.shell.completion

import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import static org.junit.Assume.assumeFalse

@RunWith(JUnit4)
class FileNameCompleterTest extends GroovyTestCase {

    @Test
    void testRender() {
        assert FileNameCompleter.render('foo') == 'foo'
        assert FileNameCompleter.render('foo bar') == 'foo\\ bar'
        // intentionally adding empty String, to get better power assert output
        assert FileNameCompleter.render('foo \'\"bar') == 'foo\\ \\\'\\\"bar' + ''
    }

    @Test
    void testCompletionNoFiles() {
        // abusing junit testrule
        TemporaryFolder testFolder = null;
        try {
            testFolder = new TemporaryFolder();
            testFolder.create()

            FileNameCompleter completor = new FileNameCompleter() {
                @Override
                protected File getUserDir() {
                    return testFolder.getRoot()
                }
            }
            def candidates = []
            String buffer = ''
            assert 0 == completor.complete(buffer, 0, candidates)
            assert [] == candidates
        } finally {
            if (testFolder != null) {
                testFolder.delete()
            }
        }
    }

    @Test
    void testMatchFiles_Unix() {
        assumeFalse('Test requires unix like system.', System.getProperty('os.name').startsWith('Windows'))

        FileNameCompleter completer = new FileNameCompleter()
        List<String> candidates = []
        int resultIndex = completer.matchFiles('foo/bar', '/foo/bar', [new File('/foo/baroo'), new File('/foo/barbee')] as File[], candidates)
        assert resultIndex == 'foo/'.length()
        assert candidates == ['baroo ', 'barbee ']
    }
}
