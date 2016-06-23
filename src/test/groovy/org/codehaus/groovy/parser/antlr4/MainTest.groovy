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
package org.codehaus.groovy.parser.antlr4

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.parser.antlr4.util.ASTComparatorCategory
import org.codehaus.groovy.parser.antlr4.util.ASTWriter
import org.codehaus.groovy.syntax.Token
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Logger

class MainTest extends Specification {
    private Logger log = Logger.getLogger(MainTest.class.getName());
    public static final String DEFAULT_RESOURCES_PATH = 'subprojects/groovy-antlr4-grammar/src/test/resources';
    public static final String RESOURCES_PATH = new File(DEFAULT_RESOURCES_PATH).exists() ? DEFAULT_RESOURCES_PATH : 'src/test/resources';



	@Unroll
    def "test ast builder for #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }

        where:
        path | config
        "Annotations_Issue30_1.groovy" | _
        "Annotations_Issue30_2.groovy" | _
        "ArrayType_Issue44_1.groovy" | _
        "AssignmentOps_Issue23_1.groovy" | _
        "ClassConstructorBug_Issue13_1.groovy" | _
        "ClassInitializers_Issue_20_1.groovy" | _
        "ClassMembers_Issue3_1.groovy" | _
        "ClassMembers_Issue3_2.groovy" | _
        "ClassModifiers_Issue_2.groovy" | _
        "ClassProperty_Issue4_1.groovy" | _
        "Closure_Issue21_1.groovy" | addIgnore(ExpressionStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "Enums_Issue43_1.groovy" | _
        "ExceptionHandling_Issue27_1.groovy" | _
        "ExplicitConstructor.groovy" | _
        "Extendsimplements_Issue25_1.groovy" | _
        "FieldAccessAndMethodCalls_Issue37_1.groovy" | _
        'FieldAccessAndMethodCalls_Issue37_2.groovy' | addIgnore(ExpressionStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "FieldInitializersAndDefaultMethods_Issue49_1.groovy" | _
        "Generics_Issue26_1.groovy" | addIgnore(GenericsType, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "GStrings_Issue41_1.groovy" | _
        "ImportRecognition_Issue6_1.groovy" | _
        "ImportRecognition_Issue6_2.groovy" | _
        "InnerClasses_Issue48_1.groovy" | _
        "ListsAndMaps_Issue22_1.groovy" | _
        "Literals_Numbers_Issue36_1.groovy" | _
        'Literals_Other_Issue36_4.groovy' | _
        "Literals_HexOctNumbers_Issue36_2.groovy" | _
        "Literals_Strings_Issue36_3.groovy" | addIgnore(ExpressionStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "MapParameters_Issue55.groovy" | addIgnore(ExpressionStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "MemberAccess_Issue14_1.groovy" | _
        "MethodBody_Issue7_1.groovy" | _
        "MethodCall_Issue15_1.groovy" | addIgnore(ExpressionStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "New_Issue47_1.groovy" | _
        "Operators_Issue9_1.groovy" | _
        "Binary_and_Unary_Operators.groovy" | _
        "ParenthesisExpression_Issue24_1.groovy" | _
        "Script_Issue50_1.groovy" | addIgnore(ExpressionStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "Statements_Issue17_1.groovy" | addIgnore([IfStatement, ExpressionStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "Statements_Issue58_1.groovy" | addIgnore([IfStatement, ForStatement, ExpressionStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "SubscriptOperator.groovy" | _
        "AnnotationDeclaration.groovy" | _
        "TernaryAndElvis_Issue57.groovy" | _
        "TernaryAndElvis_01.groovy" | _
        "TestClass1.groovy" | _
        "ThrowDeclarations_Issue_28_1.groovy" | _
        "Assert_Statements.groovy" | _
        "Unicode_Identifiers.groovy" | _
        "ClassMembers_String_Method_Name.groovy" | _
        "ScriptPart_String_Method_Name.groovy" | _
        "Multiline_GString.groovy" | _
        "Unescape_String_Literals_Issue7.groovy" | _
        "GString-closure-and-expression_issue12.groovy" | _
        "Slashy_Strings.groovy" | _
        "Expression_Precedence.groovy" | _
        "Expression_Span_Rows.groovy" | _
        "Tuples_issue13.groovy" | _
        "Dollar_Slashy_Strings.groovy" | _
        "Dollar_Slashy_GStrings.groovy" | _
        "SyntheticPublic_issue19.groovy" | _
        "Traits_issue21.groovy" | _
        "EmptyScript.groovy" | _
        "SemiColonScript.groovy" | _
        "Enums_issue31.groovy" | _
        "CallExpression_issue33_1.groovy" | _
        "CallExpression_issue33_2.groovy" | addIgnore(Parameter, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "CallExpression_issue33_3.groovy" | addIgnore([Parameter, IfStatement, ExpressionStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "CallExpression_issue33_4.groovy" | _
        "CallExpression_issue33_6.groovy" | _
        "Closure_Call_Issue40.groovy" | _
        "CommandExpression_issue41.groovy" | _
        "SynchronizedStatement.groovy" | _
        "VarArg.groovy" | _
        "Join_Line_Escape_issue46.groovy" | _
        "Enums_Inner.groovy" | _
        "Interface.groovy" | _
        "ClassMembers_Issue3_3.groovy" | _
        "FieldAccess_1.groovy" | _
        "BreakAndContinue.groovy" | _
        "ClassConstants.groovy" | _
        "Switch-Case_issue36.groovy" | addIgnore(CaseStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "ScriptSupport.groovy" | addIgnore([FieldNode, PropertyNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)

    }



    @Unroll
    def "test groovy part1 for #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/groovy-2.5.0-SNAPSHOT/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }

        where:
        path | config
        "benchmark/bench.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/ackermann.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/ary.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/binarytrees.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/fannkuch.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/fibo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "benchmark/bench/heapsort.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/hello.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/mandelbrot.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/nsieve.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/random.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/recursive.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/regexdna.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/revcomp.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/spectralnorm.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/threadring.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "benchmark/bench/wordfreq.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "buildSrc/src/main/groovy/org/codehaus/groovy/gradle/WriteExtensionDescriptorTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "config/binarycompatibility/binarycompat-report.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "config/checkstyle/checkstyle-report.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "config/codenarc/codenarc.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/astbuilder/Main.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/astbuilder/MainExample.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/astbuilder/MainIntegrationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/astbuilder/MainTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/commandLineTools/AntMap.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/commandLineTools/BigTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/commandLineTools/ListFiles.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/commandLineTools/Reflections.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/commandLineTools/SimpleWebServer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/examples/console/MortgageCalculator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/console/knowYourTables.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/console/thinkOfANumber.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovy2d/paintingByNumbers.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovyShell/ArithmeticShell.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovyShell/ArithmeticShellTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovyShell/BlacklistingShell.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovyShell/BlacklistingShellTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovy/j2ee/CreateData.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovy/model/MvcDemo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovy/swing/SwingDemo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovy/swing/TableDemo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/groovy/swing/TableLayoutDemo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/osgi/hello-groovy-bundle/org/codehaus/groovy/osgi/Activator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/osgi/hello-groovy-bundle/org/codehaus/groovy/osgi/GroovyGreeter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/osgi/hello-groovy-bundle/org/codehaus/groovy/osgi/GroovyGreeterImpl.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/osgi/hello-groovy-test-harness/org/codehaus/groovy/osgi/harness/HarnessActivator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/searchEngine/Indexer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/searchEngine/Searcher.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/BindingExample.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/BloglinesClient.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/examples/swing/ModelNodeExample.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/RegexCoach.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/RegexCoachController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/RegexCoachView.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/Widgets.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/examples/swing/binding/caricature/Caricature.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/greet/Greet.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/examples/swing/greet/TwitterAPI.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/examples/swing/greet/View.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/timelog/TimeLogMain.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/swing/timelog/TimeLogModel.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/examples/swing/timelog/TimeLogView.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/global/CompiledAtASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/global/CompiledAtExample.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/global/CompiledAtIntegrationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/global/LoggingASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/global/LoggingExample.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/local/LoggingASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/local/LoggingExample.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/transforms/local/WithLogging.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/WEB-INF/groovy/Animal.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/WEB-INF/groovy/zoo/Fish.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/WEB-INF/groovy/zoo/fish/Shark.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/WEB-INF/groovy/zoo/fish/Trout.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/hello/hello.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/index.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/zoo/HommingbergerGepardenforelle.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/zoo/visit.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/examples/webapps/groovlet-examples/zoo/zoo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/beans/ListenerList.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/beans/ListenerListASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/cli/CliBuilderException.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/cli/OptionField.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/cli/UnparsedField.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/main/groovy/grape/GrapeIvy.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/AutoExternalize.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/Canonical.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/CompileDynamic.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/ConditionalInterrupt.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/TailRecursive.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/ThreadInterrupt.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/transform/TimedInterrupt.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/util/CliBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/util/ConfigSlurper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/util/FileNameByRegexFinder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/groovy/util/FileTreeBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/ast/builder/AstBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/ast/builder/AstSpecificationCompiler.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/ast/builder/AstStringCompiler.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/classgen/genArrayAccess.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/classgen/genArrays.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/classgen/genDgmMath.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/classgen/genMathModification.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/control/customizers/ASTTransformationCustomizer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/control/customizers/builder/ASTTransformationCustomizerFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/control/customizers/builder/CompilerCustomizationBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/tools/GrapeMain.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/main/org/codehaus/groovy/tools/ast/TransformTestHelper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/ASTTestTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/ConditionalInterruptibleASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/ThreadInterruptibleASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/main/org/codehaus/groovy/transform/TimedInterruptibleASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/AstHelper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/CollectRecursiveCalls.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/HasRecursiveCalls.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/InWhileLoopWrapper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/RecursivenessTester.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/ReturnAdderForClosures.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/ReturnStatementToIterationConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/StatementReplacer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/TailRecursiveASTTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/TernaryToIfStatementConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/VariableAccessReplacer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/VariableExpressionReplacer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/transform/tailrec/VariableExpressionTransformer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/main/org/codehaus/groovy/util/StringUtil.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/aftermethodcall.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/aftervisitclass.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/aftervisitmethod.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/ambiguousmethods.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/beforemethodcall.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/beforevisitclass.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/beforevisitmethod.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/finish.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/incompatibleassignment.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/methodnotfound.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/newmethod.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/onmethodselection.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/reloading/dependency1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/reloading/dependency2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/reloading/source1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/reloading/source2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/reloading/source3.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/robotextension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/spec/test-resources/robotextension2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/robotextension3.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/scoping.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/scoping_alt.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/selfcheck.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/setup.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/unresolvedattribute.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/unresolvedproperty.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test-resources/unresolvedvariable.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/BaseScriptSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/ClassDesignASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/ClassTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/CloningASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/spec/test/ClosuresSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/CodeGenerationASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/CoercionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/CommandChainsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/CompilerDirectivesASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/CustomizersTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/DeclarativeConcurrencyASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/DelegatesToSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/DesignPatternsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/DifferencesFromJavaTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/ExtensionModuleSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/IntegrationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/LogImprovementsASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/spec/test/OperatorsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/PackageTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/PrimitiveTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/SaferScriptingASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/ScriptsAndClassesSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/SemanticsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/SwingASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/spec/test/SyntaxTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/TestingASTTransformsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/TraitsSpecificationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/asciidoctor/Utils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/builder/CliBuilderTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/builder/FileTreeBuilderTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/builder/NodeBuilderTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/builder/ObjectGraphBuilderTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/gdk/ConfigSlurperTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/gdk/ExpandoTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/gdk/ObservableTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/gdk/WorkingWithCollectionsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/gdk/WorkingWithIOSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/ASTXFormSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/CategoryTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/ExpandoMetaClassTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/GroovyObjectTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/InterceptableTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/InterceptionThroughMetaClassTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/MethodPropertyMissingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/metaprogramming/MyTransformToDebug.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/objectorientation/MethodsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/semantics/GPathTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/semantics/LabelsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/semantics/OptionalityTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/semantics/PowerAssertTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/semantics/TheGroovyTruthTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/support/MaxRetriesExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/support/StaticStringExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/testingguide/GDKMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/testingguide/GroovyTestCaseExampleTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/testingguide/JUnit4ExampleTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/testingguide/MockingExampleTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/OptionalTypingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/PrecompiledExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/Robot.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/StaticCompilationIntroTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/TypeCheckingExtensionSpecTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/TypeCheckingHintsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/spec/test/typing/TypeCheckingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/tck/src/org/codehaus/groovy/tck/BatchGenerate.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/tck/src/org/codehaus/groovy/tck/TestGenerator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/tck/test/gls/ch03/s01/Unicode1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/tck/test/gls/ch03/s01/Unicode2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/tck/test/gls/ch03/s02/LexicalTranslation1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/tck/test/gls/ch03/s02/Longest1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/tck/test/gls/ch03/s03/UnicodeEscapes1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/tck/test/gls/ch03/s03/UnicodeEscapes2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/sc/MixedMode.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/sc/MixedMode2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/sc/MixedModeDynamicBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/AmbiguousMethods.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/AnnotatedByTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/ArgumentsTestingTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/BeforeAfterClassTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/BinaryOperatorTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/DelegatesToTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/FinishTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/FirstArgumentsTestingTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/Groovy6047Extension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/IncompatibleAssignmentTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/IncompatibleReturnTypeTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/MissingMethod1TestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/MissingMethod2TestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/NewMethodAndIsGeneratedTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/NthArgumentTestingTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/OnMethodSelectionTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test-resources/groovy/transform/stc/PrefixChangerTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/RobotMove.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/ScopeEnterExitTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/SetupTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/SilentTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/SprintfExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UndefinedVariableNoHandleTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UndefinedVariableTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UnresolvedAttributeTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UnresolvedPropertyTestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UpperCaseMethodTest1Extension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UpperCaseMethodTest2Extension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/groovy/transform/stc/UpperCaseMethodTest3Extension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/stubgenerator/circularLanguageReference/Rectangle.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test-resources/stubgenerator/propertyUsageFromJava/somepackage/GroovyPogo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/MainJavadocAssertionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/Outer3.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/Outer4.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/CompilableTestSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/annotations/AnnotationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/annotations/XmlEnum.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/annotations/XmlEnumValue.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/annotations/closures/AnnotationClosureExhaustiveTestSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/annotations/closures/AnnotationClosureOwnerCallTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/annotations/closures/AnnotationClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/annotations/closures/AnnotationClosureThisObjectCallTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/annotations/closures/AnnotationClosureUnqualifiedCallTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/annotations/closures/AnnotationClosureWithNonLocalVariable.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/annotations/closures/AnnotationClosureWithParametersTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/annotations/closures/JavaCompatibility.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/annotations/closures/JavaCompatibilityParameterized.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/ch06/s05/GName1Test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/ch08/s04/FormalParameterTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/ch08/s04/RepetitiveMethodTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/enums/EnumTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/generics/GenericsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/innerClass/InnerClassTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/innerClass/InnerInterfaceTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/ClassDuplicationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/ClosureDelegationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/ConstructorDelegationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/CovariantReturnTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/DefaultParamTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/GroovyObjectInheritanceTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/MethodDeclarationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/MethodSelectionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/invocation/StaticMethodInvocationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/property/MetaClassOverridingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/scope/BlockScopeVisibilityTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/scope/ClassVariableHidingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/scope/FinalAccessTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/scope/MultipleDefinitionOfSameVariableTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/scope/NameResolvingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/scope/StaticScopeTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/scope/VariablePrecedenceTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/sizelimits/StringSizeTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/statements/DeclarationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/statements/MultipleAssignmentDeclarationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/statements/MultipleAssignmentTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/statements/ReturnTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/AssertTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/BinaryLiteralTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/Gep3OrderDslTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/gls/syntax/Gep3Test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/MethodCallValidationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/NumberLiteralTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/OldClosureSyntaxRemovalTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/OldPropertySyntaxRemovalTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/OldSpreadTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/ParsingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/syntax/UnderscoreInNumbersTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/types/BooleanExpressionConversionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/types/GroovyCastTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/gls/types/OperationsResultTypeTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/AbstractClassAndInterfaceTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ActorTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/AmbiguousInvocationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ArrayAutoboxingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ArrayCoerceTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ArrayParamMethodTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ArrayTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ArrayTypeTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/AsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/AssertNumberTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/AssertTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/Bar.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/Base64Test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/BinaryStreamsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/BindingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/BitSetTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/BreakContinueLabelTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CallInnerClassCtorTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CastTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CategoryTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ChainedAssignmentTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClassExpressionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClassLoaderBug.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClassTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureAsParamTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureCloneTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureComparatorTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureComposeTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureCurryTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureDefaultParameterTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureInClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureInStaticMethodTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureMethodCallTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureMethodTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureMethodsOnFileTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureMissingMethodTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureReturnTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureReturnWithoutReturnStatementTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureSugarTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/ClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureUsingOuterVariablesTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureWithDefaultParamTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ClosureWithEmptyParametersTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CollateTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CompareEqualsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CompareToTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CompareTypesTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CompileOrderTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/CompilerErrorTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/Constructor2Test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ConstructorTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/CurlyBracketLayoutTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DateTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DefaultParamClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DoWhileLoopTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DollarEscapingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DownUpStepTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DummyMethodsGroovy.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/DynamicMemberTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/EqualsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME HANG        "src/test/groovy/EscapedUnicodeTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ExceptionInClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ExpandoPropertyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/FileTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/FilterLineTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/FinallyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/Foo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/ForLoopTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ForLoopWithLocalVariablesTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/GStringTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/GeneratorTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/GlobalPrintlnTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/GroovyCharSequenceMethodsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/GroovyClosureMethodsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/GroovyInterceptableTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/GroovyMethodsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/GroovyTruthTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/HeredocsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/HexTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/HomepageTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/IdentityClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/IfElseCompactTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/IfElseTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/IfPropertyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/IfTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/IfWithMethodCallTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ImmutableModificationTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ImportTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/InstanceofTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/InterfaceTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/InvokeNormalMethodsFirstTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/JointGroovy.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/KeywordsInPropertyNamesTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LeftShiftTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ListIteratingTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ListTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LiteralTypesTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LittleClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LocalFieldTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LocalPropertyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LocalVariableTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LogicTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/LoopBreakTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MapConstructionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MapPropertyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MapTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MethodCallTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MethodCallWithoutParenthesisTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MethodInBadPositionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MethodParameterAccessWithinClosureTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MinMaxTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MinusEqualsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ModifiersTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/ModuloTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/MultiCatchTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/MultiDimArraysTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MultilineChainExpressionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MultilineStringTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/MultiplyDivideEqualsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/NamedParameterTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/NestedClassTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "src/test/groovy/NewExpressionTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/NoPackageTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/NullPropertyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/NumberMathTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/OptionalReturnTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/OverloadInvokeMethodTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/OverridePropertyGetterTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/OverrideTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/PlusEqualsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/PostfixTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/PrefixTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/PrimitiveArraysTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/PrimitiveDefaultValueTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "src/test/groovy/PrimitiveTypeFieldTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)

    }




    @Unroll
    def "test grails-core-3 part1 for #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/grails-core-3/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }

        where:
        path | config
        "buildSrc/src/main/groovy/org/grails/gradle/GrailsBuildPlugin.groovy" | _

        "grails-async/src/main/groovy/grails/async/DelegateAsync.groovy" | _
        "grails-async/src/main/groovy/grails/async/Promise.groovy" | _
        "grails-async/src/main/groovy/grails/async/PromiseFactory.groovy" | _
        "grails-async/src/main/groovy/grails/async/PromiseList.groovy" | _
        "grails-async/src/main/groovy/grails/async/PromiseMap.groovy" | _
        "grails-async/src/main/groovy/grails/async/Promises.groovy" | _
        "grails-async/src/main/groovy/grails/async/decorator/PromiseDecorator.groovy" | _
        "grails-async/src/main/groovy/grails/async/decorator/PromiseDecoratorLookupStrategy.groovy" | _
        "grails-async/src/main/groovy/grails/async/decorator/PromiseDecoratorProvider.groovy" | _
        "grails-async/src/main/groovy/grails/async/factory/AbstractPromiseFactory.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/decorator/PromiseDecorator.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/decorator/PromiseDecoratorLookupStrategy.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/decorator/PromiseDecoratorProvider.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/AbstractPromiseFactory.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/BoundPromise.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/SynchronousPromise.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/SynchronousPromiseFactory.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/gpars/GparsPromise.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/gpars/GparsPromiseFactory.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/gpars/LoggingPoolFactory.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/reactor/ReactorPromise.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/factory/reactor/ReactorPromiseFactory.groovy" | _
        "grails-async/src/main/groovy/org/grails/async/transform/internal/DelegateAsyncUtils.groovy" | _
        "grails-async/src/test/groovy/grails/async/DelegateAsyncSpec.groovy" | _
        "grails-async/src/test/groovy/grails/async/PromiseListSpec.groovy" | addIgnore(ThrowStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-async/src/test/groovy/grails/async/PromiseMapSpec.groovy" | addIgnore(ThrowStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-async/src/test/groovy/grails/async/PromiseSpec.groovy" | _
        "grails-async/src/test/groovy/grails/async/ReactorPromiseFactorySpec.groovy" | _
        "grails-async/src/test/groovy/grails/async/SynchronousPromiseFactorySpec.groovy" | _

//        "grails-bootstrap/src/main/groovy/grails/build/proxy/SystemPropertiesAuthenticator.groovy" | _
        "grails-bootstrap/SystemPropertiesAuthenticator.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/codegen/model/Model.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/codegen/model/ModelBuilder.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/config/ConfigMap.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/io/IOUtils.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/io/ResourceUtils.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/io/support/SystemOutErrCapturer.groovy" | addIgnore(MethodNode, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-bootstrap/src/main/groovy/grails/io/support/SystemStreamsRedirector.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/plugins/GrailsVersionUtils.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/plugins/VersionComparator.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/util/BuildSettings.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/util/CosineSimilarity.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/util/Described.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/util/Metadata.groovy" | _
        "grails-bootstrap/src/main/groovy/grails/util/Named.groovy" | _
        "grails-bootstrap/src/main/groovy/org/codehaus/groovy/grails/io/support/GrailsResourceUtils.groovy" | _
        "grails-bootstrap/src/main/groovy/org/codehaus/groovy/grails/io/support/Resource.groovy" | _
        "grails-bootstrap/src/main/groovy/org/codehaus/groovy/grails/plugins/GrailsPluginInfo.groovy" | _
//        "grails-bootstrap/src/main/groovy/org/grails/build/parsing/ScriptNameResolver.groovy" | _
        "grails-bootstrap/ScriptNameResolver.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/config/CodeGenConfig.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/config/NavigableMap.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/exceptions/ExceptionUtils.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/exceptions/reporting/CodeSnippetPrinter.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/exceptions/reporting/DefaultStackTracePrinter.groovy" | addIgnore(ReturnStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-bootstrap/src/main/groovy/org/grails/exceptions/reporting/StackTracePrinter.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/io/support/ByteArrayResource.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/io/support/DevNullPrintStream.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/io/support/FactoriesLoaderSupport.groovy" | _
        "grails-bootstrap/src/main/groovy/org/grails/io/support/MainClassFinder.groovy" | addIgnore(ReturnStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-bootstrap/src/main/groovy/org/grails/io/watch/FileExtensionFileChangeListener.groovy" | _
//        "grails-bootstrap/src/test/groovy/grails/build/logging/GrailsConsoleSpec.groovy" | _
        "grails-bootstrap/GrailsConsoleSpec.groovy" | _
        "grails-bootstrap/src/test/groovy/grails/config/ConfigMapSpec.groovy" | _
        "grails-bootstrap/src/test/groovy/grails/config/GrailsConfigSpec.groovy" | _
        "grails-bootstrap/src/test/groovy/grails/io/IOUtilsSpec.groovy" | _
        "grails-bootstrap/src/test/groovy/grails/util/EnvironmentTests.groovy" | _
        "grails-bootstrap/src/test/groovy/org/codehaus/groovy/grails/cli/parsing/CommandLineParserSpec.groovy" | _

        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/Base64CodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/DigestUtils.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/HexCodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/MD5BytesCodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/MD5CodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/SHA1BytesCodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/SHA1CodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/SHA256BytesCodecExtensionMethods.groovy" | _
        "grails-codecs/src/main/groovy/org/grails/plugins/codecs/SHA256CodecExtensionMethods.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/Base64CodecTests.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/HexCodecTests.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/MD5BytesCodecTests.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/MD5CodecTests.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/SHA1BytesCodecTests.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/SHA1CodecTests.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/SHA256BytesCodec.groovy" | _
        "grails-codecs/src/test/groovy/org/grails/web/codecs/SHA256CodecTests.groovy" | _

        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/beans/factory/GenericBeanFactoryAccessor.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/AbstractGrailsClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/AbstractInjectableGrailsClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/AnnotationDomainClassArtefactHandler.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/ArtefactHandlerAdapter.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/BootstrapArtefactHandler.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/ClassPropertyFetcher.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/ControllerArtefactHandler.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsApplication.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsBootstrapClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsControllerClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsDomainClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsDomainClassProperty.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsServiceClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsTagLibClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/DefaultGrailsUrlMappingsClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/ExternalGrailsDomainClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsBootstrapClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/InjectableGrailsClass.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/cfg/GrailsConfig.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/spring/DefaultRuntimeSpringConfiguration.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/spring/GrailsRuntimeConfigurator.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/spring/GrailsWebApplicationContext.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/commons/spring/RuntimeSpringConfiguration.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/AbstractArtefactTypeAstTransformation.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/AllArtefactClassInjector.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/AnnotatedClassInjector.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/ClassInjector.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/DefaultGrailsDomainClassInjector.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/EntityASTTransformation.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/GrailsAwareClassLoader.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/GrailsAwareInjectionOperation.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/core/io/DefaultResourceLocator.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/domain/GrailsDomainClassCleaner.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/exceptions/GrailsConfigurationException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/exceptions/GrailsDomainException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/exceptions/GrailsException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/exceptions/GrailsRuntimeException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/io/support/GrailsIOUtils.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/io/support/IOUtils.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/lifecycle/ShutdownOperations.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/orm/support/TransactionManagerAware.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/plugins/AbstractGrailsPluginManager.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/plugins/DefaultGrailsPluginManager.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/plugins/DomainClassPluginSupport.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/plugins/GrailsVersionUtils.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/plugins/support/aware/ClassLoaderAware.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/plugins/support/aware/GrailsConfigurationAware.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/support/ClassEditor.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/support/PersistenceContextInterceptor.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/support/SoftThreadLocalMap.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/support/proxy/EntityProxyHandler.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/support/proxy/ProxyHandler.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/validation/AbstractVetoingConstraint.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/validation/DefaultConstraintEvaluator.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/validation/GrailsDomainClassValidator.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/validation/VetoingConstraint.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/validation/exceptions/ConstraintException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/binding/GrailsWebDataBinder.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/context/GrailsConfigUtils.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/context/ServletContextHolder.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/errors/GrailsExceptionResolver.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/mapping/CachingLinkGenerator.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/metaclass/ForwardMethod.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/metaclass/RenderDynamicMethod.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/DelegatingApplicationAttributes.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/GrailsFlashScope.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/GrailsUrlPathHelper.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/HttpHeaders.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/WrappedResponseHolder.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/GrailsDispatcherServlet.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/GrailsHttpSession.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/GrailsParameterMap.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/GrailsWebRequest.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/RedirectEventListener.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/exceptions/ControllerExecutionException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/servlet/mvc/exceptions/GrailsMVCException.groovy" | _
        "grails-compat/src/main/groovy/org/codehaus/groovy/grails/web/util/WebUtils.groovy" | _
        "grails-compat/src/main/groovy/org/grails/databinding/SimpleDataBinder.groovy" | _
        "grails-compat/src/main/groovy/org/grails/databinding/SimpleMapDataBindingSource.groovy" | _

        "grails-console/src/main/groovy/grails/ui/command/GrailsApplicationContextCommandRunner.groovy" | _
        "grails-console/src/main/groovy/grails/ui/console/GrailsSwingConsole.groovy" | _
        "grails-console/src/main/groovy/grails/ui/console/support/GroovyConsoleApplicationContext.groovy" | _
        "grails-console/src/main/groovy/grails/ui/console/support/GroovyConsoleWebApplicationContext.groovy" | _
        "grails-console/src/main/groovy/grails/ui/script/GrailsApplicationScriptRunner.groovy" | _
        "grails-console/src/main/groovy/grails/ui/shell/GrailsShell.groovy" | _
        "grails-console/src/main/groovy/grails/ui/shell/support/GroovyshApplicationContext.groovy" | _
        "grails-console/src/main/groovy/grails/ui/shell/support/GroovyshWebApplicationContext.groovy" | _
        "grails-console/src/main/groovy/grails/ui/support/DevelopmentGrailsApplication.groovy" | _
        "grails-console/src/main/groovy/grails/ui/support/DevelopmentWebApplicationContext.groovy" | _

        "grails-core/src/main/groovy/grails/beans/util/LazyBeanMap.groovy" | _
        "grails-core/src/main/groovy/grails/boot/GrailsApp.groovy" | _
        "grails-core/src/main/groovy/grails/boot/GrailsAppBuilder.groovy" | _
        "grails-core/src/main/groovy/grails/boot/GrailsPluginApplication.groovy" | _
        "grails-core/src/main/groovy/grails/boot/config/GrailsApplicationContextLoader.groovy" | _
        "grails-core/src/main/groovy/grails/boot/config/GrailsApplicationPostProcessor.groovy" | _
        "grails-core/src/main/groovy/grails/boot/config/GrailsAutoConfiguration.groovy" | _
        "grails-core/src/main/groovy/grails/boot/config/tools/ProfilingGrailsApplicationPostProcessor.groovy" | _
        "grails-core/src/main/groovy/grails/boot/config/tools/SettingsFile.groovy" | _
        "grails-core/src/main/groovy/grails/compiler/DelegatingMethod.groovy" | _
        "grails-core/src/main/groovy/grails/compiler/GrailsCompileStatic.groovy" | _
        "grails-core/src/main/groovy/grails/compiler/GrailsTypeChecked.groovy" | _
        "grails-core/src/main/groovy/grails/compiler/ast/GlobalClassInjector.groovy" | _
        "grails-core/src/main/groovy/grails/compiler/ast/GlobalClassInjectorAdapter.groovy" | _
        "grails-core/src/main/groovy/grails/config/Config.groovy" | _
        "grails-core/src/main/groovy/grails/config/ConfigProperties.groovy" | _
        "grails-core/src/main/groovy/grails/config/Settings.groovy" | _
        "grails-core/src/main/groovy/grails/core/GrailsApplicationClass.groovy" | _
        "grails-core/src/main/groovy/grails/core/GrailsApplicationLifeCycle.groovy" | _
        "grails-core/src/main/groovy/grails/core/GrailsApplicationLifeCycleAdapter.groovy" | _
        "grails-core/src/main/groovy/grails/core/events/ArtefactAdditionEvent.groovy" | _
        "grails-core/src/main/groovy/grails/dev/Support.groovy" | _
        "grails-core/src/main/groovy/grails/dev/commands/ApplicationCommand.groovy" | _
        "grails-core/src/main/groovy/grails/dev/commands/ApplicationContextCommandRegistry.groovy" | _
        "grails-core/src/main/groovy/grails/dev/commands/ExecutionContext.groovy" | _
        "grails-core/src/main/groovy/grails/persistence/support/PersistenceContextInterceptorExecutor.groovy" | _
        "grails-core/src/main/groovy/grails/plugins/Plugin.groovy" | _
        "grails-core/src/main/groovy/grails/plugins/PluginManagerLoader.groovy" | _
        "grails-core/src/main/groovy/grails/transaction/GrailsTransactionTemplate.groovy" | _
        "grails-core/src/main/groovy/grails/transaction/Rollback.groovy" | _
        "grails-core/src/main/groovy/grails/util/GrailsArrayUtils.groovy" | _
        "grails-core/src/main/groovy/grails/util/GrailsStringUtils.groovy" | _
        "grails-core/src/main/groovy/grails/util/MixinTargetAware.groovy" | _
        "grails-core/src/main/groovy/grails/util/TypeConvertingMap.groovy" | _
        "grails-core/src/main/groovy/grails/validation/ValidationErrors.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/ApplicationAttributes.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/ArtefactHandler.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/ArtefactInfo.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/DomainClassArtefactHandler.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsApplication.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsClass.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsClassUtils.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsControllerClass.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsDomainClass.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsDomainClassProperty.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsDomainConfigurationUtil.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/commons/GrailsMetaClassUtils.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/ASTErrorsHelper.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/ASTValidationErrorsHelper.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/AbstractGrailsArtefactTransformer.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/AstTransformer.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/GrailsASTUtils.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/GrailsArtefactClassInjector.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/compiler/injection/GrailsDomainClassInjector.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/plugins/GrailsPlugin.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/plugins/GrailsPluginManager.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/plugins/PluginManagerAware.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/plugins/support/aware/GrailsApplicationAware.groovy" | _
        "grails-core/src/main/groovy/org/codehaus/groovy/grails/validation/ConstraintsEvaluator.groovy" | _
        "grails-core/src/main/groovy/org/grails/boot/internal/JavaCompiler.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/CriteriaTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/DomainMappingTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/DynamicFinderTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/HttpServletRequestTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/RelationshipManagementMethodTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/ValidateableTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/WhereQueryTypeCheckingExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/injection/ApplicationClassInjector.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/injection/EnhancesTraitTransformation.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/injection/GlobalGrailsClassInjectorTransformation.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/injection/GlobalImportTransformation.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/injection/GroovyEclipseCompilationHelper.groovy" | _
        "grails-core/src/main/groovy/org/grails/compiler/injection/TraitInjectionSupport.groovy" | addIgnore([Parameter, IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-core/src/main/groovy/org/grails/config/NavigableMapPropertySource.groovy" | _
        "grails-core/src/main/groovy/org/grails/config/PrefixedMapPropertySource.groovy" | _
        "grails-core/src/main/groovy/org/grails/config/yaml/YamlPropertySourceLoader.groovy" | addIgnore([Parameter, IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-core/src/main/groovy/org/grails/core/artefact/ApplicationArtefactHandler.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/cfg/GroovyConfigPropertySourceLoader.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/exceptions/DefaultErrorsPrinter.groovy" | addIgnore([Parameter, IfStatement, ExpressionStatement, ContinueStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-core/src/main/groovy/org/grails/core/io/CachingPathMatchingResourcePatternResolver.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/io/GrailsResource.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/io/support/GrailsFactoriesLoader.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/legacy/LegacyGrailsApplication.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/legacy/LegacyGrailsDomainClass.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/metaclass/MetaClassEnhancer.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/support/GrailsApplicationDiscoveryStrategy.groovy" | _
        "grails-core/src/main/groovy/org/grails/core/util/IncludeExcludeSupport.groovy" | _
        "grails-core/src/main/groovy/org/grails/dev/support/DevelopmentShutdownHook.groovy" | _
        "grails-core/src/main/groovy/org/grails/plugins/CoreGrailsPlugin.groovy" | _
        "grails-core/src/main/groovy/org/grails/plugins/support/WatchPattern.groovy" | addIgnore([IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-core/src/main/groovy/org/grails/spring/beans/factory/HotSwappableTargetSourceFactoryBean.groovy" | _
        "grails-core/src/main/groovy/org/grails/spring/context/ApplicationContextExtension.groovy" | _
        "grails-core/src/main/groovy/org/grails/spring/context/support/MapBasedSmartPropertyOverrideConfigurer.groovy" | _
        "grails-core/src/main/groovy/org/grails/transaction/transform/RollbackTransform.groovy" | _
        "grails-core/src/main/groovy/org/grails/transaction/transform/TransactionalTransform.groovy" | _
        "grails-core/src/main/groovy/org/grails/validation/ConstraintEvalUtils.groovy" | _
        "grails-core/src/test/groovy/grails/artefact/EnhancesSpec.groovy" | _
        "grails-core/src/test/groovy/grails/config/ConfigPropertiesSpec.groovy" | _
        "grails-core/src/test/groovy/grails/transaction/TransactionalTransformSpec.groovy" | _
        "grails-core/src/test/groovy/grails/util/GrailsMetaClassUtilsSpec.groovy" | addIgnore([PackageNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-core/src/test/groovy/grails/web/CamelCaseUrlConverterSpec.groovy" | _
        "grails-core/src/test/groovy/grails/web/HyphenatedUrlConverterSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/commons/GrailsArrayUtilsSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/commons/GrailsStringUtilsSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/commons/cfg/GrailsPlaceHolderConfigurerCorePluginRuntimeSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/commons/cfg/GrailsPlaceholderConfigurerSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/compiler/injection/ASTValidationErrorsHelperSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/compiler/injection/ApiDelegateSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/compiler/injection/GrailsArtefactTransformerSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/core/io/ResourceLocatorSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/exceptions/StackTraceFiltererSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/exceptions/StackTracePrinterSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/plugins/BinaryPluginSpec.groovy" | _
        "grails-core/src/test/groovy/org/codehaus/groovy/grails/plugins/support/WatchPatternParserSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/compiler/injection/ArtefactTypeAstTransformationSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/compiler/injection/DefaultDomainClassInjectorSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/compiler/injection/GlobalGrailsClassInjectorTransformationSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/config/NavigableMapPropertySourceSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/config/PropertySourcesConfigSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/config/YamlPropertySourceLoaderSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/core/DefaultGrailsControllerClassSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/plugins/GrailsPluginTests.groovy" | _
        "grails-core/src/test/groovy/org/grails/spring/context/ApplicationContextExtensionSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/transaction/ChainedTransactionManagerPostProcessorSpec.groovy" | _
        "grails-core/src/test/groovy/org/grails/util/TypeConvertingMapTests.groovy" | _

        "grails-databinding/src/main/groovy/grails/databinding/SimpleDataBinder.groovy" | addIgnore([Parameter, ExpressionStatement, IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-databinding/src/main/groovy/grails/databinding/SimpleMapDataBindingSource.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/BindUsing.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/BindingFormat.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/ClosureValueConverter.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/IndexedPropertyReferenceDescriptor.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/AbstractStructuredDateBindingEditor.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/CurrencyValueConverter.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/DateConversionHelper.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/FormattedDateValueConverter.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/StructuredCalendarBindingEditor.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/StructuredDateBindingEditor.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/StructuredSqlDateBindingEditor.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/converters/TimeZoneConverter.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/xml/GPathResultCollectionDataBindingSource.groovy" | _
        "grails-databinding/src/main/groovy/org/grails/databinding/xml/GPathResultMap.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/BindUsingSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/BindingErrorSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/BindingFormatSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/BindingListenerSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/CollectionBindingSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/CustomTypeConverterSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/IncludeExcludeBindingSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/SimpleDataBinderEnumBindingSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/SimpleDataBinderEnumValueConverterSpec.groovy" | addIgnore([FieldNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-databinding/src/test/groovy/grails/databinding/SimpleDataBinderSpec.groovy" | _
        "grails-databinding/src/test/groovy/grails/databinding/XMLBindingSpec.groovy" | _
        "grails-databinding/src/test/groovy/org/grails/databinding/compiler/BindingFormatCompilationErrorsSpec.groovy" | _
        "grails-databinding/src/test/groovy/org/grails/databinding/converters/CurrencyConversionSpec.groovy" | _
        "grails-databinding/src/test/groovy/org/grails/databinding/converters/DateConversionHelperSpec.groovy" | _
        "grails-databinding/src/test/groovy/org/grails/databinding/xml/GPathCollectionDataBindingSourceSpec.groovy" | _
        "grails-databinding/src/test/groovy/org/grails/databinding/xml/GPathResultMapSpec.groovy" | _

        "grails-docs/src/main/groovy/grails/doc/DocEngine.groovy" | addIgnore([ExpressionStatement, IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/DocPublisher.groovy" | addIgnore([ExpressionStatement, IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/LegacyDocMigrator.groovy" | addIgnore([ExpressionStatement, Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/PdfBuilder.groovy" | _
        "grails-docs/src/main/groovy/grails/doc/ant/DocPublisherTask.groovy" | addIgnore([IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/filters/HeaderFilter.groovy" | _
        "grails-docs/src/main/groovy/grails/doc/filters/LinkTestFilter.groovy" | addIgnore([IfStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/filters/ListFilter.groovy" | _
        "grails-docs/src/main/groovy/grails/doc/gradle/MigrateLegacyDocs.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/gradle/PublishGuide.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/gradle/PublishPdf.groovy" | _
        "grails-docs/src/main/groovy/grails/doc/internal/FileResourceChecker.groovy" | _
        "grails-docs/src/main/groovy/grails/doc/internal/LegacyTocStrategy.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/internal/UserGuideNode.groovy" | _
        "grails-docs/src/main/groovy/grails/doc/internal/YamlTocStrategy.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/macros/GspTagSourceMacro.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-docs/src/main/groovy/grails/doc/macros/HiddenMacro.groovy" | _
        "grails-docs/src/test/groovy/grails/doc/internal/LegacyTocStrategySpec.groovy" | _
        "grails-docs/src/test/groovy/grails/doc/internal/StringEscapeCategoryTests.groovy" | _
        "grails-docs/src/test/groovy/grails/doc/internal/YamlTocStrategySpec.groovy" | _
        "grails-docs/src/test/groovy/grails/doc/macros/GspTagSourceMacroTest.groovy" | addIgnore([FieldNode, PropertyNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "grails-encoder/src/main/groovy/org/grails/buffer/StreamCharBufferMetaUtils.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/main/groovy/org/grails/encoder/CodecMetaClassSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/main/groovy/org/grails/encoder/impl/HTMLCodecFactory.groovy" | addIgnore([FieldNode, PropertyNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/main/groovy/org/grails/encoder/impl/JSONCodecFactory.groovy" | _
        "grails-encoder/src/main/groovy/org/grails/encoder/impl/JavaScriptCodec.groovy" | addIgnore([ExpressionStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/main/groovy/org/grails/encoder/impl/StandaloneCodecLookup.groovy" | addIgnore([Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/main/groovy/org/grails/encoder/impl/URLCodecFactory.groovy" | _
        "grails-encoder/src/test/groovy/org/grails/buffer/StreamCharBufferGroovyTests.groovy" | _
        "grails-encoder/src/test/groovy/org/grails/charsequences/CharSequencesSpec.groovy" | addIgnore([ExpressionStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/test/groovy/org/grails/encoder/ChainedEncodersSpec.groovy" | addIgnore([ExpressionStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/test/groovy/org/grails/encoder/impl/BasicCodecLookupSpec.groovy" | _
        "grails-encoder/src/test/groovy/org/grails/encoder/impl/HTMLEncoderSpec.groovy" | addIgnore([ExpressionStatement, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-encoder/src/test/groovy/org/grails/encoder/impl/JavaScriptCodecTests.groovy" | _

        "grails-gradle-model/src/main/groovy/org/grails/gradle/plugin/model/DefaultGrailsClasspath.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-model/src/main/groovy/org/grails/gradle/plugin/model/GrailsClasspath.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/agent/AgentTasksEnhancer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/commands/ApplicationContextCommandTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/core/GrailsExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//tested separately in "test GrailsGradlePlugin for #path"       "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/core/GrailsGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/core/GrailsPluginGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/core/IntegrationTestGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/core/PluginDefiner.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/doc/GrailsDocGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/doc/PublishGuideTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/model/GrailsClasspathToolingModelBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/profiles/GrailsProfileGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/profiles/GrailsProfilePublishGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/profiles/tasks/ProfileCompilerTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/publishing/GrailsCentralPublishGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/publishing/GrailsPublishExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/run/FindMainClassTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/run/GrailsRunTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/util/SourceSets.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/watch/GrailsWatchPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/watch/WatchConfig.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/web/GrailsWebGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/web/gsp/GroovyPageCompileTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/web/gsp/GroovyPagePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)


        "grails-gsp/src/main/groovy/org/grails/gsp/GroovyPagesMetaUtils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gsp/src/main/groovy/org/grails/gsp/compiler/GroovyPageCompiler.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-gsp/src/test/groovy/org/grails/gsp/GroovyPagesTemplateEngineTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-logging/src/test/groovy/org/grails/compiler/logging/LoggingTransformerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/grails/artefact/AsyncController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/grails/async/services/PersistenceContextPromiseDecorator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/grails/async/services/TransactionalPromiseDecorator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/grails/async/web/AsyncGrailsWebRequest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/grails/compiler/traits/AsyncControllerTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/async/transform/internal/DefaultDelegateAsyncTransactionalMethodTransformer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/compiler/web/async/TransactionalAsyncTransformUtils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/plugins/web/async/ControllersAsyncGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/plugins/web/async/GrailsAsyncContext.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/plugins/web/async/WebRequestPromiseDecorator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/plugins/web/async/WebRequestPromiseDecoratorLookupStrategy.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/main/groovy/org/grails/plugins/web/async/mvc/AsyncActionResultTransformer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-async/src/test/groovy/grails/async/services/AsyncTransactionalServiceSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-codecs/src/main/groovy/org/grails/plugins/CodecsGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-codecs/src/main/groovy/org/grails/plugins/codecs/URLCodec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-codecs/src/test/groovy/org/grails/web/codecs/HTMLCodecTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-codecs/src/test/groovy/org/grails/web/codecs/HTMLJSCodecSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-codecs/src/test/groovy/org/grails/web/codecs/JSONEncoderSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-codecs/src/test/groovy/org/grails/web/codecs/URLCodecTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/artefact/Controller.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, CaseStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/artefact/controller/support/AllowedMethodsHelper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/artefact/controller/support/RequestForwarder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/artefact/controller/support/ResponseRedirector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//tested separately        "grails-plugin-controllers/src/main/groovy/grails/artefact/controller/support/ResponseRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/compiler/traits/ControllerTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/web/Controller.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/org/grails/plugins/web/controllers/ControllersGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/org/grails/plugins/web/controllers/DefaultControllerExceptionHandlerMetaData.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/org/grails/plugins/web/controllers/metaclass/ForwardMethod.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/org/grails/plugins/web/servlet/context/BootStrapClassRunner.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/org/grails/plugins/web/servlet/mvc/InvalidResponseHandler.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/org/grails/plugins/web/servlet/mvc/ValidResponseHandler.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/test/groovy/grails/artefact/controller/support/AllowedMethodsHelperSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/test/groovy/org/codehaus/groovy/grails/compiler/web/ControllerActionTransformerClosureActionOverridingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/test/groovy/org/codehaus/groovy/grails/compiler/web/ControllerActionTransformerCompilationErrorsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/test/groovy/org/codehaus/groovy/grails/compiler/web/ControllerActionTransformerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/grails/web/JSONBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/org/grails/plugins/converters/ConvertersGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
//tested separately        "grails-plugin-converters/src/main/groovy/org/grails/web/converters/AbstractParsingParameterCreationListener.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/org/grails/web/converters/ConfigurableConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/org/grails/web/converters/ConvertersExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/org/grails/web/converters/IncludeExcludeConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/org/grails/web/converters/configuration/configtest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/test/groovy/grails/converters/ParsingNullJsonValuesSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/test/groovy/org/grails/compiler/web/converters/ConvertersDomainTransformerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/test/groovy/org/grails/plugins/converters/api/ConvertersApiSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/test/groovy/org/grails/web/converters/ConverterUtilSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/test/groovy/org/grails/web/converters/marshaller/json/DomainClassMarshallerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/test/groovy/org/grails/web/converters/marshaller/json/ValidationErrorsMarshallerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, AssertStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "grails-plugin-databinding/src/main/groovy/org/grails/databinding/converters/web/LocaleAwareBigDecimalConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-databinding/src/main/groovy/org/grails/databinding/converters/web/LocaleAwareNumberConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-databinding/src/main/groovy/org/grails/plugins/databinding/DataBindingGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-datasource/src/main/groovy/org/grails/plugins/datasource/DataSourceGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-datasource/src/main/groovy/org/grails/plugins/datasource/DataSourceUtils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-datasource/src/main/groovy/org/grails/plugins/datasource/EmbeddedDatabaseShutdownHook.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-datasource/src/main/groovy/org/grails/plugins/datasource/TomcatJDBCPoolMBeanExporter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/main/groovy/grails/artefact/DomainClass.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/main/groovy/grails/compiler/traits/DomainClassTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/main/groovy/org/grails/plugins/domain/DomainClassGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/main/groovy/org/grails/plugins/domain/DomainClassPluginSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/main/groovy/org/grails/plugins/domain/support/GormApiSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/main/groovy/org/grails/plugins/domain/support/GrailsDomainClassCleaner.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/test/groovy/org/codehaus/groovy/grails/domain/CircularBidirectionalMapBySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/test/groovy/org/codehaus/groovy/grails/domain/DomainClassTraitSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-domain-class/src/test/groovy/org/codehaus/groovy/grails/domain/EntityTransformIncludesGormApiSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/main/groovy/grails/events/Events.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/main/groovy/org/grails/events/ClosureEventConsumer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/main/groovy/org/grails/events/reactor/GrailsReactorConfigurationReader.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/main/groovy/org/grails/events/spring/SpringEventTranslator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/main/groovy/org/grails/plugins/events/EventBusGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/test/groovy/grails/events/EventsTraitSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/test/groovy/grails/events/SpringEventTranslatorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-events/src/test/groovy/org/grails/events/reactor/GrailsReactorConfigurationReaderSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/ast/groovy/grails/compiler/traits/ControllerTagLibraryTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/GrailsLayoutViewResolverPostProcessor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/GroovyPagesGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/ApplicationTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/CountryTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/FormTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/FormatTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/JavascriptTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/PluginTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/UrlMappingTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-gsp/src/main/groovy/org/grails/plugins/web/taglib/ValidationTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-i18n/src/main/groovy/org/grails/plugins/i18n/I18nGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/main/groovy/grails/artefact/Interceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/main/groovy/grails/compiler/traits/InterceptorTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/main/groovy/grails/interceptors/Matcher.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/main/groovy/org/grails/plugins/web/interceptors/GrailsInterceptorHandlerInterceptorAdapter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/main/groovy/org/grails/plugins/web/interceptors/InterceptorsGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/main/groovy/org/grails/plugins/web/interceptors/UrlMappingMatcher.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/test/groovy/grails/artefact/GrailsInterceptorHandlerInterceptorAdapterSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/test/groovy/grails/artefact/InterceptorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-interceptors/src/test/groovy/org/grails/plugins/web/interceptors/UrlMappingMatcherSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/grails/web/mime/AcceptHeaderParser.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/plugins/web/api/MimeTypesApiSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/plugins/web/mime/FormatInterceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/plugins/web/mime/MimeTypesFactoryBean.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/plugins/web/mime/MimeTypesGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/web/mime/DefaultAcceptHeaderParser.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/web/mime/DefaultMimeTypeResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/web/mime/HttpServletRequestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/main/groovy/org/grails/web/mime/HttpServletResponseExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/test/groovy/org/codehaus/groovy/grails/plugins/web/api/RequestAndResponseMimeTypesApiSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/test/groovy/org/codehaus/groovy/grails/web/mime/AcceptHeaderParserTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-mimetypes/src/test/groovy/org/codehaus/groovy/grails/web/mime/MimeUtilitySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "grails-plugin-rest/src/main/groovy/grails/artefact/controller/RestResponder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/compiler/traits/RestResponderTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/Link.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/Linkable.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/Resource.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/RestfulController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/AbstractIncludeExcludeRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/AbstractRenderContext.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/AbstractRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/ContainerRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/RenderContext.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/Renderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/RendererRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/atom/AtomCollectionRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/atom/AtomRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/errors/AbstractVndErrorRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/errors/VndErrorJsonRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/errors/VndErrorXmlRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/hal/HalJsonCollectionRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/hal/HalJsonRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/hal/HalXmlCollectionRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/hal/HalXmlRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/json/JsonCollectionRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/json/JsonRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/util/AbstractLinkingRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/xml/XmlCollectionRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/grails/rest/render/xml/XmlRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/plugin/RestResponderGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/render/DefaultRendererRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/render/ServletRenderContext.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/render/html/DefaultHtmlRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/render/json/DefaultJsonRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/render/xml/DefaultXmlRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/transform/LinkableTransform.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/main/groovy/org/grails/plugins/web/rest/transform/ResourceTransform.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/render/DefaultRendererRegistrySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/render/VndErrorRenderingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/render/hal/HalJsonRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/render/html/HtmlRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/render/json/JsonRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/transform/LinkableTransformSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-rest/src/test/groovy/org/grails/plugins/web/rest/transform/ResourceTransformSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-services/src/main/groovy/grails/artefact/Service.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-services/src/main/groovy/grails/compiler/traits/ServiceTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-services/src/main/groovy/org/grails/plugins/services/ServiceBeanAliasPostProcessor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-services/src/main/groovy/org/grails/plugins/services/ServicesGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/TestMixinTargetAware.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/domain/DomainClassUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/domain/MockCascadingDomainClassValidator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/integration/Integration.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/integration/IntegrationTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/services/ServiceUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/support/GrailsUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/support/TestMixinRegistrar.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/support/TestMixinRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/web/ControllerUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/web/GroovyPageUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/web/InterceptorUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/web/UrlMappingsUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/webflow/WebFlowUnitTestMixin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/mixin/webflow/WebFlowUnitTestSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/ControllerTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/CoreBeansTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/DefaultSharedRuntimeConfigurer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/DirtiesRuntime.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/DomainClassTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/FreshRuntime.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/GrailsApplicationTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/GroovyPageTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/InterceptorTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/MetaClassCleanerTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/SharedRuntime.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/SharedRuntimeConfigurer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestEvent.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestEventInterceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestPluginUsage.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestRuntime.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestRuntimeFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestRuntimeJunitAdapter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestRuntimeSettings.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/TestRuntimeUtil.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/grails/test/runtime/WebFlowTestPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/org/grails/compiler/injection/test/IntegrationTestMixinTransformation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/org/grails/test/context/junit4/GrailsJunit4ClassRunner.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/main/groovy/org/grails/test/mixin/support/DefaultTestMixinRegistrar.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/test/groovy/grails/test/mixin/MetaClassCleanupSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/test/groovy/grails/test/mixin/TestForSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/test/groovy/grails/test/mixin/TestMixinSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/test/groovy/grails/test/mixin/integration/compiler/IntegrationTestMixinCompilationErrorsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-testing/src/test/groovy/grails/test/runtime/TestRuntimeFactorySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-url-mappings/src/main/groovy/org/grails/plugins/web/mapping/UrlMappingsGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-validation/src/main/groovy/grails/validation/Validateable.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-validation/src/main/groovy/org/grails/web/plugins/support/ValidationSupport.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-validation/src/test/groovy/grails/validation/DefaultASTValidateableHelperSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "grails-shell/src/main/groovy/org/grails/cli/GrailsCli.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/boot/GrailsDependencyVersions.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/boot/GrailsTestCompilerAutoConfiguration.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/boot/SpringInvoker.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/ClasspathBuildAction.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/GradleAsyncInvoker.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/GradleInvoker.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/GradleUtil.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/cache/CachedGradleOperation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/cache/ListReadingCachedGradleOperation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/cache/MapReadingCachedGradleOperation.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/commands/GradleCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/commands/GradleTaskCommandAdapter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/gradle/commands/ReadGradleTasks.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/AllClassCompleter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/ClassNameCompleter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/ClosureCompleter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/DomainClassCompleter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/EscapingFileNameCompletor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/RegexCompletor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/SimpleOrFileNameCompletor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/interactive/completers/TestsCompleter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/AbstractProfile.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/AbstractStep.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/Command.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/CommandArgument.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/CommandDescription.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/CommandException.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/DefaultFeature.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/Feature.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/FileSystemProfile.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/MultiStepCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/ProfileCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/ProfileRepository.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/ProfileRepositoryAware.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/ProjectCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/ProjectContextAware.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/ResourceProfile.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/Step.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/codegen/ModelBuilder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/ArgumentCompletingCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/ClosureExecutingCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/CommandCompleter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/CommandRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/CreateAppCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/CreatePluginCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/CreateProfileCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/DefaultMultiStepCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/HelpCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/ListProfilesCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/OpenCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/ProfileInfoCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/events/CommandEvents.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/events/EventStorage.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/ApplicationContextCommandFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/ClasspathCommandResourceResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/CommandFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/CommandResourceResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/FileSystemCommandResourceResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/GroovyScriptCommandFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/ResourceResolvingCommandFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/ServiceCommandFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/factory/YamlCommandFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/io/FileSystemInteraction.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/io/FileSystemInteractionImpl.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/io/ServerInteraction.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/script/GroovyScriptCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/script/GroovyScriptCommandTransform.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/templates/SimpleTemplate.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/templates/TemplateException.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/templates/TemplateRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/commands/templates/TemplateRendererImpl.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/git/GitProfileRepository.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/repository/AbstractJarProfileRepository.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/repository/MavenProfileRepository.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/repository/StaticJarProfileRepository.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/DefaultStepFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/ExecuteStep.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/GradleStep.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/MkdirStep.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/RenderStep.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/StepFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/steps/StepRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/main/groovy/org/grails/cli/profile/support/ArtefactVariableResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/groovy/org/grails/cli/interactive/completers/RegexCompletorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/groovy/org/grails/cli/profile/ResourceProfileSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/groovy/org/grails/cli/profile/commands/CommandRegistrySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/groovy/org/grails/cli/profile/commands/CommandScriptTransformSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/groovy/org/grails/cli/profile/repository/MavenRepositorySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/groovy/org/grails/cli/profile/steps/StepRegistrySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-shell/src/test/resources/profiles-repository/profiles/web/commands/TestGroovy.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-spring/src/main/groovy/grails/spring/DynamicElementReader.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/main/groovy/org/grails/taglib/NamespacedTagDispatcher.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/main/groovy/org/grails/taglib/TagLibraryMetaUtils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/main/groovy/org/grails/taglib/TemplateNamespacedTagDispatcher.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/main/groovy/org/grails/taglib/encoder/OutputEncodingSettings.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/main/groovy/org/grails/taglib/encoder/WithCodecHelper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/test/groovy/org/grails/taglib/GroovyPageAttributesTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/test/groovy/org/grails/taglib/GroovyPageTagWriterSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-taglib/src/test/groovy/org/grails/taglib/encoder/WithCodecHelperSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)


    }


    @Unroll
    def "test grails-core-3 part2 for #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/grails-core-3/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }


        where:
        path | config
        "grails-test-suite-base/src/main/groovy/org/grails/plugins/web/AbstractGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-base/src/main/groovy/org/grails/web/servlet/mvc/AbstractGrailsControllerTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-base/src/main/groovy/org/grails/web/taglib/AbstractGrailsTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/artefact/DomainClassTraitSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/gorm/criteri/WithCriteriaReadOnlySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/test/mixin/domain/DomainClassUnitTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/test/mixin/domain/SaveDomainSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/web/databinding/GrailsWebDataBinderBindingXmlSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/web/databinding/GrailsWebDataBinderConfigurationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/web/databinding/GrailsWebDataBinderListenerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/web/databinding/GrailsWebDataBinderSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/grails/web/databinding/GrailsWebDataBindingStructuredEditorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/domain/compiler/DomainPropertiesAccessorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/orm/support/TransactionManagerPostProcessorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/plugins/MockHibernateGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/plugins/services/ScopedProxyAndServiceClassTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/plugins/services/ServicesGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/reload/ServiceReloadTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-persistence/src/test/groovy/org/grails/reload/TransactionalServiceReloadTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/compiler/GrailsCompileStaticCompilationErrorsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/compiler/GrailsTypeCheckedCompilationErrorsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/persistence/EntityTransformTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/spring/BeanBuilderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/spring/DynamicElementReaderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/MetaTestHelper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/AddToAndServiceInjectionTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/AnotherController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/AstEnhancedControllerUnitTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/AutowireServiceViaDefineBeansTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/BidirectionalOneToManyUnitTestTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/CascadeValidationForEmbeddedSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/ControllerAndGroovyPageMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/ControllerMockWithMultipleControllersSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/ControllerTestForTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/ControllerUnitTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/ControllerWithMockCollabTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassAnnotatedSetupMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassControllerUnitTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassDeepValidationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassMetaClassCleanupSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassSetupMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassWithAutoTimestampSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassWithCustomValidatorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassWithDefaultConstraintsUnitTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/DomainClassWithUniqueConstraintSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/FirstController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/GroovyPageUnitTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/GroovyPageUnitTestMixinWithCustomViewDirSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/InheritanceWithValidationTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/InterceptorUnitTestMixinSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/MainContextTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/MockedBeanSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/MyService.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/PartialMockWithManyToManySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/ResourceAnnotationRestfulControllerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/RestfulControllerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/RestfulControllerSubclassSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/RestfulControllerSuperClassSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/SetupTeardownInvokeTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/SpyBeanSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/StaticCallbacksSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/TagLibraryInvokeBodySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/TestForControllerWithoutMockDomainTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/TestInstanceCallbacksAnnotationsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/TestInstanceCallbacksSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/TestMixinSetupTeardownInvokeTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/UnitTestDataBindingAssociatonTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/UnitTestEmbeddedPropertyQuery.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/UrlMappingsTestMixinTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/User.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/support/GrailsUnitTestMixinGrailsApplicationAwareSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/mixin/unique/UniqueConstraintOnHasOneSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/test/runtime/DirtiesRuntimeSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/util/BuildScopeTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/util/ClosureToMapPopulatorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/util/CollectionUtilsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/validation/CommandObjectConstraintGettersSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/validation/DomainClassValidationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/validation/DomainConstraintGettersSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/validation/ValidateableMockSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/validation/ValidateableTraitAdHocSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/validation/ValidateableTraitSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/grails/web/JSONBuilderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/cli/ScriptNameResolverTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/DefaultArtefactInfoTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/DefaultGrailsCodecClassTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/DefaultGrailsDomainClassPropertyTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/DefaultGrailsDomainClassTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/GrailsMetaClassUtilsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/GrailsPluginManagerTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/MultipleClassesPerFileTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/TestReload.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/UrlMappingsArtefactHandlerTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/cfg/ExampleConfigClassObject.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/cfg/ExampleConfigCompiledClass.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/metaclass/LazyMetaPropertyMapSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/metaclass/LazyMetaPropertyMapTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/metaclass/MetaClassEnhancerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/metaclass/MetaClassEnhancerTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/commons/spring/OptimizedAutowireCapableBeanFactorySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/compiler/injection/GrailsASTUtilsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/CircularRelationship.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/ManyToManyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/OneToManyTest2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/OneToOneTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/RelationshipsTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/Test1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/Test2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/domain/UniOneToManyTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/CoreGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/DomainClassGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/PluginLoadOrderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/datasource/DataSourceGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/grails-app/conf/NonPooledApplicationDataSource.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/grails-app/conf/PooledApplicationDataSource.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/grails-app/services/TestService.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/metadata/GrailsPluginMetadataTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/testing/GrailsMockHttpServletRequestSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/testing/GrailsMockHttpServletRequestTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/testing/GrailsMockHttpServletResponseTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/testing/GrailsMockHttpSessionTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/web/ControllersGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/web/ServletsExtensionTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/web/rest/render/atom/AtomDomainClassRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/web/rest/render/hal/HalDomainClassJsonRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/plugins/web/rest/render/hal/HalDomainClassXmlRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/reload/SpringProxiedBeanReloadTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/test/support/ControllerNameExtractorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/test/support/MockHibernatePluginHelper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/CascadingErrorCountSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/ConstrainedPropertyBuilderForCommandsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/ConstraintMessageTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/ConstraintsBuilderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/GrailsDomainClassValidatorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/NullableConstraintTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/TestConstraints.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/validation/TestingValidationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/codecs/HTMLJSCodecIntegrationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/context/GrailsConfigUtilsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/errors/GrailsExceptionResolverTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/filters/HiddenHttpMethodFilterTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/i18n/ParamsAwareLocaleChangeInterceptorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/json/JSONObjectTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/metaclass/ChainMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/metaclass/ChainMethodWithRequestDataValueProcessorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/metaclass/ForwardMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/metaclass/ForwardMethodspec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/metaclass/WithFormMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/DefaultGrailsApplicationAttributesTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/FlashScopeWithErrorsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/GrailsHttpSessionTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/MultipleRenderCallsContentTypeTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/RenderMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/ControllerInheritanceTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/ParamsObjectTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/RedirectController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/RedirectMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/RedirectMethodWithRequestDataValueProcessorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/RedirectToDefaultActionTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/RenderDynamicMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/ReturnModelAndViewController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/TagLibDynamicMethodsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/alpha/AnotherNamespacedController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/alpha/NamespacedController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/beta/AnotherNamespacedController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/beta/NamespacedController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/controller1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/controller2.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/servlet/mvc/controller4.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/sitemesh/FactoryHolderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/sitemesh/FullSitemeshLifeCycleTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/sitemesh/FullSitemeshLifeCycleWithNoPreprocessingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/sitemesh/GSPSitemeshPageTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/util/CodecWithClosureProperties.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/util/StreamCharBufferSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/org/grails/web/util/WebUtilsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/sharedruntimetest/MySharedRuntimeConfigurer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/sharedruntimetest/SharedRuntimeCheck.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/sharedruntimetest/SharedRuntimeSample2Test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/sharedruntimetest/SharedRuntimeSampleTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/sharedruntimetest/subpackage/SharedRuntimeByPkgSample2Test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/groovy/sharedruntimetest/subpackage/SharedRuntimeByPkgSampleTest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/resources/grails/spring/resources1.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/resources/org/grails/commons/cfg/ExampleConfigDefaults.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/resources/org/grails/commons/cfg/ExampleConfigScript.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/resources/org/grails/commons/classes.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/src/test/resources/org/grails/plugins/ClassEditorGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-app/conf/BuildConfig.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/global-plugins/logging-0.1/LoggingGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/global-plugins/logging-0.1/scripts/DoSomething.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/global-plugins/logging-0.1/scripts/_Install.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/grails-debug/scripts/RunDebug.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/plugins/jsecurity-0.3/JSecurityGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/plugins/jsecurity-0.3/scripts/CreateAuthController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/grails-plugin-utils/plugins/jsecurity-0.3/scripts/CreateDbRealm.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/resources/spring/test.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/inline-plugins/app/grails-app/conf/BuildConfig.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/inline-plugins/plugins/foo/FooGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/inline-plugins/plugins/foo/grails-app/controllers/foo/FooController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/inline-plugins/plugins/foobar/FoobarGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/inline-plugins/plugins/foobar/grails-app/controllers/foobar/FoobarController.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/nested-inline-plugins/app/grails-app/conf/BuildConfig.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/nested-inline-plugins/plugins/plugin-one/PluginOneGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/nested-inline-plugins/plugins/plugin-one/grails-app/conf/BuildConfig.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/nested-inline-plugins/plugins/plugin-two/PluginTwoGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/grails-app/conf/BootStrap.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/grails-app/conf/BuildConfig.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/grails-app/conf/Config.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/grails-app/conf/DataSource.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/grails-app/conf/UrlMappings.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/grails-app/conf/spring/resources.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/hibernate-1.2-SNAPSHOT/HibernateGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/hibernate-1.2-SNAPSHOT/dependencies.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/hibernate-1.2-SNAPSHOT/scripts/_Install.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/hibernate-1.2-SNAPSHOT/scripts/_Uninstall.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/hibernate-1.2-SNAPSHOT/scripts/_Upgrade.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/webflow-1.2-SNAPSHOT/WebflowGrailsPlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/webflow-1.2-SNAPSHOT/dependencies.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/webflow-1.2-SNAPSHOT/scripts/_Install.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/webflow-1.2-SNAPSHOT/scripts/_Uninstall.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-uber/test/test-projects/plugin-build-settings/plugins/webflow-1.2-SNAPSHOT/scripts/_Upgrade.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/artefact/ControllerTraitSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/artefact/TagLibraryTraitSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/gsp/PageRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/rest/web/RespondMethodSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/AbstractGrailsEnvChangingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/mixin/TagLibWithServiceMockTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/mixin/UrlMappingsTestForTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/web/AsyncControllerTestSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/web/ControllerWithGroovyMixinSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/web/FordedUrlSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/web/GetHeadersFromResponseSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/grails/test/web/RedirectToDomainSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/compiler/web/ControllerActionTransformerAllowedMethodsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/compiler/web/WithFormatSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/compiler/web/converters/ConvertersControllersApiSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/compiler/web/taglib/TagLibraryTransformerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/compiler/tags/GroovyEachParseTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/compiler/tags/GroovyEachTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/compiler/tags/GroovyFindAllTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/compiler/tags/GroovyGrepTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/compiler/tags/GroovySyntaxTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/GroovyPageWithJSPTagsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/GroovyPagesPageContextTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/IterativeJspTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/SimpleJspTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/SimpleTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/TagLibraryResolverTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/TldReaderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/gsp/jsp/WebXmlTagLibraryReaderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/plugins/web/CodecsGrailsPluginTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/plugins/web/rest/render/xml/DefaultXmlRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindStringArrayToGenericListTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindToEnumTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindToObjectWithEmbeddableTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindToPropertyThatIsNotReadableTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindXmlWithAssociationTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindingExcludeTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindingRequestMethodSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/BindingToNullableTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/CheckboxBindingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/ControllerActionParameterBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/DataBindingLazyMetaPropertyMapTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/DataBindingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/DefaultASTDatabindingHelperDomainClassSpecialPropertiesSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/EnumBindingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/GrailsParameterMapBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/JSONBindingToNullTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/JSONRequestToResponseRenderingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/NestedXmlBindingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/hal/json/HalJsonBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/hal/xml/HalXmlBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/json/JsonBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/json/JsonBindingWithExceptionHandlerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/binding/xml/XmlBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/codecs/CodecSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/commandobjects/ClassWithNoValidateMethod.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/commandobjects/CommandObjectInstantiationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/commandobjects/CommandObjectNullabilitySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/commandobjects/CommandObjectsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/commandobjects/NonValidateableCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/commandobjects/SomeValidateableClass.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/controllers/ContentNegotiationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/controllers/ControllerExceptionHandlerCompilationErrorsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/controllers/ControllerExceptionHandlerInheritanceSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/controllers/ControllerExceptionHandlerSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/controllers/ControllerMetaProgrammingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/converters/ControllerWithXmlConvertersTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/converters/ConverterConfigurationTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/converters/JSONArrayTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/converters/JSONConverterTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/converters/MarshallerRegistrarSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/converters/XMLConverterTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/includes/IncludeHandlingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/json/DomainClassCollectionRenderingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/json/JSONWriterSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/AbstractGrailsMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/AdditionalParamsMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/DoubleWildcardUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/DynamicActionNameEvaluatingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/DynamicParameterValuesTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/IdUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/OverlappingUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/RegexUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/ResponseCodeUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/RestfulMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/RestfulReverseUrlRenderingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/ReverseUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/ReverseUrlMappingToDefaultActionTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/RootUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/UrlMappingEvaluatorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/UrlMappingParameterTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/UrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/UrlMappingWithCustomValidatorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/UrlMappingsHolderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mapping/ViewUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/metaclass/CollectionBindDataMethodSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mime/ContentFormatControllerTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/mime/WithFormatContentTypeSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/AliasedTagPropertySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/ElvisAndClosureGroovyPageTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GSPResponseWriterSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageBindingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageLineNumberTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageMethodDispatchTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageMethodDispatchWithNamespaceTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageRenderingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageServletSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPageTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPagesIfTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/GroovyPagesWhitespaceParsingTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/InvokeTagWithCustomBodyClosureSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/ModifyOurScopeWithBodyTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/NewLineRenderingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/OptionalTagBodySpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/ReservedWordOverrideTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/SitemeshPreprocessorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/StaticContentRenderingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/TagLibMethodMissingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/TagLibNamespaceTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/TagLibWithGStringTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/pages/TagLibWithNullValuesTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/servlet/BindDataMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/servlet/GrailsFlashScopeSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/servlet/mvc/SynchronizerTokensHolderTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/servlet/view/GroovyPageViewTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ApplicationTagLibResourcesTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ApplicationTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ApplyCodecTagSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ControllerTagLibMethodDispatchSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ControllerTagLibMethodInheritanceSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/CoreTagsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/CountryTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/FormRenderingTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/FormTagLib2Tests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/FormTagLib3Tests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/FormTagLibResourceTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/FormTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/FormatTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/InvokeTagLibAsMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/InvokeTagLibWithBodyAsMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/JavascriptTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/LayoutWriterStackTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/LinkRenderingTagLib2Tests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/LinkRenderingTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/MessageTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/NamedTagBodyParamsTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/NamespacedNamedUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/NamespacedTagAndActionConflictTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/NamespacedTagLibMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/NamespacedTagLibRenderMethodTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/OverlappingReverseMappedLinkTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/PageScopeSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/PageScopeTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/PluginTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/RenderTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ReturnValueTagLibTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/SelectTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/TagLibraryDynamicPropertyTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/UploadFormTagTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test-suite-web/src/test/groovy/org/grails/web/taglib/ValidationTagLibSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/plugins/testing/AbstractGrailsMockHttpServletResponse.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/plugins/testing/GrailsMockHttpServletRequest.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/plugins/testing/GrailsMockHttpSession.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/io/MultiplexingOutputStream.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/io/SystemOutAndErrSwapper.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/spock/IntegrationSpecConfigurerExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/support/ControllerNameExtractor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/support/GrailsTestAutowirer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/support/GrailsTestInterceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/support/GrailsTestMode.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/support/GrailsTestRequestEnvironmentInterceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-test/src/main/groovy/org/grails/test/support/GrailsTestTransactionInterceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "grails-validation/src/main/groovy/org/codehaus/groovy/grails/validation/AbstractConstraint.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/main/groovy/org/codehaus/groovy/grails/validation/CascadingValidator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/main/groovy/org/codehaus/groovy/grails/validation/ConstrainedProperty.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/main/groovy/org/codehaus/groovy/grails/validation/Constraint.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/main/groovy/org/codehaus/groovy/grails/validation/ConstraintFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/test/groovy/grails/validation/ConstraintTypeMismatchSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/test/groovy/org/grails/validation/ConstrainedPropertyBuilderSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-validation/src/test/groovy/org/grails/validation/errors/ValidationErrorsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-boot/src/main/groovy/org/grails/boot/context/web/GrailsAppServletInitializer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-boot/src/main/groovy/org/grails/compiler/boot/BootInitializerClassInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-boot/src/test/groovy/grails/boot/EmbeddedContainerWithGrailsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-boot/src/test/groovy/grails/boot/GrailsSpringApplicationSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/util/GrailsWebMockUtil.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/web/api/ServletAttributes.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/web/api/WebAttributes.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/web/mime/MimeType.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/web/mime/MimeTypeProvider.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/web/mime/MimeTypeResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/grails/web/mime/MimeTypeUtils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/codehaus/groovy/grails/web/metaclass/ControllerDynamicMethods.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/codehaus/groovy/grails/web/pages/GroovyPagesUriService.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/codehaus/groovy/grails/web/servlet/FlashScope.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/codehaus/groovy/grails/web/servlet/GrailsApplicationAttributes.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/databinding/bindingsource/DataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/beans/PropertyEditorRegistryUtils.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/context/ServletEnvironmentGrailsApplicationDiscoveryStrategy.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/databinding/bindingsource/DataBindingSourceRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/errors/ErrorsViewStackTracePrinter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/i18n/ParamsAwareLocaleChangeInterceptor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/servlet/mvc/ActionResultTransformer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/servlet/view/CompositeViewResolver.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/main/groovy/org/grails/web/util/ClassAndMimeTypeRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-common/src/test/groovy/org/codehaus/groovy/grails/web/servlet/mvc/GrailsParameterMapTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, AssertStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/grails/compiler/traits/WebDataBindingTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/grails/web/databinding/DataBinder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/grails/web/databinding/GrailsWebDataBinder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/grails/web/databinding/WebDataBinding.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/DataBindingEventMulticastListener.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/GrailsWebDataBindingListener.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/SpringConversionServiceAdapter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/AbstractRequestBodyDataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/DefaultDataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/DefaultDataBindingSourceRegistry.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/HalGPathResultMap.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/HalJsonDataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/HalXmlDataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/JsonDataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/bindingsource/XmlDataBindingSourceCreator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/converters/AbstractStructuredBindingEditor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/main/groovy/org/grails/web/databinding/converters/ByteArrayMultipartFileValueConverter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/test/groovy/org/grails/web/databinding/bindingsource/HalGPathResultMapSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/test/groovy/org/grails/web/databinding/bindingsource/hal/json/HalJsonDataBindingSourceCreatorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-databinding/src/test/groovy/org/grails/web/databinding/bindingsource/json/JsonDataBindingSourceCreatorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-gsp-taglib/src/main/groovy/org/grails/plugins/web/taglib/RenderTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-gsp-taglib/src/main/groovy/org/grails/plugins/web/taglib/SitemeshTagLib.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-gsp/src/main/groovy/grails/gsp/PageRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-gsp/src/main/groovy/org/grails/web/pages/GroovyPageCompilerTask.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-gsp/src/test/groovy/org/grails/web/gsp/io/GrailsConventionGroovyPageLocatorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-gsp/src/test/groovy/org/grails/web/servlet/view/GroovyPageViewResolverSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/GroovyPagesJspFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/JspTagImpl.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/JspTagLibImpl.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/PageContextFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/TagLibraryResolverImpl.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/TldReader.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-jsp/src/main/groovy/org/grails/gsp/jsp/WebXmlTagLibraryReader.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-mvc/src/main/groovy/org/grails/web/servlet/mvc/GrailsDispatcherServlet.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-mvc/src/main/groovy/org/grails/web/servlet/mvc/SynchronizerTokensHolder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-mvc/src/main/groovy/org/grails/web/servlet/mvc/TokenResponseActionResultTransformer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-taglib/src/main/groovy/grails/artefact/TagLibrary.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-taglib/src/main/groovy/grails/artefact/gsp/TagLibraryInvoker.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-taglib/src/main/groovy/grails/compiler/traits/TagLibraryTraitInjector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-taglib/src/test/groovy/org/grails/web/taglib/TagLibraryLookupSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/grails/web/mapping/LinkGeneratorFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/grails/web/mapping/ResponseRedirector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/grails/web/mapping/UrlMappingsFactory.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/grails/web/mapping/reporting/UrlMappingsRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/codehaus/groovy/grails/web/mapping/LinkGenerator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/codehaus/groovy/grails/web/mapping/ResponseRedirector.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappings.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappingsHolder.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/codehaus/groovy/grails/web/mapping/mvc/RedirectEventListener.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/ControllerActionConventions.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/DefaultLinkGenerator.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/DefaultUrlMappings.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/ForwardUrlMappingInfo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/MetaMappingInfo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/ResponseCodeUrlMappingVisitor.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/mvc/AbstractGrailsControllerUrlMappings.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/mvc/GrailsControllerUrlMappingInfo.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/mvc/UrlMappingsHandlerMapping.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/mvc/UrlMappingsInfoHandlerAdapter.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/reporting/AnsiConsoleUrlMappingsRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/reporting/UrlMappingsReportCommand.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/main/groovy/org/grails/web/mapping/servlet/UrlMappingsErrorPageCustomizer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/AbstractUrlMappingsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/DefaultActionUrlMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/DefaultUrlCreatorTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/DoubleWildcardUrlMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/GroupedUrlMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/LinkGeneratorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/LinkGeneratorWithFormatSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/LinkGeneratorWithUrlMappingsSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/MandatoryParamMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/OverlappingParametersReverseMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/OverlappingUrlMappingsMatchingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/RegisterUrlMappingsAtRuntimeSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/RestfulResourceMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/StaticAndWildcardMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappingSizeConstraintSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappingsBindingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappingsHolderComparatorSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappingsWithHttpMethodSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/UrlMappingsWithOptionalExtensionSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/VersionedResourceMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/mapping/reporting/AnsiConsoleUrlMappingsRendererSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/codehaus/groovy/grails/web/servlet/mvc/UrlMappingsHandlerMappingSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web-url-mappings/src/test/groovy/org/grails/web/mapping/RegexUrlMappingTests.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/main/groovy/grails/web/servlet/plugins/GrailsWebPluginManager.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/main/groovy/org/grails/web/servlet/HttpServletRequestExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/main/groovy/org/grails/web/servlet/HttpServletResponseExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/main/groovy/org/grails/web/servlet/HttpSessionExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/main/groovy/org/grails/web/servlet/ServletContextExtension.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/test/groovy/org/codehaus/groovy/grails/web/context/ServletContextHolderSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-web/src/test/groovy/org/grails/web/servlet/ServletRequestXhrApiSpec.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, ReturnStatement, ForStatement, CaseStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)


    }


    @Unroll
    def "test separately for #path"() { // "and: ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)" will cause java.lang.OutOfMemoryError
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/grails-core-3/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config
        def moduleNodeNewAstStr = moduleNodeNew ? ASTWriter.astToString(moduleNodeNew) : null;
        def moduleNodeOld2AstStr = moduleNodeOld2 ? ASTWriter.astToString(moduleNodeOld2) : null;
        def astStrCompareResult = moduleNodeNewAstStr && moduleNodeOld2AstStr && (moduleNodeNewAstStr == moduleNodeOld2AstStr)

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        astStrCompareResult
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }
        where:
        path | config
        "grails-gradle-plugin/src/main/groovy/org/grails/gradle/plugin/core/GrailsGradlePlugin.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode, GenericsType], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-controllers/src/main/groovy/grails/artefact/controller/support/ResponseRenderer.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "grails-plugin-converters/src/main/groovy/org/grails/web/converters/AbstractParsingParameterCreationListener.groovy" | addIgnore([Parameter, IfStatement, ThrowStatement, ExpressionStatement, FieldNode, PropertyNode, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)

    }


    @Unroll
    def "test Groovy in Action 2nd Edition for #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/GroovyInAction2/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }

        where:
        path | config
        "appD/Listing_D_01_GStrings.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "appD/Listing_D_02_Lists.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "appD/Listing_D_03_Closures.groovy" | addIgnore([AssertStatement, Parameter], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "appD/Listing_D_04_Regex.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "appD/Listing_D_05_GPath.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap01/Listing_01_01_Gold.groovy" | addIgnore([AssertStatement, ReturnStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap01/snippet0101_customers.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap01/snippet0101_fileLineNumbers.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap01/snippet0101_printPackageNames.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap01/snippet0101_printPackageNamesGpath.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap01/snippet0102_printGroovyWebSiteCount.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap01/snippet0103_googleIpAdr.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap02/Book.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_01_Assertions.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_03_BookScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_04_BookBean.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_05_ImmutableBook.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_06_Grab.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_07_Clinks.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/Listing_02_08_ControlStructures.groovy" | addIgnore([AssertStatement, WhileStatement, ForStatement, BreakStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0201_comments.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0202_failing_assert.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0203_clinks_java.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0203_gstring.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0203_int_usage.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0203_map_usage.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0203_range_usage.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0203_roman.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0204_evaluate_jdk7_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0204_evaluate_jdk8_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap02/snippet0204_failing_typechecked.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap03/extra_escaped_characters_table36.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/extra_method_operators_table34.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/extra_numeric_literals_table32.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/extra_numerical_coercion_table310.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/extra_optional_typing_table33.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/extra_primitive_values_table31.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_01_PrimitiveMethodsObjectOperators.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_02_ListMapCast.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_03_DefiningOperators.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_04_DefiningGStrings.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_05_StringOperations.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_06_RegexGStrings.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_07_RegularExpressions.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_08_EachMatch.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_09_PatternReuse.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_10_PatternsClassification.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/Listing_03_11_NumberMethodsGDK.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0301_autoboxing.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0304_GString_internals.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0304_stringbuffer.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0305_matcher_each_group.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0305_matcher_groups.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0305_matcher_parallel_assignment.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0305_matcher_plain.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap03/snippet0306_GDK_methods_for_numbers.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap04/extra_EnumRange.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/extra_ListCast.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/extra_ListTable.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/extra_Map_as.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/extra_Map_group.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/extra_MaxMinSum.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/extra_SplitList.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_01_range_declarations.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_02_ranges_are_objects.groovy" | addIgnore([AssertStatement, ThrowStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_03_custom_ranges.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_04_list_declarations.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_05_list_subscript_operator.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_06_list_add_remove.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_07_lists_control_structures.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_08_list_content_manipulation.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_09_list_other_methods.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_10_list_quicksort.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_11_list_mapreduce.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_12_map_declarations.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_13_map_accessors.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_14_map_query_methods.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_15_map_iteration.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_16_map_content.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/Listing_04_17_map_example.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0402_ListAsSet.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0402_ListRemoveNulls.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0402_ListStreams_jdk8_plus.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0403_Map_Ctor_Expression.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0403_Map_Ctor_Unquoted.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0403_Map_MapReduce.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap04/snippet0403_Map_String_accessors.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap05/extra_Closure_delegate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/extra_Closure_myWith.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/extra_ClosureProperty.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_01_closure_simple_declaration.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_02_simple_method_closure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_03_multi_method_closure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_04_closure_all_declarations.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_05_simple_closure_calling.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_06_calling_closures.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_07_simple_currying.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_08_logging_curry_example.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_09_closure_scope.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_10_closure_accumulator.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/Listing_05_11_visitor_pattern.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0501_envelope.groovy.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0504_closure_default_params.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0504_closure_isCase.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0504_closure_paramcount.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0505_map_with.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0505_scoping.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0506_closure_return.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0507_closure_composition.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0508_memoize.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap05/snippet0509_trampoline.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap06/extra_if_return.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/extra_in_operator.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/extra_switch_return.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_01_groovy_truth.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_02_assignment_bug.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_03_if_then_else.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_04_conditional_operator.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_05_switch_basic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_06_switch_advanced.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_07_assert_host.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_08_while.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_09_for.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_10_break_continue.groovy" | addIgnore([AssertStatement, ContinueStatement], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/Listing_06_11_exception_example.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0602_bad_file_read.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0602_bad_file_read_with_message.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0602_failing_assert.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0603_each_loop_iterate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0603_file_iterate_lines.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0603_for_loop_iterate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0603_null_iterate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0603_object_iterate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0603_regex_iterate_match.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap06/snippet0604_multicatch.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap07/business/Vendor.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_01_Declaring_Variables.groovy" | addIgnore([AssertStatement, MethodNode], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_02_TypeBreaking_Assignment.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_03_Referencing_Fields.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_04_Overriding_Field_Access.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_05_Declaring_Methods.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_06_Declaring_Parameters.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_07_Parameter_Usages.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_08_Safe_Dereferencing.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_09_Instantiation.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_10_Instantiation_Named.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_11_Classes.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_13_Import.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_14_Import_As_BugFix.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_15_Import_As_NameClash.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_16_Multimethods.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_17_MultiEquals.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_18_Traits.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_19_Declaring_Beans.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_20_Calling_Beans.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_21_Calling_Beans_Advanced.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_22_Property_Methods.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_23_Expando.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/Listing_07_24_GPath.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/snippet0703_Implicit_Closure_To_SAM.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/snippet0705_Spread_List.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/snippet0705_Spread_Map.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/snippet0705_Spread_Range.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/thirdparty/MathLib.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap07/thirdparty2/MathLib.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap08/custom/Custom.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/custom/useCustom.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/failing_Listing_08_15_EMC_static.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/failing_Listing_08_16_EMC_super.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/groovy/runtime/metaclass/custom/CustomMetaClass.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_01_method_missing.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_02_mini_gorm.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_03_property_missing.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_04_bin_property.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_05_closure_dynamic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_06_property_method.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_07_MetaClass_jdk7_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_07_MetaClass_jdk8_plus.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_08_ProxyMetaClass.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_09_Expando.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_10_EMC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_11_EMC_Groovy_Class.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_12_EMC_Groovy_Object.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_13_EMC_Java_Object.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_14_EMC_Builder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_15_EMC_static.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_16_EMC_super.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_17_EMC_hooks.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_18_Existing_Categories.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_19_Marshal.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_20_MarshalCategory.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_21_Test_Mixin.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_22_Sieve_Mixin.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_23_Millimeter.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_24_create_factory.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_25_fake_assign.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_26_restore_emc.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap08/Listing_08_27_intercept_cache_invoke.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap09/Listing_09_01_ToStringDetective.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_02_ToStringSleuth.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_03_EqualsAndHashCode.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_04_TupleConstructor.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_05_Lazy.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_06_IndexedProperty.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_07_InheritConstructors.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_08_Sortable.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_09_Builder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_10_Canonical.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_11_Immutable.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_12_Delegate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_13_Singleton.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_14_Memoized.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_15_TailRecursive.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_16_Log.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_17_Synchronized.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_18_SynchronizedCustomLock.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_19_ReadWriteLock.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_20_AutoClone.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_21_AutoCloneCopyConstructor.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_22_AutoExternalize.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_23_TimedInterrupt.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_24_ThreadInterrupt.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_25_ConditionalInterrupt.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_26_Field.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_27_BaseScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_28_AstByHand.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_29_AstByHandWithUtils.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_30_AstBuildFromSpec.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_31_AstBuildFromString.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_32_AstBuildFromStringMixed.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_33_AstBuildFromCode.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_34_GreeterMainTransform.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_35_GreeterMainTransform2.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_38_AstTesting1.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_39_AstTesting2.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_40_AstTesting3.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_41_AstTesting4.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/Listing_09_42_AstTesting5.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/settings.gradle" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_autoCloneDefault.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_autoCloneSerialization.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_autoExternalize.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_fieldEquivalent.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_mapCreation.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_noisySetDelegateByHand.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_noisySetInheritance.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_nonTailCallReverseList.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_readWriteByHand.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_readWriteLock.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_singletonByHand.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0902_toStringEquivalent.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0903_greeterExpanded.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0903_greeterScript.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0903_localMain.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0903_localMainTransformation.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/snippet0905_GetCompiledTimeScript.txt" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/src/main/groovy/regina/CompiledAtASTTransformation.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap09/src/test/groovy/regina/CompiledAtASTTransformationTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap10/extra1004_RuntimeGroovyDispatch.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_01_Duck.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_02_failing_Typo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_03_ClassTC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_04_OneMethodTC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_05_CompileTimeTypo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_06_MethodNameTypo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_07_MethodArgsFlipped.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_08_InvalidAssignments.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_09_AssignmentsWithCoercion.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_10_DefField.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_11_InPlaceList.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_12_Generics.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_13_ListStyleCtorRuntime.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_14_ListStyleCtorTC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_15_MapStyleCtorBad.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_16_ListStyleCtor.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_17_ListStyleCtorFixed.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_18_CodeAsData.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_19_ClosuresBadReturnType.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_20_UserValidation.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_21_UserValidationTC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_22_UserValidation_ExplicitTypes.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_23_UserValidation_SAM.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_24_UserValidation_ClosureParams.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_25_UserValidation_DSL.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_26_UserValidation_DelegatesTo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_27_UserValidation_DelegatesToTarget.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_28_Category.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_29_EMC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_30_Builder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_31_MixedTypeChecking.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_32_Skip.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_33_FlowTyping.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_34_FlowTypingOk.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_35_LUB.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_36_Condition.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_37_ClosureSharedVar.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_38_LubError.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_39_LubOk.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_40_FibBench.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_42_StaticCompileDispatch.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_43_MonkeyPatching.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_44_BookingDSL.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_45_MultiValidation.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_46_RobotExtension.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/Listing_10_47_SQLExtension.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/snippet1003_GroovyGreeter.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/snippet1005_RobotMainTC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/snippet1005_SqlMainTC.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap10/User.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap11/Listing_11_03_MarkupBuilderPlain.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_04_NodeBuilder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_05_NodeBuilderLogic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_06_MarkupBuilderLogic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_07_MarkupBuilderHtml.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_08_StreamingMarkupBuilderLogic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_10_PW_SwingBuilder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_11_Swing_Widgets.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_12_Swing_Layout.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_13_Table_Demo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_14_Binding.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_15_Plotter.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_16_Groovyfx.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_17_CalorieCounterBuilderSupport.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_18_CalorieCounterFactoryBuilderSupport.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/Listing_11_19_CalorieCounterByHand.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/snippet1103_MarkupWithHyphen.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/snippet1106_AntBuilderIf.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap11/snippet1107_Printer.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap12/Listing_12_01_info_jdk6_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_01_info_jdk7_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_01_info_jdk8_plus.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_02_properties.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_03_File_Iteration.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_04_Filesystem.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_05_Traversal.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_06_File_Read.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_07_File_Write.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_08_Writer_LeftShift.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_09_File_Transform_jdk7_plus.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_10_File_ObjectStreams.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_11_Temp_Dir.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_12_Threads.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_13_Processes_UnixCommands.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_14_Processes_ZipUnzip.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_15_SimpleTemplateEngine.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_16_GroovletExample.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_17_HelloWorldGroovlet.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_19_InspectGroovlet.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_20_HiLowGame.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/Listing_12_22_TemplateGroovlet.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/snippet1201_SlowTyping.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/snippet1201_UseCategory.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap12/snippet1202_base64.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap13/extra_NeoGremlinGraph.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/layering/AthleteApplication.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/layering/AthleteDAO.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/layering/DataAccessObject.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/layering/DbHelper.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_01_Connecting.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_02_ConnectingDataSource.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_03_Creating.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_05_Inserting.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_06_Reading.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_07_Updating.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_08_Delete.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_09_Transactions.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_10_Batching.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_11_Paging.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_12_Metadata.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_13_MoreMetadata.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_14_NamedOrdinal.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_15_StoredProcBasic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_16_StoredProcParam.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_17_StoredProcInOut.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_18_DataSetBasics.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_19_DataSetFiltering.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_20_DataSetViews.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_25_AthleteAppMain.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_26_AthleteAppTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_27_MongoAthletes.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_28_NeoAthletes.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/Listing_13_29_NeoGremlin.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ConnectingWithGrab.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ConnectingWithInstance.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ConnectingWithMap.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ReadEachRow.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ReadEachRowList.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ReadQuery.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/snippet1301_ReadRows.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/util/DbUtil.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/util/MarathonRelationships.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap13/util/Neo4jUtil.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap14/Listing_14_02_DOM.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_03_DOM_Category.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_04_XmlParser.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_05_XmlSlurper.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_06_SAX.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_07_StAX.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_08_XmlBoiler.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_09_XmlStreamer.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_10_StreamedHtml.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_11_UpdateDomCategory.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_12_UpdateParser.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_13_UpdateSlurper.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_14_XPath.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_16_XPathTemplate.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_17_JsonParser.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_18_JsonBuilder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_19_JsonBuilderLogic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/Listing_14_20_JsonOutputAthlete.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap14/UpdateChecker.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap15/Listing_15_01_RSS_bbcnews.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_02_ATOM_devworks.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_03_REST_jira_url.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_04_REST_jira_httpb_get.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_05_REST_currency_httpb_get.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_06_REST_currency_httpb_post.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_07_REST_currency_jaxrs.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_08_REST_currency_jaxrs_proxy.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_09_XMLRPC_echo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_10_XMLRPC_jira.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_11_SOAP_wsdl.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_12_SOAP11_currency_url.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_13_SOAP12_currency_httpb.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_14_SOAP11_currency_wslite.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap15/Listing_15_15_SOAP12_currency_wslite.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap16/Listing_16_01_HelloIntegration.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_03_MultilineScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_04_UsingEval.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_05_Binding.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_06_BindingTwoWay.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_07_ClassInScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_08_Payment_calculator.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_09_MethodsInBinding.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/Listing_16_12_BeanToString.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/shapes/Circle.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/shapes/MaxAreaInfo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/spring/groovy/Circle.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap16/spring/groovy/MaxAreaInfo.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap17/automation/src/main/groovy/Calculator.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/automation/src/test/groovy/CalculatorTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/cobertura/src/main/groovy/BiggestPairCalc.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/cobertura/src/main/groovy/BiggestPairCalcFixed.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/cobertura/src/test/groovy/BiggestPairCalcTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Converter.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Counter.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
//FIXME        "chap17/extra_ParameterizedTestNG.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/extra_TestNG.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Farm.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_01_Celsius.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_02_CounterTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_03_HashMapTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_04_GroovyTestSuite.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_05_AllTestSuite.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_06_DataDrivenJUnitTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_07_PropertyBased.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_08_Balancer.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_09_BalancerStub.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_10_BalancerMock.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_11_LoggingCounterTest.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_12_JUnitPerf.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_13_SpockSimple.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_14_SpockMock.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_15_SpockMockWildcards.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_16_SpockMockClosureChecks.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Listing_17_17_SpockDataDriven.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/LoggingCounter.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/MovieTheater.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/Purchase.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/snippet1701_JUnit4.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap17/snippet1704_listPropertyCheck.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap18/Listing_18_01_ConcurrentSquares.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_02_ConcurrentSquaresTransparent.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_03_ConcurrentSquaresTransitive.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_04_MapFilterReduce.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_05_SquaresMapReduce.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_06_Dataflow.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_07_DataflowStreams.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_08_Actors.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_09_ActorsLifecycle.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_10_ActorsMessageAware.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_11_Agent.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_13_YahooForkJoin.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_14_YahooMapReduce.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/Listing_18_15_YahooDataflow.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/snippet1801_startThread.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/snippet1803_java_parallel_streams_jdk8_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/snippet1804_deadlock.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/snippet1804_nondeterministic.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap18/YahooService.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap19/FetchOptions.groovy" | addIgnore([AssertStatement, ConstructorNode] , ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/FetchOptionsBuilder.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_06_Binding.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_29_OrderDSL.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_30_WhenIfControlStructure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_31_Until_failing_.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_32_UntilControlStructure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_39_GivenWhenThen.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_43_FetchOptionsScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_44_RubyStyleNewify.groovy" | addIgnore([AssertStatement, Token], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_45_PythonStyleNewify.groovy" | addIgnore([AssertStatement, Token], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_46_Terms.groovy" | addIgnore([AssertStatement, Token], ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_48_No_IO.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_49_ArithmeticShell.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_50_TimedInterrupt.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_51_SystemExitGuard.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Listing_19_53_QueryCustomizer.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/Query.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/extra_FetchOptions_traditional.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v01/Listing_19_01_SelfContainedScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_04_MainSimple.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_05_MainGroovyShell.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_07_MainBinding.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_08_MainDirectionConstants.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_09_MainDirectionsSpread.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_10_MainImplicitMethod.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_12_MainBaseScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_13_MainImportCustomizer.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_14_MainCustomBaseScriptClass.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_16_MainMethodClosure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/Listing_19_19_MainLowerCase.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/integration/CaseRobotBaseScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/integration/CustomBinding.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/integration/RobotBaseScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/model/Direction.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/model/Robot.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v02/snippet1901_MainFileRunner.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/Listing_19_27_SimpleCommandChain.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/Listing_19_40_Robot_With.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/integration/DistanceCategory.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/integration/RobotBaseScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/integration/SuperBotBaseScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/Direction.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/Distance.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/Duration.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/Robot.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/Speed.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/SuperBot.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/v03/model/Unit.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/xform/BusinessLogicScript.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/xform/CustomControlStructure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/xform/Listing_19_36_WhenTransformation.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/xform/WhenUntilTransform.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/xform/extra_WhenTransformationWorksWithoutBraces.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap19/xform/snippet1906_WhenUntilXform_Structure.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

        "chap20/Listing_20_01_Grapes_for_twitter_urls.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap20/Listing_20_02_Scriptom_Windows_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap20/Listing_20_03_ActivX_Windows_only.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap20/Listing_20_10_SquaringMapValue.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap20/Listing_20_11_Synchronized.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)
        "chap20/Listing_20_12_DbC_invariants.groovy" | addIgnore(AssertStatement, ASTComparatorCategory.LOCATION_IGNORE_LIST)

    }



    @Unroll
    def "test comments for #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/$path")
        def moduleNodeNew = new Main(Configuration.NEW).process(file)
        def moduleNodeOld = new Main(Configuration.OLD).process(file)
        def moduleNodeOld2 = new Main(Configuration.OLD).process(file)
        config = config.is(_) ? ASTComparatorCategory.DEFAULT_CONFIGURATION : config
        List<ClassNode> classes = new LinkedList(moduleNodeNew.classes).sort { c1, c2 -> c1.name <=> c2.name }

        expect:
        moduleNodeNew
        moduleNodeOld
        ASTComparatorCategory.apply(config) {
            assert moduleNodeOld == moduleNodeOld2
        }
        and:
        ASTWriter.astToString(moduleNodeNew) == ASTWriter.astToString(moduleNodeOld2)
        and:
        ASTComparatorCategory.apply(config) {
            assert moduleNodeNew == moduleNodeOld, "Fail in $path"
        }
        and:
        classes[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')            == '/** * test class Comments */'
        and:
        classes[0].fields[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')  == '/**     * test Comments.SOME_VAR     */'
        and:
        classes[0].fields[1].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')  == '/**     * test Comments.SOME_VAR2     */'
        and:
        classes[0].declaredConstructors[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '') == '/**     * test Comments.constructor1     */'
        and:
        classes[0].methods[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '') == '/**     * test Comments.m1     */'
        and:
        classes[0].methods[1].nodeMetaData[ASTBuilder.DOC_COMMENT] == null
        and:
        classes[0].methods[2].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '') == '/**     * test Comments.m3     */'

        and:
        classes[1].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')            == '/**     * test class InnerClazz     */'
        and:
        classes[1].fields[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')  == '/**         * test InnerClazz.SOME_VAR3         */'
        and:
        classes[1].fields[1].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')  == '/**         * test InnerClazz.SOME_VAR4         */'
        and:
        classes[1].methods[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '') == '/**         * test Comments.m4         */'
        and:
        classes[1].methods[1].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '') == '/**         * test Comments.m5         */'

        and:
        classes[2].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')            == '/**     * test class InnerEnum     */'
        and:
        classes[2].fields[0].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')  == '/**         * InnerEnum.NEW         */'
        and:
        classes[2].fields[1].nodeMetaData[ASTBuilder.DOC_COMMENT].replaceAll(/\r?\n/, '')  == '/**         * InnerEnum.OLD         */'

        where:
        path | config
        "Comments.groovy" | _

    }


    @Unroll
    def "test by evaluating script: #path"() {
        def filename = path;

        setup:
        def file = new File("$RESOURCES_PATH/$path")
        def gsh = createGroovyShell(compilerConfiguration)


        expect:
        assertScript(gsh, file);

        where:
        path | compilerConfiguration
        "Assert_issue9.groovy" | CompilerConfiguration.DEFAULT
        "CallExpression_issue33_5.groovy" | CompilerConfiguration.DEFAULT
    }


    @Unroll
    def "test invalid files #path"() {
        when:
            def file = new File("$RESOURCES_PATH/$path")
        then:
            ! canLoad(file, Configuration.NEW) && ! canLoad(file, Configuration.OLD)
        where:
            path | output
            "Statement_Errors_1.groovy" | _
            "Statement_Errors_2.groovy" | _
            "Statement_Errors_3.groovy" | _
            "Statement_Errors_4.groovy" | _
            "Statement_Errors_5.groovy" | _
            "Statement_Errors_6.groovy" | _
            "Statement_Errors_7.groovy" | _
            "Statement_Errors_8.groovy" | _
            "Statement_Errors_9.groovy" | _
            "Statement_Errors_10.groovy" | _
            "ClassModifiersInvalid_Issue1_2.groovy" | _
            "ClassModifiersInvalid_Issue2_2.groovy" | _

    }


    def addIgnore(Class aClass, ArrayList<String> ignore, Map<Class, List<String>> c = null) {
        c = c ?: ASTComparatorCategory.DEFAULT_CONFIGURATION.clone() as Map<Class, List<String>>;
        c[aClass].addAll(ignore)
        c
    }

    def addIgnore(Collection<Class> aClass, ArrayList<String> ignore, Map<Class, List<String>> c = null) {
        c = c ?: ASTComparatorCategory.DEFAULT_CONFIGURATION.clone() as Map<Class, List<String>>;
        aClass.each { c[it].addAll(ignore) }
        c
    }

    boolean canLoad(File file, Configuration config) {
        def module = new Main(config).process(file)
        return module != null && ! module.context.errorCollector.hasErrors()
    }

    def createGroovyShell(CompilerConfiguration c) {
        CompilerConfiguration configuration = new CompilerConfiguration(c)
        configuration.pluginFactory = new Antlrv4PluginFactory()

        return new GroovyShell(configuration);
    }

    def assertScript(gsh, file) {
        def content = file.text;
        try {
            gsh.evaluate(content);

            log.info("Evaluated $file")

            return true;
        } catch (Throwable t) {
            log.info("Failed $file: ${t.getMessage()}");

            return false;
        }
    }
}

