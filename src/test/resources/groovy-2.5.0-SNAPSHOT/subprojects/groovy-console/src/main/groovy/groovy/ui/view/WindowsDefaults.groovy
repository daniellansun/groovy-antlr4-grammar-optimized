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
package groovy.ui.view

import javax.swing.JComponent
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
import org.codehaus.groovy.runtime.InvokerHelper

import java.util.prefs.Preferences

build(Defaults)

def prefs = Preferences.userNodeForPackage(groovy.ui.Console)
def fontFamily = prefs.get("fontName", "Consolas")

// change fonts for vista
if (System.properties['os.version'] =~ /6\./) {
    // Vista/Server 2008 or later
    styles.regular[StyleConstants.FontFamily] = fontFamily
    styles[StyleContext.DEFAULT_STYLE][StyleConstants.FontFamily] = fontFamily
}
