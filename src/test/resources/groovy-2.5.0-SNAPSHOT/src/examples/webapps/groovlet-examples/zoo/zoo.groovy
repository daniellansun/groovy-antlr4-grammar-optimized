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
println """
<html>
    <head>
        <title>Groovy Servlet Example - Visiting the zoo</title>
    </head>
    <body>
    <a href="../"><img src="../images/return.gif" width="24" height="24" border="0"></a><a href="../">Return</a>
    <p>
"""

Animal shark = new zoo.fish.Shark()
Animal trout = new zoo.fish.Trout()
Animal forelle = new zoo.HommingbergerGepardenforelle()

println """
     <p>Shark<br>
     ${shark.saySomething("\"Where is the trout?\"")}

     <p>Trout<br>
     ${trout.saySomething("Here is the trout!")}

     <p>Forelle<br>
     ${forelle.saySomething("\"<a href=\"http://www.hommingberger-forelle.de\">There is no spoon.</a>\"")}
     <!-- http://en.wikipedia.org/wiki/Nigritude_ultramarine -->
"""

println """
    </body>
</html>
"""
