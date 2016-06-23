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
package org.codehaus.groovy.parser.antlr4;

import groovy.lang.Closure;
import groovy.lang.IntRange;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.*;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.antlr.EnumHelper;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.parser.antlr4.util.StringUtil;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.syntax.Numbers;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Types;
import org.objectweb.asm.Opcodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.*;

@SuppressWarnings("all")
public class ASTBuilder {
    public static final String GROOVY_TRANSFORM_TRAIT = "groovy.transform.Trait";
    public static final String DOC_COMMENT = "docComment";
    public static final String DOC_COMMENT_PREFIX = "/**";
    public static final String ABSTRACT = "abstract";
    public static final String DEF = "def";
    public static final String CALL = "call";
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";
    public static final String PROTECTED = "protected";

    public ASTBuilder(final SourceUnit sourceUnit, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.sourceUnit = sourceUnit;
        this.moduleNode = new ModuleNode(sourceUnit);

        String text = this.readSourceCode(sourceUnit);

        if (log.isLoggable(Level.FINE)) {
            this.logTokens(text);
        }

        this.lexer = new GroovyLangLexer(new ANTLRInputStream(text));
        this.parser = new GroovyLangParser(new CommonTokenStream(lexer));

    }

    public ModuleNode buildAST() {
        this.parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        this.parser.removeErrorListeners();
        this.parser.setErrorHandler(new BailErrorStrategy());

        try {
            this.startParsing(this.parser);
        } catch (RuntimeException e) {
            if (log.isLoggable(Level.FINE)) {
                log.fine(e.getMessage());
            }

            ((CommonTokenStream) this.parser.getInputStream()).reset();
            this.setupErrorListener(this.parser);
            this.parser.setErrorHandler(new DefaultErrorStrategy());
            this.parser.getInterpreter().setPredictionMode(PredictionMode.LL);

            this.startParsing(this.parser);
        }

        this.addClasses();

        return this.moduleNode;
    }

    private void startParsing(GroovyLangParser parser) {
        GroovyLangParser.CompilationUnitContext tree = parser.compilationUnit();

        if (log.isLoggable(Level.FINE)) {
            this.logTreeStr(tree);
        }

        int cnt = 0;
        try {
            for (GroovyLangParser.ImportStatementContext it : tree.importStatement()) {
                parseImportStatement(it);
            }

            for (ParseTree it : tree.children) {
                if (it instanceof GroovyLangParser.ClassDeclarationContext) {
                    parseClassDeclaration((GroovyLangParser.ClassDeclarationContext)it);

                    cnt++;
                } else if (it instanceof GroovyLangParser.PackageDefinitionContext) {
                    parsePackageDefinition((GroovyLangParser.PackageDefinitionContext)it);
                }
            }

            for (GroovyLangParser.ScriptPartContext part : tree.scriptPart()) {
                if (part.statement() != null) {
                    unpackStatement(moduleNode, parseStatement(part.statement()));
                } else {
                    moduleNode.addMethod(parseScriptMethod(part.methodDeclaration()));
                }

                cnt++;
            }

            if (0 == cnt) {
                this.addEmptyReturnStatement();
                return;
            }
        } catch (CompilationFailedException e) {
            log.severe(createExceptionMessage(e));

            throw e;
        }
    }

    public void parseImportStatement(GroovyLangParser.ImportStatementContext ctx) {
        ImportNode node;
        List<TerminalNode> qualifiedClassName = new LinkedList<TerminalNode>(ctx.IDENTIFIER());
        boolean isStar = ctx.MULT() != null;
        boolean isStatic = ctx.KW_STATIC() != null;
        String alias = (ctx.KW_AS() != null) ? DefaultGroovyMethods.pop(qualifiedClassName).getText() : null;
        List<AnnotationNode> annotations = parseAnnotations(ctx.annotationClause());

        if (isStar) {
            if (isStatic) {
                // import is like "import static foo.Bar.*"
                // packageName is actually a className in this case
                ClassNode type = ClassHelper.make(DefaultGroovyMethods.join(qualifiedClassName, "."));
                moduleNode.addStaticStarImport(type.getText(), type, annotations);

                node = DefaultGroovyMethods.last(moduleNode.getStaticStarImports().values());
            } else {
                // import is like "import foo.*"
                moduleNode.addStarImport(DefaultGroovyMethods.join(qualifiedClassName, ".") + ".", annotations);

                node = DefaultGroovyMethods.last(moduleNode.getStarImports());
            }

            if (alias != null) throw new GroovyBugError(
                    "imports like 'import foo.* as Bar' are not " +
                            "supported and should be caught by the grammar");
        } else {
            if (isStatic) {
                // import is like "import static foo.Bar.method"
                // packageName is really class name in this case
                String fieldName = DefaultGroovyMethods.pop(qualifiedClassName).getText();
                ClassNode type = ClassHelper.make(DefaultGroovyMethods.join(qualifiedClassName, "."));
                moduleNode.addStaticImport(type, fieldName, alias != null ? alias : fieldName, annotations);

                node = DefaultGroovyMethods.last(moduleNode.getStaticImports().values());
            } else {
                // import is like "import foo.Bar"
                ClassNode type = ClassHelper.make(DefaultGroovyMethods.join(qualifiedClassName, "."));
                if (alias == null) {
                    alias = DefaultGroovyMethods.last(qualifiedClassName).getText();
                }
                moduleNode.addImport(alias, type, annotations);

                node = DefaultGroovyMethods.last(moduleNode.getImports());
            }

        }

        setupNodeLocation(node, ctx);
    }


    public void parsePackageDefinition(GroovyLangParser.PackageDefinitionContext ctx) {
        moduleNode.setPackageName(DefaultGroovyMethods.join(ctx.IDENTIFIER(), ".") + ".");
        attachAnnotations(moduleNode.getPackage(), ctx.annotationClause());
        setupNodeLocation(moduleNode.getPackage(), ctx);
    }

    private void unpackStatement(ModuleNode destination, Statement stmt) {
        if (stmt instanceof DeclarationList) {
            for (DeclarationExpression decl : ((DeclarationList)stmt).declarations) {
                destination.addStatement(setupNodeLocation(new ExpressionStatement(decl), decl));
            }
        } else {
            destination.addStatement(stmt);
        }
    }

    private void unpackStatement(BlockStatement blockStatement, Statement stmt) {
        if (stmt instanceof DeclarationList) {
            String label = stmt.getStatementLabel();

            for (DeclarationExpression decl : ((DeclarationList)stmt).declarations) {
                Statement declarationStatement = new ExpressionStatement(decl);

                if (null != label) {
                    declarationStatement.setStatementLabel(label);
                }

                blockStatement.addStatement(setupNodeLocation(declarationStatement, decl));
            }
        } else {
            blockStatement.addStatement(stmt);
        }
    }

    /**
     *
     * @param isAnnotationDeclaration   whether the method is defined in an annotation
     * @param hasAnnotation             whether the method declaration has annotations
     * @param hasVisibilityModifier     whether the method declaration contains visibility modifier(e.g. public, protected, private)
     * @param hasModifier               whether the method declaration has modifier(e.g. visibility modifier, final, static and so on)
     * @param hasReturnType             whether the method declaration has an return type(e.g. String, generic types)
     * @param hasDef                    whether the method declaration using def keyword
     * @return                          the result
     *
     */
    private boolean isSyntheticPublic(boolean isAnnotationDeclaration, boolean hasAnnotation, boolean hasVisibilityModifier, boolean hasModifier, boolean hasReturnType, boolean hasDef) {

        if (hasVisibilityModifier) {
            return false;
        }

        if (isAnnotationDeclaration) {
            return true;
        }

        if (hasDef && hasReturnType) {
            return true;
        }

        if (hasModifier || hasAnnotation || !hasReturnType) {
            return true;
        }

        return false;
    }

    private MethodNode parseMethodDeclaration(ClassNode classNode, GroovyLangParser.MethodDeclarationContext ctx, Closure<MethodNode> createMethodNode) {
        //noinspection GroovyAssignabilityCheck
        final Iterator<Object> iterator = parseModifiers(ctx.memberModifier(), Opcodes.ACC_PUBLIC).iterator();
        int modifiers = ((Integer)(iterator.hasNext() ? iterator.next() : Opcodes.ACC_PUBLIC));

        boolean isAnnotationDeclaration = null != classNode
                && ClassHelper.Annotation_TYPE.equals(classNode.getInterfaces().length > 0 ? classNode.getInterfaces()[0] : null);
        boolean hasVisibilityModifier = ((Boolean)(iterator.hasNext() ? iterator.next() : false));
        boolean hasModifier = 0 != ctx.memberModifier().size();
        boolean hasAnnotation = 0 != ctx.annotationClause().size();
        boolean hasReturnType = (asBoolean(ctx.typeDeclaration()) && !DEF.equals(ctx.typeDeclaration().getText()))
                || asBoolean(ctx.genericClassNameExpression());
        boolean hasDef = asBoolean(ctx.KW_DEF);

        innerClassesDefinedInMethodStack.add(new LinkedList<InnerClassNode>());
        Statement statement = asBoolean(ctx.blockStatementWithCurve())
                ? parseBlockStatementWithCurve(ctx.blockStatementWithCurve())
                : null;

        Parameter[] params = parseParameters(ctx.argumentDeclarationList());

        ClassNode returnType = asBoolean(ctx.typeDeclaration())
                ? parseTypeDeclaration(ctx.typeDeclaration())
                : asBoolean(ctx.genericClassNameExpression())
                ? parseExpression(ctx.genericClassNameExpression())
                : ClassHelper.OBJECT_TYPE;

        ClassNode[] exceptions = parseThrowsClause(ctx.throwsClause());


        String methodName = (null != ctx.IDENTIFIER()) ? ctx.IDENTIFIER().getText() : parseString(ctx.STRING());

        final MethodNode methodNode = createMethodNode.call(classNode, ctx, methodName, modifiers, returnType, params, exceptions, statement);

        for (InnerClassNode it : innerClassesDefinedInMethodStack.pop()) {
            it.setEnclosingMethod(methodNode);
        }

        setupNodeLocation(methodNode, ctx);
        attachAnnotations(methodNode, ctx.annotationClause());
        methodNode.setSyntheticPublic(isSyntheticPublic(isAnnotationDeclaration, hasAnnotation, hasVisibilityModifier, hasModifier, hasReturnType, hasDef));
        methodNode.setSynthetic(false); // user-defined method are not synthetic

        return methodNode;
    }


    public MethodNode parseScriptMethod(final GroovyLangParser.MethodDeclarationContext ctx) {

        return parseMethodDeclaration(null, ctx, new Closure<MethodNode>(this, this) {
                    public MethodNode doCall(ClassNode classNode, GroovyLangParser.MethodDeclarationContext ctx, String methodName, int modifiers, ClassNode returnType, Parameter[] params, ClassNode[] exceptions, Statement statement) {

                        final MethodNode methodNode = new MethodNode(methodName, modifiers, returnType, params, exceptions, statement);
                        methodNode.setGenericsTypes(parseGenericDeclaration(ctx.genericDeclarationList()));
                        methodNode.setAnnotationDefault(true);

                        return methodNode;
                    }
                }
        );
    }

    public ClassNode parseClassDeclaration(final GroovyLangParser.ClassDeclarationContext ctx) {
        boolean isEnum = asBoolean(ctx.KW_ENUM());

        final ClassNode outerClass = asBoolean(classNodeStack) ? classNodeStack.peek() : null;
        ClassNode[] interfaces = asBoolean(ctx.implementsClause())
                ? collect(ctx.implementsClause().genericClassNameExpression(), new Closure<ClassNode>(this, this) {
            public ClassNode doCall(GroovyLangParser.GenericClassNameExpressionContext it) {
                return parseExpression(it);
            }
        }).toArray(new ClassNode[0])
                : new ClassNode[0];

        ClassNode classNode;
        String packageName = moduleNode.getPackageName();
        packageName = packageName != null && asBoolean(packageName) ? packageName : "";

        if (isEnum) {
            classNode = EnumHelper.makeEnumNode(asBoolean(outerClass) ? ctx.IDENTIFIER().getText() : packageName + ctx.IDENTIFIER().getText(), Modifier.PUBLIC, interfaces, outerClass);
        } else {
            if (outerClass != null) {
                String name = outerClass.getName() + "$" + String.valueOf(ctx.IDENTIFIER());
                classNode = new InnerClassNode(outerClass, name, Modifier.PUBLIC, ClassHelper.OBJECT_TYPE);
            } else {
                classNode = new ClassNode(packageName + String.valueOf(ctx.IDENTIFIER()), Modifier.PUBLIC, ClassHelper.OBJECT_TYPE);
            }
        }

        setupNodeLocation(classNode, ctx);

        if (asBoolean(ctx.KW_TRAIT())) {
            attachTraitTransformAnnotation(classNode);
        }

        attachAnnotations(classNode, ctx.annotationClause());

//        moduleNode.addClass(classNode);
        classes.add(classNode);
        if (asBoolean(ctx.extendsClause())) {
            if (asBoolean(ctx.KW_INTERFACE()) && !asBoolean(ctx.AT())) { // interface(NOT annotation)
                List<ClassNode> interfaceList = new LinkedList<ClassNode>();
                for (GroovyLangParser.GenericClassNameExpressionContext genericClassNameExpressionContext : ctx.extendsClause().genericClassNameExpression()) {
                    interfaceList.add(parseExpression(genericClassNameExpressionContext));
                }
                (classNode).setInterfaces(interfaceList.toArray(new ClassNode[0]));
                (classNode).setSuperClass(ClassHelper.OBJECT_TYPE);
            } else {
                (classNode).setSuperClass(parseExpression(ctx.extendsClause().genericClassNameExpression(0)));
            }

        }

        if (asBoolean(ctx.implementsClause())) {
            (classNode).setInterfaces(interfaces);
        }


        if (!isEnum) {
            (classNode).setGenericsTypes(parseGenericDeclaration(ctx.genericDeclarationList()));
            (classNode).setUsingGenerics((classNode.getGenericsTypes() != null && classNode.getGenericsTypes().length != 0) || (classNode).getSuperClass().isUsingGenerics() || DefaultGroovyMethods.any(classNode.getInterfaces(), new Closure<Boolean>(this, this) {
                public Boolean doCall(ClassNode it) {return it.isUsingGenerics();}
            }));
        }


        classNode.setModifiers(parseClassModifiers(ctx.classModifier()) |
                (isEnum ? (Opcodes.ACC_ENUM | Opcodes.ACC_FINAL)
                        : ((asBoolean(ctx.KW_INTERFACE())
                        ? Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT
                        : 0)
                )
                )
        );

        classNode.setSyntheticPublic((classNode.getModifiers() & Opcodes.ACC_SYNTHETIC) != 0);
        classNode.setModifiers(classNode.getModifiers() & ~Opcodes.ACC_SYNTHETIC);// FIXME Magic with synthetic modifier.


        if (asBoolean(ctx.AT())) {
            classNode.addInterface(ClassHelper.Annotation_TYPE);
            classNode.setModifiers(classNode.getModifiers() | Opcodes.ACC_ANNOTATION);
        }


        classNodeStack.add(classNode);
        parseClassBody(classNode, ctx.classBody());
        classNodeStack.pop();

        if (classNode.isInterface()) { // FIXME why interface has null mixin
            try {
                // FIXME Hack with visibility.
                Field field = ClassNode.class.getDeclaredField("mixins");
                field.setAccessible(true);
                field.set(classNode, null);
            } catch (IllegalAccessException e) {
                log.warning(createExceptionMessage(e));
            } catch (NoSuchFieldException e) {
                log.warning(createExceptionMessage(e));
            }
        }

        this.attachDocCommentAsMetaData(classNode, ctx);

        return classNode;
    }

    private Expression createEnumConstantInitExpression(GroovyLangParser.ArgumentListContext ctx) {
        if (!asBoolean(ctx)) {
            return null;
        }

        TupleExpression argumentListExpression = (TupleExpression) createArgumentList(ctx);
        List<Expression> expressions = argumentListExpression.getExpressions();

        if (expressions.size() == 1) {
            return expressions.get(0);
        }

        ListExpression listExpression = new ListExpression(expressions);
        listExpression.setWrapped(true);

        return listExpression;
    }



    public void parseClassBody(ClassNode classNode, GroovyLangParser.ClassBodyContext ctx) {
        for(GroovyLangParser.EnumConstantContext node : ctx.enumConstant()) {

            FieldNode enumConstant = EnumHelper.addEnumConstant(classNode, node.IDENTIFIER().getText(), createEnumConstantInitExpression(node.argumentList()));
            setupNodeLocation(enumConstant, node.IDENTIFIER().getSymbol());

            this.attachDocCommentAsMetaData(enumConstant, node);
        }

        parseMembers(classNode, ctx.classMember());
    }


    public void parseMembers(ClassNode classNode, List<? extends GroovyLangParser.ClassMemberContext> ctx) {
        for (GroovyLangParser.ClassMemberContext member : ctx) {
            ParseTree memberContext = DefaultGroovyMethods.last(member.children);

            ASTNode memberNode = null;
            if (memberContext instanceof GroovyLangParser.ClassDeclarationContext)
                memberNode = parseClassDeclaration((GroovyLangParser.ClassDeclarationContext) memberContext);
            else if (memberContext instanceof GroovyLangParser.MethodDeclarationContext)
                memberNode = parseMember(classNode, (GroovyLangParser.MethodDeclarationContext)memberContext);
            else if (memberContext instanceof GroovyLangParser.FieldDeclarationContext)
                memberNode = parseMember(classNode, (GroovyLangParser.FieldDeclarationContext)memberContext);
            else if (memberContext instanceof GroovyLangParser.ObjectInitializerContext)
                parseMember(classNode, (GroovyLangParser.ObjectInitializerContext)memberContext);
            else if (memberContext instanceof GroovyLangParser.ClassInitializerContext)
                parseMember(classNode, (GroovyLangParser.ClassInitializerContext)memberContext);
            else
                assert false : "Unknown class member type.";


            if (asBoolean(memberNode)) {
                setupNodeLocation(memberNode, member);

                this.attachDocCommentAsMetaData(memberNode, member);
            }

            if (member.getChildCount() > 1) {
                assert memberNode != null;
                for (int i = 0; i < member.children.size() - 2; i++) {
                    ParseTree annotationCtx = member.children.get(i);
                    assert annotationCtx instanceof GroovyLangParser.AnnotationClauseContext;
                    ((AnnotatedNode)memberNode).addAnnotation(parseAnnotation((GroovyLangParser.AnnotationClauseContext)annotationCtx));
                }

            }

        }

    }


    public AnnotatedNode parseMember(ClassNode classNode, GroovyLangParser.MethodDeclarationContext ctx) {
        if (isTrait(classNode)) {
            if (null == ctx.blockStatementWithCurve() && !ctx.modifierAndDefSet.contains(ABSTRACT)) {
                throw createParsingFailedException(new InvalidSyntaxException("You defined a method without body. Try adding a body, or declare it abstract.", ctx));
            }
        }

        return parseMethodDeclaration(classNode, ctx, new Closure<MethodNode>(this, this) {
                    public MethodNode doCall(ClassNode classNode, GroovyLangParser.MethodDeclarationContext ctx, String methodName, int modifiers, ClassNode returnType, Parameter[] params, ClassNode[] exceptions, Statement statement) {
                        modifiers |= classNode.isInterface() ? Opcodes.ACC_ABSTRACT : 0;

                        if (ctx.KW_DEFAULT() != null) {
                            statement = new ExpressionStatement(parseExpression(ctx.annotationParameter()));
                        }

                        MethodNode methodNode;
                        if (asBoolean(ctx.IDENTIFIER()) // constructor's name should only be defined by IDENTIFIER
                                && !(asBoolean(ctx.typeDeclaration()) || asBoolean(ctx.genericClassNameExpression())) // constructor should not has return type
                                && asBoolean(ctx.blockStatementWithCurve()) // constructor should have block statement
                                && methodName.equals(ctx.className) // constructor should has same name with class's
                                && (ctx.modifierAndDefSet.isEmpty() || (1 == ctx.modifierAndDefSet.size() && CONSTRUCTOR_VISIBILITY_MODIFIER_SET.contains(ctx.modifierAndDefSet.toArray()[0]))) // constructor's modifier should has only public, protected, private and default(nothing)
                            ) { // constructor

                            methodNode = classNode.addConstructor(       modifiers,             params, exceptions, statement);
                        } else { // method
                            methodNode = classNode.addMethod(methodName, modifiers, returnType, params, exceptions, statement);
                        }


                        methodNode.setGenericsTypes(parseGenericDeclaration(ctx.genericDeclarationList()));


                        if (ctx.KW_DEFAULT() != null) {
                            methodNode.setAnnotationDefault(true);
                        }


                        return methodNode;
                    }
                }
        );
    }

    public AnnotatedNode parseMember(ClassNode classNode, GroovyLangParser.FieldDeclarationContext ctx) {
        //noinspection GroovyAssignabilityCheck
        final Iterator<Object> iterator = parseModifiers(ctx.memberModifier()).iterator();
        int modifiers = ((Integer)(iterator.hasNext() ? iterator.next() : null));
        boolean hasVisibilityModifier = ((Boolean)(iterator.hasNext() ? iterator.next() : null));

        modifiers |= classNode.isInterface() ? Opcodes.ACC_STATIC | Opcodes.ACC_FINAL : 0;


        AnnotatedNode node = null;
        List<? extends GroovyLangParser.SingleDeclarationContext> variables = ctx.singleDeclaration();
        for (GroovyLangParser.SingleDeclarationContext variableCtx : variables) {
            GroovyLangParser.ExpressionContext initExprContext = variableCtx.expression();
            Expression initialierExpression = asBoolean(initExprContext)
                    ? parseExpression(initExprContext)
                    : null;
            ClassNode typeDeclaration = asBoolean(ctx.genericClassNameExpression())
                    ? parseExpression(ctx.genericClassNameExpression())
                    : ClassHelper.OBJECT_TYPE;

            Object defaultValue = findDefaultValueByType(typeDeclaration);
            Expression initialValue = classNode.isInterface() && (null == initialierExpression)
                    ? (null == defaultValue ? null : new ConstantExpression(defaultValue))
                    : initialierExpression;


            org.codehaus.groovy.syntax.Token token;
            if (asBoolean(variableCtx.ASSIGN())) {
                token = createGroovyToken(variableCtx.ASSIGN().getSymbol(), Types.ASSIGN);
            } else {
                int line = variableCtx.start.getLine();
                int col = -1; //ASSIGN TOKEN DOES NOT APPEAR, SO COL IS -1. IF NO ERROR OCCURS, THE ORIGINAL CODE CAN BE REMOVED IN THE FURTURE: variableCtx.getStart().getCharPositionInLine() + 1; // FIXME Why assignment token location is it's first occurrence.

                token = new org.codehaus.groovy.syntax.Token(Types.ASSIGN, "=", line, col);
            }

            if (classNode.isInterface() || hasVisibilityModifier) {
                modifiers |= classNode.isInterface() ? Opcodes.ACC_PUBLIC : 0;

                FieldNode field = classNode.addField(variableCtx.IDENTIFIER().getText(), modifiers, typeDeclaration, initialValue, token);
                attachAnnotations(field, ctx.annotationClause());
                node = setupNodeLocation(field, variables.size() == 1 ? ctx : variableCtx);
            } else {// no visibility specified. Generate property node.
                Integer propertyModifier = modifiers | Opcodes.ACC_PUBLIC;
                PropertyNode propertyNode = classNode.addProperty(variableCtx.IDENTIFIER().getText(), propertyModifier, typeDeclaration, initialValue, null, null, token);
                propertyNode.getField().setModifiers(modifiers | Opcodes.ACC_PRIVATE);
                propertyNode.getField().setSynthetic(!classNode.isInterface());
                node = setupNodeLocation(propertyNode.getField(), variables.size() == 1 ? ctx : variableCtx);
                attachAnnotations(propertyNode.getField(), ctx.annotationClause());
                setupNodeLocation(propertyNode, variables.size() == 1 ? ctx : variableCtx);
            }
        }
        return node;
    }

    public void parseMember(ClassNode classNode, GroovyLangParser.ClassInitializerContext ctx) {
        unpackStatement((BlockStatement)getOrCreateClinitMethod(classNode).getCode(),  parseBlockStatementWithCurve(ctx.blockStatementWithCurve()));
    }

    public void parseMember(ClassNode classNode, GroovyLangParser.ObjectInitializerContext ctx) {
        BlockStatement statement = new BlockStatement();
        unpackStatement(statement, parseBlockStatementWithCurve(ctx.blockStatementWithCurve()));
        classNode.addObjectInitializerStatements(statement);
    }

    private static class DeclarationList extends Statement{
        List<DeclarationExpression> declarations;

        DeclarationList(List<DeclarationExpression> declarations) {
            this.declarations = declarations;
        }
    }

    public Statement parseStatement(GroovyLangParser.StatementContext ctx) {
        if (ctx instanceof GroovyLangParser.IfStatementContext)
            return parseStatement((GroovyLangParser.IfStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.NewArrayStatementContext)
            return parseStatement((GroovyLangParser.NewArrayStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.TryCatchFinallyStatementContext)
            return parseStatement((GroovyLangParser.TryCatchFinallyStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ThrowStatementContext)
            return parseStatement((GroovyLangParser.ThrowStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ClassicForStatementContext)
            return parseStatement((GroovyLangParser.ClassicForStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.DeclarationStatementContext)
            return parseStatement((GroovyLangParser.DeclarationStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ReturnStatementContext)
            return parseStatement((GroovyLangParser.ReturnStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ExpressionStatementContext)
            return parseStatement((GroovyLangParser.ExpressionStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ForInStatementContext)
            return parseStatement((GroovyLangParser.ForInStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ForColonStatementContext)
            return parseStatement((GroovyLangParser.ForColonStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.SwitchStatementContext)
            return parseStatement((GroovyLangParser.SwitchStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.WhileStatementContext)
            return parseStatement((GroovyLangParser.WhileStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.ControlStatementContext)
            return parseStatement((GroovyLangParser.ControlStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.NewInstanceStatementContext)
            return parseStatement((GroovyLangParser.NewInstanceStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.AssertStatementContext)
            return parseStatement((GroovyLangParser.AssertStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.LabeledStatementContext)
            return parseStatement((GroovyLangParser.LabeledStatementContext)ctx);
        if (ctx instanceof GroovyLangParser.SynchronizedStatementContext)
            return parseStatement((GroovyLangParser.SynchronizedStatementContext)ctx);

        throw createParsingFailedException(new InvalidSyntaxException("Unsupported statement type! " + ctx.getText(), ctx));
    }

    public Statement parseStatement(GroovyLangParser.BlockStatementContext ctx) {
        final BlockStatement statement = new BlockStatement();
        if (!asBoolean(ctx)) return statement;

        for (GroovyLangParser.StatementContext it : ctx.statement()) {
            unpackStatement(statement, parseStatement(it));
        }

        return setupNodeLocation(statement, ctx);
    }

    public Statement parseStatement(GroovyLangParser.ExpressionStatementContext ctx) {
        return setupNodeLocation(new ExpressionStatement(parseExpression(ctx.expression())), ctx);
    }

    public Statement parseStatement(GroovyLangParser.IfStatementContext ctx) {
        Statement trueBranch = parse(ctx.statementBlock(0));
        Statement falseBranch = asBoolean(ctx.KW_ELSE())
                ? parse(ctx.statementBlock(1))
                : EmptyStatement.INSTANCE;
        BooleanExpression expression = new BooleanExpression(parseExpression(ctx.expression()));
        return setupNodeLocation(new IfStatement(expression, trueBranch, falseBranch), ctx);
    }

    public Statement parseStatement(GroovyLangParser.WhileStatementContext ctx) {
        return setupNodeLocation(new WhileStatement(new BooleanExpression(parseExpression(ctx.expression())), parse(ctx.statementBlock())), ctx);
    }

    public Expression parseExpression(GroovyLangParser.DeclarationRuleContext ctx) {
        List<?> declarations = parseDeclaration(ctx);

        if (declarations.size() == 1) {
            return setupNodeLocation((Expression) declarations.get(0), ctx);
        } else {
            return new ClosureListExpression((List<Expression>)declarations);
        }
    }

    public Statement parseStatement(GroovyLangParser.ClassicForStatementContext ctx) {
        ClosureListExpression expression = new ClosureListExpression();

        Boolean captureNext = false;
        for (ParseTree c : ctx.children) {
            // FIXME terrible logic.
            Boolean isSemicolon = c instanceof TerminalNode && (((TerminalNode)c).getSymbol().getText().equals(";") || ((TerminalNode)c).getSymbol().getText().equals("(") || ((TerminalNode)c).getSymbol().getText().equals(")"));

            if (captureNext) {
                if (isSemicolon) {
                    expression.addExpression(EmptyExpression.INSTANCE);
                } else if (c instanceof GroovyLangParser.ExpressionContext) {
                    expression.addExpression(parseExpression((GroovyLangParser.ExpressionContext)c));
                } else if (c instanceof GroovyLangParser.DeclarationRuleContext) {
                    expression.addExpression(parseExpression((GroovyLangParser.DeclarationRuleContext) c));
                }
            }

            captureNext = isSemicolon;
        }

        Parameter parameter = ForStatement.FOR_LOOP_DUMMY;
        return setupNodeLocation(new ForStatement(parameter, expression, parse(ctx.statementBlock())), ctx);
    }

    public Statement parseStatement(GroovyLangParser.ForInStatementContext ctx) {
        Parameter parameter = new Parameter(parseTypeDeclaration(ctx.typeDeclaration()), ctx.IDENTIFIER().getText());
        parameter = setupNodeLocation(parameter, ctx.IDENTIFIER().getSymbol());

        return setupNodeLocation(new ForStatement(parameter, parseExpression(ctx.expression()), parse(ctx.statementBlock())), ctx);
    }

    public Statement parseStatement(GroovyLangParser.ForColonStatementContext ctx) {
        if (!asBoolean(ctx.typeDeclaration()))
            throw createParsingFailedException(new InvalidSyntaxException("Classic for statement require type to be declared.", ctx));

        Parameter parameter = new Parameter(parseTypeDeclaration(ctx.typeDeclaration()), ctx.IDENTIFIER().getText());
        parameter = setupNodeLocation(parameter, ctx.IDENTIFIER().getSymbol());

        return setupNodeLocation(new ForStatement(parameter, parseExpression(ctx.expression()), parse(ctx.statementBlock())), ctx);
    }

    public Statement parseBlockStatementWithCurve(GroovyLangParser.BlockStatementWithCurveContext ctx) {
        return parseStatement(ctx.blockStatement());
    }

    public Statement parse(GroovyLangParser.StatementBlockContext ctx) {
        if (asBoolean(ctx.statement()))
            return setupNodeLocation(parseStatement(ctx.statement()), ctx.statement());
        else
            return parseBlockStatementWithCurve(ctx.blockStatementWithCurve());
    }

    public Statement parseStatement(GroovyLangParser.SwitchStatementContext ctx) {
        List<CaseStatement> caseStatements = new LinkedList<CaseStatement>();
        for (GroovyLangParser.CaseStatementContext caseStmt : ctx.caseStatement()) {

            BlockStatement stmt =  new BlockStatement();// #BSC
            for (GroovyLangParser.StatementContext st : caseStmt.statement()) {
                unpackStatement (stmt, parseStatement(st));
            }

            caseStatements.add(setupNodeLocation(new CaseStatement(parseExpression(caseStmt.expression()), asBoolean(stmt.getStatements()) ? stmt : EmptyStatement.INSTANCE), caseStmt.KW_CASE().getSymbol()));// There only 'case' kw was highlighted in parser old version.
        }

        Statement defaultStatement;
        if (asBoolean(ctx.KW_DEFAULT())) {

            defaultStatement = new BlockStatement();// #BSC
            for (GroovyLangParser.StatementContext stmt : ctx.statement())
                unpackStatement((BlockStatement)defaultStatement,parseStatement(stmt));
        } else
            defaultStatement = EmptyStatement.INSTANCE;// TODO Refactor empty stataements and expressions.

        return new SwitchStatement(parseExpression(ctx.expression()), caseStatements, defaultStatement);
    }

    public Statement parseStatement(GroovyLangParser.DeclarationStatementContext ctx) {
        List<DeclarationExpression> declarations = parseDeclaration(ctx.declarationRule());
        return setupNodeLocation(new DeclarationList(declarations), ctx);
    }

    public Statement parseStatement(GroovyLangParser.NewArrayStatementContext ctx) {
        return setupNodeLocation(new ExpressionStatement(parse(ctx.newArrayRule())), ctx);
    }

    public Statement parseStatement(GroovyLangParser.NewInstanceStatementContext ctx) {
        return setupNodeLocation(new ExpressionStatement(parse(ctx.newInstanceRule())), ctx);
    }

    public Statement parseStatement(GroovyLangParser.ControlStatementContext ctx) {
        // TODO check validity.
        // Fake inspection result should be suppressed.
        //noinspection GroovyConditionalWithIdenticalBranches
        String label = asBoolean(ctx.IDENTIFIER()) ? ctx.IDENTIFIER().getText() : null;

        return setupNodeLocation(asBoolean(ctx.KW_BREAK())
                ? new BreakStatement(label)
                : new ContinueStatement(label), ctx);
    }

    public Statement parseStatement(GroovyLangParser.ReturnStatementContext ctx) {
        GroovyLangParser.ExpressionContext expression = ctx.expression();

        return setupNodeLocation(new ReturnStatement(asBoolean(expression)
                ? parseExpression(expression)
                : new ConstantExpression(null)), ctx);
    }


    public Statement parseStatement(GroovyLangParser.AssertStatementContext ctx) {
        Expression conditionExpression = parseExpression(ctx.expression(0));
        BooleanExpression booleanConditionExpression = new BooleanExpression(conditionExpression);

        if (ctx.expression().size() == 1) {
            return setupNodeLocation(new AssertStatement(booleanConditionExpression), ctx);
        } else {
            Expression errorMessage = parseExpression(ctx.expression(1));
            return setupNodeLocation(new AssertStatement(booleanConditionExpression, errorMessage), ctx);
        }
    }

    public Statement parseStatement(GroovyLangParser.LabeledStatementContext ctx) {
        Statement statement = parse(ctx.statementBlock());

        statement.setStatementLabel(ctx.IDENTIFIER().getText());

        return setupNodeLocation(statement, ctx);
    }

    public Statement parseStatement(GroovyLangParser.SynchronizedStatementContext ctx) {
        Expression expression = parseExpression(ctx.expression());
        Statement statementBlock = parse(ctx.statementBlock());

        return setupNodeLocation(new SynchronizedStatement(expression, statementBlock), ctx);
    }

    public Statement parseStatement(GroovyLangParser.ThrowStatementContext ctx) {
        return setupNodeLocation(new ThrowStatement(parseExpression(ctx.expression())), ctx);
    }

    public Statement parseStatement(GroovyLangParser.TryCatchFinallyStatementContext ctx) {
        Object finallyStatement;

        GroovyLangParser.BlockStatementWithCurveContext finallyBlockStatement = ctx.finallyBlock() != null ? ctx.finallyBlock().blockStatementWithCurve() : null;
        if (finallyBlockStatement != null) {
            BlockStatement fbs = new BlockStatement();
            unpackStatement(fbs, parseBlockStatementWithCurve(finallyBlockStatement));
            finallyStatement = setupNodeLocation(fbs, finallyBlockStatement);

        } else finallyStatement = EmptyStatement.INSTANCE;

        final TryCatchStatement statement = new TryCatchStatement(parseBlockStatementWithCurve(ctx.tryBlock().blockStatementWithCurve()), (Statement)finallyStatement);

        for (GroovyLangParser.CatchBlockContext it : ctx.catchBlock()) {
            final Statement catchBlock = parseBlockStatementWithCurve(it.blockStatementWithCurve());
            final String var = it.IDENTIFIER().getText();

            List<? extends GroovyLangParser.ClassNameExpressionContext> classNameExpression = it.classNameExpression();
            if (!asBoolean(classNameExpression)) {
                statement.addCatch(setupNodeLocation(new CatchStatement(new Parameter(ClassHelper.OBJECT_TYPE, var), catchBlock), it));
            } else {
                for (GroovyLangParser.ClassNameExpressionContext classNameExpressionContext : classNameExpression) {
                    statement.addCatch(setupNodeLocation(new CatchStatement(new Parameter(parseClassNameExpression((GroovyLangParser.ClassNameExpressionContext) classNameExpressionContext), var), catchBlock), classNameExpressionContext));
                }
            }
        }

        return statement;
    }

    public Expression parseExpression(GroovyLangParser.AtomExpressionContext ctx) {
        GroovyLangParser.AtomExpressionRuleContext atomExpressionRuleContext = ctx.atomExpressionRule();

        return parseExpression(atomExpressionRuleContext);
    }

    public Expression parseExpression(GroovyLangParser.AtomExpressionRuleContext ctx) {
        if (ctx instanceof GroovyLangParser.ConstantIntegerExpressionContext)
            return parseExpression((GroovyLangParser.ConstantIntegerExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.ClosureExpressionContext)
            return parseExpression((GroovyLangParser.ClosureExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.ConstantDecimalExpressionContext)
            return parseExpression((GroovyLangParser.ConstantDecimalExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.NullExpressionContext)
            return parseExpression((GroovyLangParser.NullExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.ListConstructorContext)
            return parseExpression((GroovyLangParser.ListConstructorContext)ctx);
        else if (ctx instanceof GroovyLangParser.ConstantExpressionContext)
            return parseExpression((GroovyLangParser.ConstantExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.NewArrayExpressionContext)
            return parseExpression((GroovyLangParser.NewArrayExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.VariableExpressionContext)
            return parseExpression((GroovyLangParser.VariableExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.ClassConstantExpressionContext)
            return parseExpression((GroovyLangParser.ClassConstantExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.NewInstanceExpressionContext)
            return parseExpression((GroovyLangParser.NewInstanceExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.BoolExpressionContext)
            return parseExpression((GroovyLangParser.BoolExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.MapConstructorContext)
            return parseExpression((GroovyLangParser.MapConstructorContext)ctx);
        else if (ctx instanceof GroovyLangParser.GstringExpressionContext)
            return parseExpression((GroovyLangParser.GstringExpressionContext)ctx);

        throw createParsingFailedException(new InvalidSyntaxException("Unsupported atom expression type! " + String.valueOf(ctx), ctx));
    }

    public Expression parseExpression(GroovyLangParser.ExpressionContext ctx) {
        if (ctx instanceof GroovyLangParser.AtomExpressionContext)
            return parseExpression((GroovyLangParser.AtomExpressionContext)ctx);
        if (ctx instanceof GroovyLangParser.ParenthesisExpressionContext)
            return parseExpression((GroovyLangParser.ParenthesisExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.PostfixExpressionContext)
            return parseExpression((GroovyLangParser.PostfixExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.AssignmentExpressionContext)
            return parseExpression((GroovyLangParser.AssignmentExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.TernaryExpressionContext)
            return parseExpression((GroovyLangParser.TernaryExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.CmdExpressionContext)
            return parseExpression((GroovyLangParser.CmdExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.CallExpressionContext)
            return parseExpression((GroovyLangParser.CallExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.CastExpressionContext)
            return parseExpression((GroovyLangParser.CastExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.BinaryExpressionContext)
            return parseExpression((GroovyLangParser.BinaryExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.PrefixExpressionContext)
            return parseExpression((GroovyLangParser.PrefixExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.FieldAccessExpressionContext)
            return parseExpression((GroovyLangParser.FieldAccessExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.ConstructorCallExpressionContext)
            return parseExpression((GroovyLangParser.ConstructorCallExpressionContext)ctx);
        else if (ctx instanceof GroovyLangParser.UnaryExpressionContext)
            return parseExpression((GroovyLangParser.UnaryExpressionContext)ctx);
        if (ctx instanceof GroovyLangParser.IndexExpressionContext)
            return parseExpression((GroovyLangParser.IndexExpressionContext)ctx);
        if (ctx instanceof GroovyLangParser.SpreadExpressionContext)
            return parseExpression((GroovyLangParser.SpreadExpressionContext)ctx);
        if (ctx instanceof GroovyLangParser.ThisExpressionContext)
            return parseExpression((GroovyLangParser.ThisExpressionContext)ctx);
        if (ctx instanceof GroovyLangParser.SuperExpressionContext)
            return parseExpression((GroovyLangParser.SuperExpressionContext)ctx);

        throw createParsingFailedException(new InvalidSyntaxException("Unsupported expression type! " + String.valueOf(ctx), ctx));
    }

    public Expression parseExpression(GroovyLangParser.NewArrayExpressionContext ctx) {
        return parse(ctx.newArrayRule());
    }

    public Expression parseExpression(GroovyLangParser.NewInstanceExpressionContext ctx) {
        return parse(ctx.newInstanceRule());
    }

    public Expression parseExpression(GroovyLangParser.ParenthesisExpressionContext ctx) {
        return parseExpression(ctx.expression());
    }

    public Expression parseExpression(GroovyLangParser.ListConstructorContext ctx) {
        ListExpression expression = new ListExpression(collect(ctx.expression(), new MethodClosure(this, "parseExpression")));
        return setupNodeLocation(expression, ctx);
    }

    public Expression parseExpression(GroovyLangParser.MapConstructorContext ctx) {
        final List collect = collect(ctx.mapEntry(), new MethodClosure(this, "parseExpression"));
        return setupNodeLocation(new MapExpression(asBoolean(collect)
                ? collect
                : new LinkedList()), ctx);
    }

    public MapEntryExpression parseExpression(GroovyLangParser.MapEntryContext ctx) {
        Expression keyExpr;
        Expression valueExpr;
        List<? extends GroovyLangParser.ExpressionContext> expressions = ctx.expression();
        if (expressions.size() == 1) {
            valueExpr = parseExpression(expressions.get(0));
            if (asBoolean(ctx.MULT())) {
                // This is really a spread map entry.
                // This is an odd construct, SpreadMapExpression does not extend MapExpression, so we workaround
                keyExpr = setupNodeLocation(new SpreadMapExpression(valueExpr), ctx);
            } else {
                if (asBoolean(ctx.STRING())) {
                    keyExpr = new ConstantExpression(parseString(ctx.STRING()));
                } else if (asBoolean(ctx.selectorName())) {
                    keyExpr = new ConstantExpression(ctx.selectorName().getText());
                } else if (asBoolean(ctx.gstring())) {
                    keyExpr = parseExpression(ctx.gstring());
                } else if (asBoolean(ctx.INTEGER())) {
                    keyExpr = parseInteger(ctx.INTEGER().getText(), ctx);
                } else if (asBoolean(ctx.DECIMAL())) {
                    keyExpr = parseDecimal(ctx.DECIMAL().getText(), ctx);
                } else {
                    throw createParsingFailedException(new InvalidSyntaxException("Unsupported map key type! " + String.valueOf(ctx), ctx));
                }
            }
        } else {
            keyExpr = parseExpression(expressions.get(0));
            valueExpr = parseExpression(expressions.get(1));
        }

        return setupNodeLocation(new MapEntryExpression(keyExpr, valueExpr), ctx);
    }

    public Expression parseExpression(GroovyLangParser.ClosureExpressionContext ctx) {
        return parseExpression(ctx.closureExpressionRule());
    }

    public Expression parseExpression(GroovyLangParser.ClosureExpressionRuleContext ctx) {
        final Parameter[] parameters1 = parseParameters(ctx.argumentDeclarationList());
        Parameter[] parameters = asBoolean(ctx.argumentDeclarationList()) ? (
                asBoolean(parameters1)
                        ? parameters1
                        : null) : (new Parameter[0]);

        Statement statement = parseStatement((GroovyLangParser.BlockStatementContext) ctx.blockStatement());
        return setupNodeLocation(new ClosureExpression(parameters, statement), ctx);
    }

    public Expression parseExpression(GroovyLangParser.BinaryExpressionContext ctx) {
        int i = 1;

        // ignore newlines
        for (ParseTree t = ctx.getChild(i); t instanceof TerminalNode && ((TerminalNode) t).getSymbol().getType() == GroovyLangParser.NL; t = ctx.getChild(i)) {
            i++;
        }

        TerminalNode c = (TerminalNode) ctx.getChild(i);

        for (ParseTree next = ctx.getChild(i + 1); next instanceof TerminalNode && ((TerminalNode)next).getSymbol().getType() == GroovyLangParser.GT; next = ctx.getChild(i + 1)) {
            i++;
        }

        org.codehaus.groovy.syntax.Token op = createToken(c, c.getSymbol().getType() == GroovyLangParser.GT ? i : 1);

        Object expression;
        Expression left = parseExpression(ctx.expression(0));
        Expression right = null;// Will be initialized later, in switch. We should handle as and instanceof creating
        // ClassExpression for given IDENTIFIERS. So, switch should fall through.
        //noinspection GroovyFallthrough
        switch (op.getType()) {
            case Types.RANGE_OPERATOR:
                right = parseExpression(ctx.expression(1));
                expression = new RangeExpression(left, right, !op.getText().endsWith("<"));
                break;
            case Types.KEYWORD_AS:
                ClassNode classNode = setupNodeLocation(parseExpression(ctx.genericClassNameExpression()), ctx.genericClassNameExpression());
                expression = CastExpression.asExpression(classNode, left);
                break;
            case Types.KEYWORD_INSTANCEOF:
                ClassNode rhClass = setupNodeLocation(parseExpression(ctx.genericClassNameExpression()), ctx.genericClassNameExpression());
                right = new ClassExpression(rhClass);
            default:
                if (!asBoolean(right)) right = parseExpression(ctx.expression(1));
                expression = new BinaryExpression(left, op, right);
                break;
        }

        ((Expression)expression).setColumnNumber(op.getStartColumn());
        ((Expression)expression).setLastColumnNumber(op.getStartColumn() + op.getText().length());
        ((Expression)expression).setLineNumber(op.getStartLine());
        ((Expression)expression).setLastLineNumber(op.getStartLine());
        return ((Expression)(expression));
    }

    public Expression parseExpression(GroovyLangParser.CastExpressionContext ctx) {
        Expression left = parseExpression(ctx.expression());
        ClassNode classNode = setupNodeLocation(parseExpression(ctx.genericClassNameExpression()), ctx.genericClassNameExpression());
        CastExpression expression = new CastExpression(classNode, left);
        return setupNodeLocation(expression, ctx);
    }

    public Expression parseExpression(GroovyLangParser.TernaryExpressionContext ctx) {
        if (asBoolean(ctx.ELVIS())) { // elvisExpression
            Expression baseExpr = parseExpression(ctx.expression(0));
            Expression falseExpr = parseExpression(ctx.expression(1));
            return setupNodeLocation(new ElvisOperatorExpression(baseExpr, falseExpr), ctx);
        } else { // ternaryExpression
            BooleanExpression boolExpr = new BooleanExpression(parseExpression(ctx.expression(0)));
            Expression trueExpr = parseExpression(ctx.expression(1));
            Expression falseExpr = parseExpression(ctx.expression(2));
            return setupNodeLocation(new TernaryExpression(boolExpr, trueExpr, falseExpr), ctx);
        }
    }


    protected Expression unaryMinusExpression(GroovyLangParser.ExpressionContext ctx) {
        // if we are a number literal then let's just parse it
        // as the negation operator on MIN_INT causes rounding to a long
        if (ctx instanceof GroovyLangParser.AtomExpressionContext) {
            GroovyLangParser.AtomExpressionRuleContext atomExpressionRuleContext = ((GroovyLangParser.AtomExpressionContext) ctx).atomExpressionRule();
            if (atomExpressionRuleContext instanceof GroovyLangParser.ConstantDecimalExpressionContext) {
                return parseDecimal('-' + ((GroovyLangParser.ConstantDecimalExpressionContext) atomExpressionRuleContext).DECIMAL().getText(), ctx);
            } else if (atomExpressionRuleContext instanceof GroovyLangParser.ConstantIntegerExpressionContext) {
                return parseInteger('-' + ((GroovyLangParser.ConstantIntegerExpressionContext) atomExpressionRuleContext).INTEGER().getText(), ctx);
            }
        }

        return new UnaryMinusExpression(parseExpression(ctx));
    }

    protected Expression unaryPlusExpression(GroovyLangParser.ExpressionContext ctx) {
        if (ctx instanceof GroovyLangParser.AtomExpressionContext) {
            GroovyLangParser.AtomExpressionRuleContext atomExpressionRuleContext = ((GroovyLangParser.AtomExpressionContext) ctx).atomExpressionRule();

            if (atomExpressionRuleContext instanceof GroovyLangParser.ConstantDecimalExpressionContext || atomExpressionRuleContext instanceof GroovyLangParser.ConstantIntegerExpressionContext) {
                return parseExpression(atomExpressionRuleContext);
            }
        }

        return new UnaryPlusExpression(parseExpression(ctx));
    }

    public Expression parseExpression(GroovyLangParser.UnaryExpressionContext ctx) {
        Object node = null;
        TerminalNode op = (TerminalNode) ctx.getChild(0);
        if (DefaultGroovyMethods.isCase("-", op.getText())) {
            node = unaryMinusExpression(ctx.expression());
        } else if (DefaultGroovyMethods.isCase("+", op.getText())) {
            node = unaryPlusExpression(ctx.expression());
        } else if (DefaultGroovyMethods.isCase("!", op.getText())) {
            node = new NotExpression(parseExpression(ctx.expression()));
        } else if (DefaultGroovyMethods.isCase("~", op.getText())) {
            node = new BitwiseNegationExpression(parseExpression(ctx.expression()));
        } else {
            assert false : "There is no " + op.getText() + " handler.";
        }

        ((Expression)node).setColumnNumber(op.getSymbol().getCharPositionInLine() + 1);
        ((Expression)node).setLineNumber(op.getSymbol().getLine());
        ((Expression)node).setLastLineNumber(op.getSymbol().getLine());
        ((Expression)node).setLastColumnNumber(op.getSymbol().getCharPositionInLine() + 1 + op.getText().length());
        return ((Expression)(node));
    }

    public SpreadExpression parseExpression(GroovyLangParser.SpreadExpressionContext ctx) {
        SpreadExpression expression = new SpreadExpression(parseExpression(ctx.expression()));
        return setupNodeLocation(expression, ctx);
    }


    public Expression parseExpression(GroovyLangParser.AnnotationParameterContext ctx) {
        if (ctx instanceof GroovyLangParser.AnnotationParamArrayExpressionContext) {
            GroovyLangParser.AnnotationParamArrayExpressionContext c = (GroovyLangParser.AnnotationParamArrayExpressionContext) ctx;
            return setupNodeLocation(new ListExpression(collect(c.annotationParameter(), new Closure<Expression>(null, null) {
                public Expression doCall(GroovyLangParser.AnnotationParameterContext it) {return parseExpression(it);}
            })), c);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamBoolExpressionContext) {
            return parseExpression((GroovyLangParser.AnnotationParamBoolExpressionContext)ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamClassExpressionContext) {
            return setupNodeLocation(new ClassExpression(parseExpression(((GroovyLangParser.AnnotationParamClassExpressionContext) ctx).genericClassNameExpression())), ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamDecimalExpressionContext) {
            return parseExpression((GroovyLangParser.AnnotationParamDecimalExpressionContext)ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamIntegerExpressionContext) {
            return parseExpression((GroovyLangParser.AnnotationParamIntegerExpressionContext)ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamNullExpressionContext) {
            return parseExpression((GroovyLangParser.AnnotationParamNullExpressionContext)ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamClassConstantExpressionContext) {
            GroovyLangParser.AnnotationParamClassConstantExpressionContext c = (GroovyLangParser.AnnotationParamClassConstantExpressionContext) ctx;
            return setupNodeLocation(parseExpression(c.classConstantRule()), ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamPathExpressionContext) {
            GroovyLangParser.AnnotationParamPathExpressionContext c = (GroovyLangParser.AnnotationParamPathExpressionContext) ctx;
            return parseExpression(c.pathExpression());
        } else if (ctx instanceof GroovyLangParser.AnnotationParamStringExpressionContext) {
            return parseExpression((GroovyLangParser.AnnotationParamStringExpressionContext)ctx);
        } else if (ctx instanceof GroovyLangParser.AnnotationParamClosureExpressionContext) {
            return parseExpression((GroovyLangParser.AnnotationParamClosureExpressionContext)ctx);
        }


        throw createParsingFailedException(new IllegalStateException(String.valueOf(ctx) + " is prohibited inside annotations."));
    }

    public Expression parseExpression(GroovyLangParser.ClassNameExpressionContext ctx) {
        GroovyLangParser.PathExpressionContext pec = ctx.pathExpression();

        Expression expr;
        if (asBoolean(pec)) {
            expr = parseExpression(pec);
        } else {
            expr = new VariableExpression(ctx.BUILT_IN_TYPE().getText());
        }

        return setupNodeLocation(expr, ctx);
    }

    public Expression parseExpression(GroovyLangParser.ClassConstantRuleContext ctx) {
        Expression expr = parseExpression(ctx.classNameExpression());

        if (asBoolean(ctx.KW_CLASS())) {
            expr = new PropertyExpression(expr, ctx.KW_CLASS().getText());
        }

        return setupNodeLocation(expr, ctx);
    }

    public Expression parseExpression(GroovyLangParser.ClassConstantExpressionContext ctx) {
        GroovyParser.ClassConstantRuleContext ccrc = ctx.classConstantRule();

        return parseExpression(ccrc);
    }

    public Expression parseExpression(GroovyLangParser.VariableExpressionContext ctx) {
        return setupNodeLocation(new VariableExpression(ctx.IDENTIFIER().getText()), ctx);
    }

    public Expression parseExpression(GroovyLangParser.FieldAccessExpressionContext ctx) {
        Token op = ctx.op;
        Expression left = parseExpression(ctx.e);
        Expression right = this.parseName(ctx.selectorName(), ctx.STRING(), ctx.gstring(), ctx.mne);

        Expression node = null;
        switch (op.getType()) {
            case GroovyLangParser.ATTR_DOT:
                node = new AttributeExpression(left, right);
                break;
            case GroovyLangParser.MEMBER_POINTER:
                node = new MethodPointerExpression(left, right);
                break;
            case GroovyLangParser.SAFE_DOT:
                node = new PropertyExpression(left, right, true);
                break;
            case GroovyLangParser.STAR_DOT:
                node = new PropertyExpression(left, right, true /* For backwards compatibility! */);
                ((PropertyExpression)node).setSpreadSafe(true);
                break;
            default:
                // Normal dot
                node = new PropertyExpression(left, right, false);
                break;
        }
        return setupNodeLocation(node, ctx);
    }

    public PrefixExpression parseExpression(GroovyLangParser.PrefixExpressionContext ctx) {
        return setupNodeLocation(new PrefixExpression(createToken((TerminalNode) ctx.getChild(0)), parseExpression(ctx.expression())), ctx);
    }

    public PostfixExpression parseExpression(GroovyLangParser.PostfixExpressionContext ctx) {
        return setupNodeLocation(new PostfixExpression(parseExpression(ctx.expression()), createToken((TerminalNode) ctx.getChild(1))), ctx);
    }

    public ConstantExpression parseDecimal(String text, ParserRuleContext ctx) {
        return setupNodeLocation(new ConstantExpression(Numbers.parseDecimal(text), !text.startsWith("-")), ctx);// Why 10 is int but -10 is Integer?
    }

    public ConstantExpression parseExpression(GroovyLangParser.AnnotationParamDecimalExpressionContext ctx) {
        return parseDecimal(ctx.DECIMAL().getText(), ctx);
    }

    public ConstantExpression parseExpression(GroovyLangParser.ConstantDecimalExpressionContext ctx) {
        return parseDecimal(ctx.DECIMAL().getText(), ctx);
    }

    public ConstantExpression parseInteger(String text, ParserRuleContext ctx) {
        return setupNodeLocation(new ConstantExpression(Numbers.parseInteger(text), !text.startsWith("-")), ctx);//Why 10 is int but -10 is Integer?
    }

    public ConstantExpression parseInteger(String text, Token ctx) {
        return setupNodeLocation(new ConstantExpression(Numbers.parseInteger(text), !text.startsWith("-")), ctx);//Why 10 is int but -10 is Integer?
    }

    public ConstantExpression parseExpression(GroovyLangParser.ConstantIntegerExpressionContext ctx) {
        return parseInteger(ctx.INTEGER().getText(), ctx);
    }

    public ConstantExpression parseExpression(GroovyLangParser.AnnotationParamIntegerExpressionContext ctx) {
        return parseInteger(ctx.INTEGER().getText(), ctx);
    }

    public ConstantExpression parseExpression(GroovyLangParser.BoolExpressionContext ctx) {
        return setupNodeLocation(new ConstantExpression(!asBoolean(ctx.KW_FALSE()), true), ctx);
    }

    public ConstantExpression parseExpression(GroovyLangParser.AnnotationParamBoolExpressionContext ctx) {
        return setupNodeLocation(new ConstantExpression(!asBoolean(ctx.KW_FALSE()), true), ctx);
    }

    public ConstantExpression cleanConstantStringLiteral(String text) {
        int slashyType = text.startsWith("/") ? StringUtil.SLASHY :
                text.startsWith("$/") ? StringUtil.DOLLAR_SLASHY : StringUtil.NONE_SLASHY;

        if (text.startsWith("'''") || text.startsWith("\"\"\"")) {
            text = StringUtil.removeCR(text); // remove CR in the multiline string

            text = text.length() == 6 ? "" : text.substring(3, text.length() - 3);
        } else if (text.startsWith("'") || text.startsWith("/") || text.startsWith("\"")) {
            text = text.length() == 2 ? "" : text.substring(1, text.length() - 1);
        } else if (text.startsWith("$/")) {
            text = StringUtil.removeCR(text);

            text = text.length() == 4 ? "" : text.substring(2, text.length() - 2);
        }

        //handle escapes.
        text = StringUtil.replaceEscapes(text, slashyType);

        return new ConstantExpression(text, true);
    }

    public ConstantExpression parseConstantString(ParserRuleContext ctx) {
        return setupNodeLocation(cleanConstantStringLiteral(ctx.getText()), ctx);
    }

    public ConstantExpression parseConstantStringToken(Token token) {
        return setupNodeLocation(cleanConstantStringLiteral(token.getText()), token);
    }

    public ConstantExpression parseExpression(GroovyLangParser.ConstantExpressionContext ctx) {
        return parseConstantString(ctx);
    }

    public Expression parseExpression(GroovyLangParser.SuperExpressionContext ctx) {
        return setupNodeLocation(new VariableExpression(ctx.KW_SUPER().getText()), ctx);
    }

    public Expression parseExpression(GroovyLangParser.ThisExpressionContext ctx) {
        return setupNodeLocation(new VariableExpression(ctx.KW_THIS().getText()), ctx);
    }

    public ConstantExpression parseExpression(GroovyLangParser.AnnotationParamStringExpressionContext ctx) {
        return parseConstantString(ctx);
    }

    public Expression parseExpression(GroovyLangParser.AnnotationParamClosureExpressionContext ctx) {
        return setupNodeLocation(parseExpression(ctx.closureExpressionRule()), ctx);
    }

    public Expression parseExpression(GroovyLangParser.GstringExpressionContext ctx) {
        return parseExpression(ctx.gstring());
    }

    public Expression parseExpression(GroovyLangParser.GstringContext ctx) {
        String gstringStartText = ctx.GSTRING_START().getText();
        final int slashyType = gstringStartText.startsWith("/") ? StringUtil.SLASHY :
                gstringStartText.startsWith("$/") ? StringUtil.DOLLAR_SLASHY : StringUtil.NONE_SLASHY;

        Closure<String> clearStart = new Closure<String>(null, null) {
            public String doCall(String it) {

                if (it.startsWith("\"\"\"")) {
                    it = StringUtil.removeCR(it);

                    it = it.substring(2); // translate leading """ to "
                } else if (it.startsWith("$/")) {
                    it = StringUtil.removeCR(it);

                    it = "\"" + it.substring(2); // translate leading $/ to "

                }

                it = StringUtil.replaceEscapes(it, slashyType);

                return (it.length() == 2)
                        ? ""
                        : DefaultGroovyMethods.getAt(it, new IntRange(true, 1, -2));
            }

        };
        final Closure<String> clearPart = new Closure<String>(null, null) {
            public String doCall(String it) {

                it = StringUtil.removeCR(it);

                it = StringUtil.replaceEscapes(it, slashyType);

                return it.length() == 1
                        ? ""
                        : DefaultGroovyMethods.getAt(it, new IntRange(true, 0, -2));
            }

        };
        Closure<String> clearEnd = new Closure<String>(null, null) {
            public String doCall(String it) {

                if (it.endsWith("\"\"\"")) {
                    it = StringUtil.removeCR(it);

                    it = DefaultGroovyMethods.getAt(it, new IntRange(true, 0, -3)); // translate tailing """ to "
                } else if (it.endsWith("/$")) {
                    it = StringUtil.removeCR(it);

                    it = DefaultGroovyMethods.getAt(it, new IntRange(true, 0, -3)) + "\""; // translate tailing /$ to "
                }

                it = StringUtil.replaceEscapes(it, slashyType);

                return (it.length() == 1)
                        ? ""
                        : DefaultGroovyMethods.getAt(it, new IntRange(true, 0, -2));
            }

        };
        Collection<String> strings = DefaultGroovyMethods.plus(DefaultGroovyMethods.plus(new LinkedList<String>(Arrays.asList(clearStart.call(ctx.GSTRING_START().getText()))), collect(ctx.GSTRING_PART(), new Closure<String>(null, null) {
            public String doCall(TerminalNode it) {return clearPart.call(it.getText());}
        })), new LinkedList<String>(Arrays.asList(clearEnd.call(ctx.GSTRING_END().getText()))));
        final List<Expression> expressions = new LinkedList<Expression>();
        final List<ParseTree> children = ctx.children;

        for (Object it : children) {
            if (!(it instanceof GroovyLangParser.GstringExpressionBodyContext)) {
                continue;
            }

            GroovyLangParser.GstringExpressionBodyContext gstringExpressionBodyContext = (GroovyLangParser.GstringExpressionBodyContext) it;

            if (asBoolean(gstringExpressionBodyContext.gstringPathExpression())) {
                expressions.add(parseExpression(gstringExpressionBodyContext.gstringPathExpression()));
            } else if (asBoolean(gstringExpressionBodyContext.closureExpressionRule())) {
                GroovyLangParser.ClosureExpressionRuleContext closureExpressionRule = gstringExpressionBodyContext.closureExpressionRule();
                Expression expression = parseExpression(closureExpressionRule);

                if (!asBoolean(closureExpressionRule.CLOSURE_ARG_SEPARATOR())) {

                    MethodCallExpression methodCallExpression = new MethodCallExpression(expression, CALL, new ArgumentListExpression());

                    expressions.add(setupNodeLocation(methodCallExpression, expression));
                } else {
                    expressions.add(expression);
                }
            } else {
                if (asBoolean(gstringExpressionBodyContext.expression())) {
                    // We can guarantee, that it will be at least fallback ExpressionContext multimethod overloading, that can handle such situation.
                    //noinspection GroovyAssignabilityCheck
                    expressions.add(parseExpression(gstringExpressionBodyContext.expression()));
                } else { // handle empty expression e.g. "GString ${}"
                    expressions.add(new ConstantExpression(null));
                }
            }
        }

        GStringExpression gstringNode = new GStringExpression(ctx.getText(), collect(strings, new Closure<ConstantExpression>(null, null) {
            public ConstantExpression doCall(String it) {return new ConstantExpression(it);}
        }), expressions);
        return setupNodeLocation(gstringNode, ctx);
    }

    public Expression parseExpression(GroovyLangParser.NullExpressionContext ctx) {
        return setupNodeLocation(new ConstantExpression(null), ctx);
    }

    public Expression parseExpression(GroovyLangParser.AnnotationParamNullExpressionContext ctx) {
        return setupNodeLocation(new ConstantExpression(null), ctx);
    }

    public Expression parseExpression(GroovyLangParser.AssignmentExpressionContext ctx) {
        Expression left;
        Expression right;
        org.codehaus.groovy.syntax.Token token;

        if (asBoolean(ctx.LPAREN())) { // tuple assignment expression
            List<Expression> expressions = new LinkedList<Expression>();

            for (TerminalNode id : ctx.IDENTIFIER()) {
                expressions.add(new VariableExpression(id.getText(), ClassHelper.OBJECT_TYPE));
            }

            left = new TupleExpression(expressions);
            right = parseExpression(ctx.expression(0));

            token = this.createGroovyToken(ctx.ASSIGN().getSymbol(), Types.ASSIGN);
        } else {
            left = parseExpression(ctx.expression(0));// TODO reference to AntlrParserPlugin line 2304 for error handling.
            right = parseExpression(ctx.expression(1));

            token = createToken((TerminalNode) ctx.getChild(1));
        }

        return setupNodeLocation(new BinaryExpression(left, token, right), ctx);
    }

    public Expression parseExpression(GroovyLangParser.PathExpressionContext ctx) {
        List<? extends TerminalNode> identifiers = ctx.IDENTIFIER();

        Expression result = null;

        switch (identifiers.size()) {
            case 1:
                result = new VariableExpression(identifiers.get(0).getText());
                break;
            default:
                result = DefaultGroovyMethods.inject(identifiers.subList(1, identifiers.size()), new VariableExpression(identifiers.get(0).getText()), new Closure<PropertyExpression>(null, null) {
                    public PropertyExpression doCall(Object val, Object prop) {
                        return setupNodeLocation(new PropertyExpression((Expression) val, new ConstantExpression(((TerminalNode) prop).getText())), ((TerminalNode) prop).getSymbol());
                    }
                });
                break;
        }

        return setupNodeLocation(result, ctx);
    }

    public Expression parseExpression(GroovyLangParser.GstringPathExpressionContext ctx) {
        if (!asBoolean(ctx.GSTRING_PATH_PART()))
            return new VariableExpression(ctx.IDENTIFIER().getText());
        else {
            Expression inj = DefaultGroovyMethods.inject(ctx.GSTRING_PATH_PART(), new VariableExpression(ctx.IDENTIFIER().getText()), new Closure<PropertyExpression>(null, null) {
                public PropertyExpression doCall(Object val, Object prop) {
                    return new PropertyExpression((Expression) val, new ConstantExpression(DefaultGroovyMethods.getAt(((TerminalNode)prop).getText(), new IntRange(true, 1, -1))));
                }

            });
            return inj;
        }

    }

    public BinaryExpression parseExpression(GroovyLangParser.IndexExpressionContext ctx) {
        // parse the lhs
        Expression leftExpression = parseExpression(ctx.expression(0));
        int expressionCount = ctx.expression().size();
        List<Expression> expressions = new LinkedList<Expression>();
        Expression rightExpression = null;

        // parse the indices
        for (int i = 1; i < expressionCount; ++i) {
            expressions.add(parseExpression(ctx.expression(i)));
        }
        if (expressionCount == 2) {
            // If only one index, treat as single expression
            rightExpression = expressions.get(0);
            // unless it's a spread operator...
            if (rightExpression instanceof SpreadExpression) {
                ListExpression wrapped = new ListExpression();
                wrapped.addExpression(rightExpression);
                rightExpression = setupNodeLocation(wrapped, ctx.expression(1));
            }
        } else {
            // Otherwise, setup as list expression
            ListExpression listExpression = new ListExpression(expressions);
            listExpression.setWrapped(true);
            rightExpression = listExpression;
            // if nonempty, set location info for index list
            if (expressionCount > 2) {
                Token start = ctx.expression(1).getStart();
                Token stop = ctx.expression(expressionCount - 1).getStart();
                listExpression.setLineNumber(start.getLine());
                listExpression.setColumnNumber(start.getCharPositionInLine() + 1);
                listExpression.setLastLineNumber(stop.getLine());
                listExpression.setLastColumnNumber(stop.getCharPositionInLine() + 1 + stop.getText().length());
            }
        }
        BinaryExpression binaryExpression = new BinaryExpression(leftExpression, createToken(ctx.LBRACK(), 1), rightExpression);
        return setupNodeLocation(binaryExpression, ctx);
    }

    public Expression parseExpression(GroovyLangParser.CmdExpressionContext cmdExpressionRuleContext) {
        boolean hasExpression = asBoolean(cmdExpressionRuleContext.expression());
        boolean hasPropertyAccess = asBoolean(cmdExpressionRuleContext.IDENTIFIER()) || asBoolean(cmdExpressionRuleContext.STRING()) || asBoolean(cmdExpressionRuleContext.gstring());

        Expression expression = hasExpression ? parseExpression(cmdExpressionRuleContext.expression()) : null;
        expression = parseCallExpressionRule(cmdExpressionRuleContext.c, cmdExpressionRuleContext.n, null, expression, cmdExpressionRuleContext.genericDeclarationList());
        if (hasExpression) {
            Token op = cmdExpressionRuleContext.op;
            ((MethodCallExpression)expression).setSpreadSafe(op.getType() == GroovyLangParser.STAR_DOT);
            ((MethodCallExpression)expression).setSafe(op.getType() == GroovyLangParser.SAFE_DOT);
        }

        for (GroovyLangParser.NonKwCallExpressionRuleContext nonKwCallExpressionRuleContext : cmdExpressionRuleContext.nonKwCallExpressionRule()) {
            if (nonKwCallExpressionRuleContext == cmdExpressionRuleContext.n) {
                continue;
            }

            expression = parseCallExpressionRule(null, nonKwCallExpressionRuleContext, null, expression, null);

        }

        if (hasPropertyAccess) {
            expression = new PropertyExpression(expression, this.parseName(cmdExpressionRuleContext.IDENTIFIER(), cmdExpressionRuleContext.STRING(), cmdExpressionRuleContext.gstring()));
        }

        return setupNodeLocation(expression, cmdExpressionRuleContext);
    }

    public Expression parseExpression(GroovyLangParser.CallExpressionContext ctx) {
        Expression expression = parseCallExpressionRule(null, null, ctx.callRule(), null, null);

        return setupNodeLocation(expression, ctx);
    }
    /**
     * If argument list contains closure and named argument, the argument list's struture looks like as follows:
     *
     *      ArgumentListExpression
     *            MapExpression (NOT NamedArgumentListExpression!)
     *            ClosureExpression
     *
     * Original structure:
     *
     *      TupleExpression
     *            NamedArgumentListExpression
     *            ClosureExpression
     *
     * @param argumentListExpression
     * @return
     *
     */
    private TupleExpression convertArgumentList(TupleExpression argumentListExpression) {
        if (argumentListExpression instanceof ArgumentListExpression) {
            return argumentListExpression;
        }

        List<Expression> result = new LinkedList<Expression>();

        int namedArgumentListExpressionCnt = 0, closureExpressionCnt = 0;
        for (Expression expression : argumentListExpression.getExpressions()) {

            if (expression instanceof NamedArgumentListExpression) {
                expression = setupNodeLocation(new MapExpression(((NamedArgumentListExpression) expression).getMapEntryExpressions()), expression);
                namedArgumentListExpressionCnt++;
            } else if (expression instanceof ClosureExpression) {
                closureExpressionCnt++;
            }

            result.add(expression);
        }

        if (namedArgumentListExpressionCnt > 0 && closureExpressionCnt > 0) {
            return setupNodeLocation(new ArgumentListExpression(result), argumentListExpression);
        }

        return argumentListExpression;
    }

    private Expression parseName(ParseTree... nodes) {
        for (ParseTree node : nodes) {
            if (null == node) {
                continue;
            }

            if (node instanceof TerminalNode) {
                TerminalNode tn = ((TerminalNode) node);
                Token token = tn.getSymbol();
                int type = token.getType();

                // STRING
                if (GroovyLangParser.STRING == type) {
                    return setupNodeLocation(parseConstantStringToken(token), token);
                }

                // IDENTIFIER, KW_THIS, KW_SUPER
                return setupNodeLocation(new ConstantExpression(tn.getText()), token);
            }

            if (node instanceof ParserRuleContext) {
                ParserRuleContext ctx = (ParserRuleContext) node;

                // selectorName
                if (ctx instanceof GroovyLangParser.SelectorNameContext) {
                    return setupNodeLocation(new ConstantExpression(ctx.getText()), ctx);
                }

                // gstring
                if (ctx instanceof GroovyLangParser.GstringContext) {
                    return setupNodeLocation(parseExpression((GroovyLangParser.GstringContext) ctx), ctx);
                }

                // LPAREN expression RPAREN
                if (ctx instanceof GroovyLangParser.ExpressionContext) {
                    return setupNodeLocation(parseExpression((GroovyLangParser.ExpressionContext) ctx), ctx);
                }
            }
        }

        return null;
    }

    public TupleExpression parseArgumentListRule(GroovyLangParser.ArgumentListRuleContext argumentListRuleContext) {
        TupleExpression argumentListExpression = (TupleExpression) createArgumentList(argumentListRuleContext.argumentList());

        for (GroovyLangParser.ClosureExpressionRuleContext closureExpressionRuleContext : argumentListRuleContext.closureExpressionRule()) {
            argumentListExpression.addExpression(parseExpression(closureExpressionRuleContext));
        }

        return convertArgumentList(argumentListExpression);
    }

    /*
     * "@baseContext" does not support in antlr4.5.3, so parse CallExpressionRule and ClosureCallExpressionRule in the same time, which is not elegant currently.
     */
    public Expression parseCallExpressionRule(GroovyLangParser.CallExpressionRuleContext ctx, GroovyLangParser.NonKwCallExpressionRuleContext nonKwCallExpressionRuleContext, GroovyLangParser.CallRuleContext callRuleContext, Expression expression, GroovyLangParser.GenericDeclarationListContext genericDeclarationListContext) {
        Expression method;
        boolean isCall = asBoolean(callRuleContext);

        if (isCall) {
            method = new ConstantExpression(CALL);
        } else {
            if (asBoolean(ctx)) {
                method = this.parseName(ctx.selectorName(), ctx.STRING(), ctx.gstring(), ctx.mne);
            } else {
                method = this.parseName(nonKwCallExpressionRuleContext.IDENTIFIER(), nonKwCallExpressionRuleContext.STRING(), nonKwCallExpressionRuleContext.gstring());
            }
        }

        if (null == method) {
            throw createParsingFailedException(new IllegalStateException("method should not be null"));
        }

        List<TupleExpression> argumentListExpressionList = new LinkedList<TupleExpression>();

        List<? extends GroovyLangParser.ArgumentListRuleContext> argumentListRuleContextList = isCall ? callRuleContext.argumentListRule() : asBoolean(ctx) ? ctx.argumentListRule() : nonKwCallExpressionRuleContext.argumentListRule();
        if (asBoolean(argumentListRuleContextList)) {
            for (GroovyLangParser.ArgumentListRuleContext argumentListRuleContext : argumentListRuleContextList) {
                argumentListExpressionList.add(parseArgumentListRule(argumentListRuleContext));
            }
        } else {
            TupleExpression argumentListExpression = (TupleExpression) createArgumentList(isCall ? callRuleContext.argumentList() : asBoolean(ctx) ? ctx.argumentList() : nonKwCallExpressionRuleContext.argumentList());

            argumentListExpressionList.add(convertArgumentList(argumentListExpression));
        }

        boolean implicitThis = !isCall && !asBoolean(expression);

        MethodCallExpression methodCallExpression = new MethodCallExpression(isCall ? null != callRuleContext.a ? parseExpression(callRuleContext.a) : (null != callRuleContext.c ? parseExpression(callRuleContext.c) : parseExpression(callRuleContext.mne))
                                                                                 : (asBoolean(expression) ? expression : VariableExpression.THIS_EXPRESSION)
                                                                   , method, argumentListExpressionList.get(0));

        methodCallExpression.setImplicitThis(implicitThis);

        if (argumentListExpressionList.size() > 1) {
            methodCallExpression = DefaultGroovyMethods.inject(argumentListExpressionList.subList(1, argumentListExpressionList.size()), methodCallExpression, new Closure<MethodCallExpression>(null, null) {
                public MethodCallExpression doCall(MethodCallExpression expr, TupleExpression argumentListExpression) {
                    MethodCallExpression mce = new MethodCallExpression(expr, CALL, argumentListExpression);
                    mce.setImplicitThis(false);

                    return setupNodeLocation(mce, argumentListExpression);
                }
            });
        }

        if (asBoolean(genericDeclarationListContext)) {
            methodCallExpression.setGenericsTypes(parseGenericDeclaration(genericDeclarationListContext));
        }

        return setupNodeLocation(methodCallExpression, isCall ? callRuleContext : asBoolean(ctx) ? ctx : nonKwCallExpressionRuleContext);
    }

    public ConstructorCallExpression parseExpression(GroovyLangParser.ConstructorCallExpressionContext ctx) {
        Expression argumentListExpression = createArgumentList(ctx.argumentList());
        ConstructorCallExpression expression = new ConstructorCallExpression(asBoolean(ctx.KW_SUPER()) ? ClassNode.SUPER : ClassNode.THIS, argumentListExpression);
        return setupNodeLocation(expression, ctx);
    }

    public ClassNode parseClassNameExpression(GroovyLangParser.ClassNameExpressionContext ctx) {
        ClassNode classNode;

        if (asBoolean(ctx.BUILT_IN_TYPE())) {
            classNode = ClassHelper.make(ctx.BUILT_IN_TYPE().getText());
        } else {
            classNode = ClassHelper.make(DefaultGroovyMethods.join(ctx.pathExpression().IDENTIFIER(), "."));
        }

        return setupNodeLocation(classNode, ctx);
    }

    public ClassNode parseExpression(GroovyLangParser.GenericClassNameExpressionContext ctx) {
        ClassNode classNode = parseClassNameExpression(ctx.classNameExpression());

        if (asBoolean(ctx.LBRACK())) {
            for (int i = 0, n = ctx.LBRACK().size(); i < n; i++) {
                classNode = classNode.makeArray();
            }
        } else {
            // Groovy's bug? array's generics type will be ignored. e.g. List<String>[]... p
            classNode.setGenericsTypes(parseGenericList(ctx.genericList()));
        }

        if (asBoolean(ctx.ELLIPSIS())) {
            classNode = classNode.makeArray();
        }

        return setupNodeLocation(classNode, ctx);
    }

    public GenericsType[] parseGenericList(GroovyLangParser.GenericListContext ctx) {
        if (ctx == null)
            return null;
        List<GenericsType> collect = collect(ctx.genericListElement(), new Closure<GenericsType>(null, null) {
            public GenericsType doCall(GroovyLangParser.GenericListElementContext it) {
                if (it instanceof GroovyLangParser.GenericsConcreteElementContext)
                    return setupNodeLocation(new GenericsType(parseExpression(((GroovyLangParser.GenericsConcreteElementContext)it).genericClassNameExpression())), it);
                else {
                    assert it instanceof GroovyLangParser.GenericsWildcardElementContext;
                    GroovyLangParser.GenericsWildcardElementContext gwec = (GroovyLangParser.GenericsWildcardElementContext)it;
                    ClassNode baseType = ClassHelper.makeWithoutCaching("?");
                    ClassNode[] upperBounds = null;
                    ClassNode lowerBound = null;
                    if (asBoolean(gwec.KW_EXTENDS())) {
                        ClassNode classNode = parseExpression(gwec.genericClassNameExpression());
                        upperBounds = new ClassNode[]{ classNode };
                    } else if (asBoolean(gwec.KW_SUPER()))
                        lowerBound = parseExpression(gwec.genericClassNameExpression());

                    GenericsType type = new GenericsType(baseType, upperBounds, lowerBound);
                    type.setWildcard(true);
                    type.setName("?");
                    return setupNodeLocation(type, it);
                }

            }
        });
        return collect.toArray(new GenericsType[collect.size()]);
    }

    public GenericsType[] parseGenericDeclaration(GroovyLangParser.GenericDeclarationListContext ctx) {
        if (ctx == null)
            return null;
        List<GenericsType> genericTypes = collect(ctx.genericsDeclarationElement(), new Closure<GenericsType>(null, null) {
            public GenericsType doCall(GroovyLangParser.GenericsDeclarationElementContext it) {
                ClassNode classNode = parseExpression(it.genericClassNameExpression(0));
                ClassNode[] upperBounds = null;
                if (asBoolean(it.KW_EXTENDS())) {
                    List<? extends GroovyLangParser.GenericClassNameExpressionContext> genericClassNameExpressionContexts = DefaultGroovyMethods.toList(it.genericClassNameExpression());
                    upperBounds = collect(genericClassNameExpressionContexts.subList(1, genericClassNameExpressionContexts.size()), new Closure<ClassNode>(this, this) {
                        public ClassNode doCall(GroovyLangParser.GenericClassNameExpressionContext it) {
                            return parseExpression(it);
                        }
                    }).toArray(new ClassNode[0]);
                }
                GenericsType type = new GenericsType(classNode, upperBounds, null);
                return setupNodeLocation(type, it);
            }
        });
        return  genericTypes.toArray(new GenericsType[genericTypes.size()]);
    }

    public List<DeclarationExpression> parseDeclaration(GroovyLangParser.DeclarationRuleContext ctx) {
        List<DeclarationExpression> declarations = new LinkedList<DeclarationExpression>();

        if (asBoolean(ctx.tupleDeclaration())) {
            DeclarationExpression declarationExpression = parseTupleDeclaration(ctx.tupleDeclaration(), asBoolean(ctx.KW_FINAL()));

            declarations.add(declarationExpression);

            return declarations;
        }

        ClassNode nullClassNode = new ClassNode("", Modifier.PUBLIC, ClassHelper.OBJECT_TYPE);
        AnnotatedNode node = this.parseMember(nullClassNode, ctx.fieldDeclaration());

        for (PropertyNode propertyNode : nullClassNode.getProperties()) {
            VariableExpression left = new VariableExpression(propertyNode.getName(), propertyNode.getType());
            left.setModifiers(propertyNode.getModifiers() & Opcodes.ACC_FINAL);

            Expression initialValue = propertyNode.getInitialExpression();
            initialValue = initialValue != null ? initialValue : setupNodeLocation(new EmptyExpression(),ctx);

            DeclarationExpression expression = new DeclarationExpression(left, propertyNode.getAssignToken(), initialValue);
            expression.addAnnotations(propertyNode.getField().getAnnotations());

            declarations.add(setupNodeLocation(expression, propertyNode));
        }

        int declarationsSize = declarations.size();
        if (declarationsSize == 1) {
            setupNodeLocation(declarations.get(0), ctx);
        } else if (declarationsSize > 0) {
            DeclarationExpression declarationExpression = declarations.get(0);
            // Tweak start of first declaration
            declarationExpression.setLineNumber(ctx.getStart().getLine());
            declarationExpression.setColumnNumber(ctx.getStart().getCharPositionInLine() + 1);
        }

        return declarations;
    }

    private org.codehaus.groovy.syntax.Token createGroovyToken(Token token, int type) {
        if (null == token) {
            throw new IllegalArgumentException("token should not be null");
        }

        return new org.codehaus.groovy.syntax.Token(type, token.getText(), token.getLine(), token.getCharPositionInLine());
    }

    public VariableExpression parseTupleVariableDeclaration(GroovyLangParser.TupleVariableDeclarationContext ctx) {
        ClassNode type = asBoolean(ctx.genericClassNameExpression())
                ? parseExpression(ctx.genericClassNameExpression())
                : ClassHelper.OBJECT_TYPE;

        return setupNodeLocation(new VariableExpression(ctx.IDENTIFIER().getText(), type), ctx);
    }

    public DeclarationExpression parseTupleDeclaration(GroovyLangParser.TupleDeclarationContext ctx, boolean isFinal) {
        // tuple must have an initial value.
        if (null == ctx.expression()) {
            throw createParsingFailedException(new InvalidSyntaxException("tuple declaration must have an initial value.", ctx));
        }

        List<Expression> variables = new LinkedList<Expression>();

        for (GroovyLangParser.TupleVariableDeclarationContext tupleVariableDeclarationContext : ctx.tupleVariableDeclaration()) {
            VariableExpression variableExpression = parseTupleVariableDeclaration(tupleVariableDeclarationContext);

            if (isFinal) {
                variableExpression.setModifiers(Opcodes.ACC_FINAL);
            }

            variables.add(variableExpression);
        }

        ArgumentListExpression argumentListExpression = new ArgumentListExpression(variables);
        org.codehaus.groovy.syntax.Token token = createGroovyToken(ctx.ASSIGN().getSymbol(), Types.ASSIGN);

        Expression initialValue = (ctx != null) ? parseExpression(ctx.expression())
                : setupNodeLocation(new EmptyExpression(),ctx);

        DeclarationExpression declarationExpression  = new DeclarationExpression(argumentListExpression, token, initialValue);

        return setupNodeLocation(declarationExpression, ctx);
    }

    private Expression createArgumentList(GroovyLangParser.ArgumentListContext ctx) {
        final List<MapEntryExpression> mapArgs = new LinkedList<MapEntryExpression>();
        final List<Expression> expressions = new LinkedList<Expression>();

        if (ctx != null) {
            for (ParseTree it : ctx.children) {
                if (it instanceof GroovyLangParser.ArgumentContext) {
                    if (asBoolean(((GroovyLangParser.ArgumentContext)it).mapEntry())) {
                        mapArgs.add(parseExpression(((GroovyLangParser.ArgumentContext) it).mapEntry()));
                    } else {
                        expressions.add(parseExpression(((GroovyLangParser.ArgumentContext) it).expression()));
                    }
                } else if (it instanceof GroovyLangParser.ClosureExpressionRuleContext) {
                    expressions.add(parseExpression((GroovyLangParser.ClosureExpressionRuleContext) it));
                }
            }

        }

        if (asBoolean(expressions)) {
            if (asBoolean(mapArgs))
                expressions.add(0, new MapExpression(mapArgs));

            return setupNodeLocation(new ArgumentListExpression(expressions), ctx);
        } else {
            if (asBoolean(mapArgs))
                return setupNodeLocation(new TupleExpression(new NamedArgumentListExpression(mapArgs)), ctx);
            else
                return setupNodeLocation(new ArgumentListExpression(), ctx);
        }

    }

    public void attachAnnotations(AnnotatedNode node, List<? extends GroovyLangParser.AnnotationClauseContext> ctxs) {
        for (GroovyLangParser.AnnotationClauseContext ctx : ctxs) {
            AnnotationNode annotation = parseAnnotation(ctx);
            node.addAnnotation(annotation);
        }
    }

    private void attachTraitTransformAnnotation(ClassNode classNode) {
        classNode.addAnnotation(new AnnotationNode(ClassHelper.make(GROOVY_TRANSFORM_TRAIT)));
    }

    public List<AnnotationNode> parseAnnotations(List<? extends GroovyLangParser.AnnotationClauseContext> ctxs) {
        return collect(ctxs, new Closure<AnnotationNode>(null, null) {
            public AnnotationNode doCall(GroovyLangParser.AnnotationClauseContext it) {return parseAnnotation(it);}
        });
    }

    public AnnotationNode parseAnnotation(GroovyLangParser.AnnotationClauseContext ctx) {
        AnnotationNode node = new AnnotationNode(parseExpression(ctx.genericClassNameExpression()));
        if (asBoolean(ctx.annotationElement()))
            node.addMember("value", parseAnnotationElement(ctx.annotationElement()));
        else {
            for (GroovyLangParser.AnnotationElementPairContext pair : ctx.annotationElementPair()) {
                node.addMember(pair.IDENTIFIER().getText(), parseAnnotationElement(pair.annotationElement()));
            }

        }


        return setupNodeLocation(node, ctx);
    }

    public Expression parseAnnotationElement(GroovyLangParser.AnnotationElementContext ctx) {
        GroovyLangParser.AnnotationClauseContext annotationClause = ctx.annotationClause();
        if (asBoolean(annotationClause))
            return setupNodeLocation(new AnnotationConstantExpression(parseAnnotation(annotationClause)), annotationClause);
        else return parseExpression(ctx.annotationParameter());
    }

    public ClassNode[] parseThrowsClause(GroovyLangParser.ThrowsClauseContext ctx) {
        List list = asBoolean(ctx)
                ? collect(ctx.classNameExpression(), new Closure<ClassNode>(null, null) {
            public ClassNode doCall(GroovyLangParser.ClassNameExpressionContext it) {return parseClassNameExpression(it);}
        })
                : new LinkedList();
        return (ClassNode[])list.toArray(new ClassNode[list.size()]);
    }

    /**
     * @param node
     * @param cardinality Used for handling GT ">" operator, which can be repeated to give bitwise shifts >> or >>>
     * @return
     */
    public org.codehaus.groovy.syntax.Token createToken(TerminalNode node, int cardinality) {
        String text = multiply(node.getText(), cardinality);
        return new org.codehaus.groovy.syntax.Token(node.getText().equals("..<") || node.getText().equals("..")
                ? Types.RANGE_OPERATOR
                : Types.lookup(text, Types.ANY), text, node.getSymbol().getLine(), node.getSymbol().getCharPositionInLine() + 1);
    }

    /**
     * @param node
     * @return
     */
    public org.codehaus.groovy.syntax.Token createToken(TerminalNode node) {
        return createToken(node, 1);
    }

    public ClassNode parseTypeDeclaration(GroovyLangParser.TypeDeclarationContext ctx) {
        return !asBoolean(ctx) || ctx.KW_DEF() != null
                ? ClassHelper.OBJECT_TYPE
                : setupNodeLocation(parseExpression(ctx.genericClassNameExpression()), ctx);
    }

    public ArrayExpression parse(GroovyLangParser.NewArrayRuleContext ctx) {
        List<Expression> collect = collect(ctx.expression(), new Closure<Expression>(null, null) {
            public Expression doCall(GroovyLangParser.ExpressionContext it) {return parseExpression(it);}
        });

        ArrayExpression expression = new ArrayExpression(parseClassNameExpression(ctx.classNameExpression()), new LinkedList<Expression>(), collect);
        return setupNodeLocation(expression, ctx);
    }

    public ConstructorCallExpression parse(GroovyLangParser.NewInstanceRuleContext ctx) {
        ClassNode creatingClass = asBoolean(ctx.genericClassNameExpression())
                ? parseExpression(ctx.genericClassNameExpression())
                : parseClassNameExpression(ctx.classNameExpression());
        if (asBoolean(ctx.LT())) creatingClass.setGenericsTypes(new GenericsType[0]);

        ConstructorCallExpression expression;
        if (!asBoolean(ctx.classBody())) {
            expression = setupNodeLocation(new ConstructorCallExpression(creatingClass, createArgumentList(ctx.argumentList())), ctx);
        } else {
            ClassNode outer;

            if (!this.classNodeStack.isEmpty()) {
                outer = this.classNodeStack.peek();
            } else {
                outer = moduleNode.getScriptClassDummy();
            }

            InnerClassNode classNode = new InnerClassNode(outer, this.genAnonymousClassName(outer.getName()), Opcodes.ACC_PUBLIC, ClassHelper.make(creatingClass.getName()));
            classNode.setSuperClass(creatingClass);


            expression = setupNodeLocation(new ConstructorCallExpression(classNode, createArgumentList(ctx.argumentList())), ctx);
            expression.setUsingAnonymousInnerClass(true);
            classNode.setAnonymous(true);

            if (!this.innerClassesDefinedInMethodStack.isEmpty()) {
                DefaultGroovyMethods.last(this.innerClassesDefinedInMethodStack).add(classNode);
            }

//            this.moduleNode.addClass(classNode);
            classes.add(classNode);
            this.classNodeStack.add(classNode);
            parseClassBody(classNode, ctx.classBody());
            this.classNodeStack.pop();
        }

        return expression;
    }

    public Parameter[] parseParameters(GroovyLangParser.ArgumentDeclarationListContext ctx) {
        List<Parameter> parameterList = ctx == null || ctx.argumentDeclaration() == null ?
                new LinkedList<Parameter>() :
                collect(ctx.argumentDeclaration(), new Closure<Parameter>(null, null) {
                    public Parameter doCall(GroovyLangParser.ArgumentDeclarationContext it) {
                        Parameter parameter = new Parameter(parseTypeDeclaration(it.typeDeclaration()), it.IDENTIFIER().getText());
                        attachAnnotations(parameter, it.annotationClause());

                        if (asBoolean(it.KW_FINAL())) {
                            parameter.setModifiers(Opcodes.ACC_FINAL);
                        }

                        if (asBoolean(it.expression()))
                            parameter.setInitialExpression(parseExpression(it.expression()));

                        return setupNodeLocation(parameter, it);
                    }
                });
        return parameterList.toArray(new Parameter[parameterList.size()]);
    }

    public MethodNode getOrCreateClinitMethod(ClassNode classNode) {
        MethodNode methodNode = DefaultGroovyMethods.find(classNode.getMethods(), new Closure<Boolean>(null, null) {
            public Boolean doCall(MethodNode it) {return it.getName().equals("<clinit>");}
        });
        if (!asBoolean(methodNode)) {
            methodNode = new MethodNode("<clinit>", Opcodes.ACC_STATIC, ClassHelper.VOID_TYPE, new Parameter[0], new ClassNode[0], new BlockStatement());
            methodNode.setSynthetic(true);
            classNode.addMethod(methodNode);
        }

        return methodNode;
    }

    /**
     * Sets location(lineNumber, colNumber, lastLineNumber, lastColumnNumber) for node using standard context information.
     * Note: this method is implemented to be closed over ASTNode. It returns same node as it received in arguments.
     *
     * @param astNode Node to be modified.
     * @param ctx     Context from which information is obtained.
     * @return Modified astNode.
     */
    public <T extends ASTNode> T setupNodeLocation(T astNode, ParserRuleContext ctx) {
        if (null == ctx) {
            return astNode;
        }

        Token start = ctx.getStart();
        Token stop = ctx.getStop();

        astNode.setLineNumber(start.getLine());
        astNode.setColumnNumber(start.getCharPositionInLine() + 1);
        astNode.setLastLineNumber(stop.getLine());
        astNode.setLastColumnNumber(stop.getCharPositionInLine() + 1 + stop.getText().length());
//        System.err.println(astNode.getClass().getSimpleName() + " at " + astNode.getLineNumber() + ":" + astNode.getColumnNumber());
        return astNode;
    }

    public <T extends ASTNode> T setupNodeLocation(T astNode, Token token) {
        astNode.setLineNumber(token.getLine());
        astNode.setColumnNumber(token.getCharPositionInLine() + 1);
        astNode.setLastLineNumber(token.getLine());
        astNode.setLastColumnNumber(token.getCharPositionInLine() + 1 + token.getText().length());
//        System.err.println(astNode.getClass().getSimpleName() + " at " + astNode.getLineNumber() + ":" + astNode.getColumnNumber());
        return astNode;
    }

    public <T extends ASTNode> T setupNodeLocation(T astNode, ASTNode source) {
        astNode.setLineNumber(source.getLineNumber());
        astNode.setColumnNumber(source.getColumnNumber());
        astNode.setLastLineNumber(source.getLastLineNumber());
        astNode.setLastColumnNumber(source.getLastColumnNumber());
        return astNode;
    }

    public int parseClassModifiers(List<? extends GroovyLangParser.ClassModifierContext> ctxs) {
        List<TerminalNode> visibilityModifiers = new LinkedList<TerminalNode>();
        int modifiers = 0;
        for (int i = 0; i < ctxs.size(); i++) {
            for (Object ctx : ctxs.get(i).children) {
                ParseTree child = null;
                if (ctx instanceof List) {
                    List list = (List)ctx;
                    assert list.size() == 1;
                    child = (ParseTree)list.get(0);
                }
                else
                    child = (ParseTree)ctx;

                assert child instanceof TerminalNode;
                switch (((TerminalNode)child).getSymbol().getType()) {
                    case GroovyLangLexer.VISIBILITY_MODIFIER:
                        visibilityModifiers.add((TerminalNode)child);
                        break;
                    case GroovyLangLexer.KW_STATIC:
                        modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_STATIC, (TerminalNode)child);
                        break;
                    case GroovyLangLexer.KW_ABSTRACT:
                        modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_ABSTRACT, (TerminalNode)child);
                        break;
                    case GroovyLangLexer.KW_FINAL:
                        modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_FINAL, (TerminalNode)child);
                        break;
                    case GroovyLangLexer.KW_STRICTFP:
                        modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_STRICT, (TerminalNode)child);
                        break;
                }
            }
        }

        if (asBoolean(visibilityModifiers))
            modifiers |= parseVisibilityModifiers(visibilityModifiers, 0);
        else modifiers |= Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC;
        return modifiers;
    }

    public int checkModifierDuplication(int modifier, int opcode, TerminalNode node) {
        if ((modifier & opcode) == 0) return modifier | opcode;
        else {
            Token symbol = node.getSymbol();

            Integer line = symbol.getLine();
            Integer col = symbol.getCharPositionInLine() + 1;
            sourceUnit.addError(new SyntaxException("Cannot repeat modifier: " + symbol.getText() + " at line: " + String.valueOf(line) + " column: " + String.valueOf(col) + ". File: " + sourceUnit.getName(), line, col));
            return modifier;
        }

    }

    /**
     * Traverse through modifiers, and combine them in one int value. Raise an error if there is multiple occurrences of same modifier.
     *
     * @param ctxList                   modifiers list.
     * @param defaultVisibilityModifier Default visibility modifier. Can be null. Applied if providen, and no visibility modifier exists in the ctxList.
     * @return tuple of int modifier and boolean flag, signalising visibility modifiers presence(true if there is visibility modifier in list, false otherwise).
     * @see #checkModifierDuplication(int, int, TerminalNode)
     */
    public List<Object> parseModifiers(List<? extends GroovyLangParser.MemberModifierContext> ctxList, Integer defaultVisibilityModifier) {
        int modifiers = 0;
        boolean hasVisibilityModifier = false;
        for (GroovyLangParser.MemberModifierContext it : ctxList) {
            TerminalNode child = (TerminalNode) it.getChild(0);
            switch (child.getSymbol().getType()) {
                case GroovyLangLexer.KW_STATIC:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_STATIC, child);
                    break;
                case GroovyLangLexer.KW_ABSTRACT:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_ABSTRACT, child);
                    break;
                case GroovyLangLexer.KW_FINAL:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_FINAL, child);
                    break;
                case GroovyLangLexer.KW_NATIVE:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_NATIVE, child);
                    break;
                case GroovyLangLexer.KW_SYNCHRONIZED:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_SYNCHRONIZED, child);
                    break;
                case GroovyLangLexer.KW_TRANSIENT:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_TRANSIENT, child);
                    break;
                case GroovyLangLexer.KW_VOLATILE:
                    modifiers |= checkModifierDuplication(modifiers, Opcodes.ACC_VOLATILE, child);
                    break;
                case GroovyLangLexer.VISIBILITY_MODIFIER:
                    modifiers |= parseVisibilityModifiers(child);
                    hasVisibilityModifier = true;
                    break;
            }
        }
        if (!hasVisibilityModifier && defaultVisibilityModifier != null) modifiers |= defaultVisibilityModifier;

        return new LinkedList<Object>(Arrays.asList(modifiers, hasVisibilityModifier));
    }

    /**
     * Traverse through modifiers, and combine them in one int value. Raise an error if there is multiple occurrences of same modifier.
     *
     * @param ctxList                   modifiers list.
     * @return tuple of int modifier and boolean flag, signalising visibility modifiers presence(true if there is visibility modifier in list, false otherwise).
     * @see #checkModifierDuplication(int, int, TerminalNode)
     */
    public List<Object> parseModifiers(List<? extends GroovyLangParser.MemberModifierContext> ctxList) {
        return parseModifiers(ctxList, null);
    }

    public void reportError(String text, int line, int col) {
        sourceUnit.addError(new SyntaxException(text, line, col));
    }

    public int parseVisibilityModifiers(TerminalNode modifier) {
        assert modifier.getSymbol().getType() == GroovyLangLexer.VISIBILITY_MODIFIER;
        if (DefaultGroovyMethods.isCase(PUBLIC, modifier.getSymbol().getText()))
            return Opcodes.ACC_PUBLIC;
        else if (DefaultGroovyMethods.isCase(PRIVATE, modifier.getSymbol().getText()))
            return Opcodes.ACC_PRIVATE;
        else if (DefaultGroovyMethods.isCase(PROTECTED, modifier.getSymbol().getText()))
            return Opcodes.ACC_PROTECTED;
        else
            throw new AssertionError(modifier.getSymbol().getText() + " is not a valid visibility modifier!");
    }

    public int parseVisibilityModifiers(List<TerminalNode> modifiers, int defaultValue) {
        if (! asBoolean(modifiers)) return defaultValue;

        if (modifiers.size() > 1) {
            Token modifier = modifiers.get(1).getSymbol();

            Integer line = modifier.getLine();
            Integer col = modifier.getCharPositionInLine() + 1;

            reportError("Cannot specify modifier: " + modifier.getText() + " when access scope has already been defined at line: " + String.valueOf(line) + " column: " + String.valueOf(col) + ". File: " + sourceUnit.getName(), line, col);
        }


        return parseVisibilityModifiers(modifiers.get(0));
    }

    /**
     * Method for construct string from string literal handling empty strings.
     *
     * @param node
     * @return
     */
    public String parseString(TerminalNode node) {
        String t = node.getText();

        if ("''".equals(t) || "\"\"".equals(t)) {
            return "";
        }

        return asBoolean(t) ? DefaultGroovyMethods.getAt(StringUtil.replaceEscapes(t, StringUtil.NONE_SLASHY), new IntRange(true, 1, -2)) : t;
    }

    private void addEmptyReturnStatement() {
        moduleNode.addStatement(new ReturnStatement(new ConstantExpression(null)));
    }

    /**
     * Attach doc comment to member node as meta data
     */
    private void attachDocCommentAsMetaData(ASTNode node, ParserRuleContext ctx) {
        ParseTree docCommentNode = this.findDocCommentByNode(ctx);

        if (null == docCommentNode) {
            return;
        }

        node.putNodeMetaData(DOC_COMMENT, docCommentNode.getText());
    }

    private ParseTree findDocCommentByNode(ParserRuleContext node) {
        ParseTree docCommentNode = null;

        for (ParseTree child : node.getParent().children) {
            if (node == child) {
                return docCommentNode;
            }

            if (!(child instanceof TerminalNode)) {
                docCommentNode = null;

                continue;
            }


            // doc comments are treated as NL
            if (((TerminalNode) child).getSymbol().getType() != GroovyLangParser.NL) {
                continue;
            }

            if (!child.getText().startsWith(DOC_COMMENT_PREFIX)) {
                continue;
            }

            docCommentNode = child;
        }

        throw new GroovyBugError("node can not be found"); // The exception should never be thrown!
    }

    private boolean isTrait(ClassNode classNode) {
        return classNode.getAnnotations(ClassHelper.make(GROOVY_TRANSFORM_TRAIT)).size() > 0;
    }

    private static Set<String> CONSTRUCTOR_VISIBILITY_MODIFIER_SET = new HashSet(Arrays.asList(PUBLIC, PROTECTED, PRIVATE));

    private static final Map<ClassNode, Object> TYPE_DEFAULT_VALUE_MAP = new HashMap<ClassNode, Object>() {
        {
            this.put(ClassHelper.int_TYPE,      0);
            this.put(ClassHelper.long_TYPE,     0L);
            this.put(ClassHelper.double_TYPE,   0.0D);
            this.put(ClassHelper.float_TYPE,    0.0F);
            this.put(ClassHelper.short_TYPE,    (short) 0);
            this.put(ClassHelper.byte_TYPE,     (byte)  0);
            this.put(ClassHelper.char_TYPE,     (char)  0);
            this.put(ClassHelper.boolean_TYPE,  Boolean.FALSE);
        }
    };

    private Object findDefaultValueByType(ClassNode type) {
        return TYPE_DEFAULT_VALUE_MAP.get(type);
    }


    private String readSourceCode(SourceUnit sourceUnit) {
        String text = null;
        try {
            text = DefaultGroovyMethods.getText(
                    new BufferedReader(
                            sourceUnit.getSource().getReader()));
        } catch (IOException e) {
            log.severe(createExceptionMessage(e));
            throw new RuntimeException("Error occurred when reading source code.", e);
        }

        return text;
    }


    private void setupErrorListener(GroovyLangParser parser) {
        parser.removeErrorListeners();
        parser.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(
                    Recognizer recognizer,
                    Object offendingSymbol, int line, int charPositionInLine,
                    String msg, RecognitionException e) {

                sourceUnit.getErrorCollector().addFatalError(new SyntaxErrorMessage(new SyntaxException(msg, line, charPositionInLine+1), sourceUnit));
            }

            /*
            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
                log.fine("Ambiguity at " + startIndex + " - " + stopIndex);
            }


            @Override
            public void reportAttemptingFullContext(
                    Parser recognizer,
                    DFA dfa, int startIndex, int stopIndex,
                    BitSet conflictingAlts, ATNConfigSet configs) {
                log.fine("Attempting Full Context at " + startIndex + " - " + stopIndex);
            }

            @Override
            public void reportContextSensitivity(
                    Parser recognizer,
                    DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
                log.fine("Context Sensitivity at " + startIndex + " - " + stopIndex);
            }
            */
        });
    }

    private void logTreeStr(GroovyLangParser.CompilationUnitContext tree) {
        final StringBuilder s = new StringBuilder();
        new ParseTreeWalker().walk(new ParseTreeListener() {
            @Override
            public void visitTerminal(TerminalNode node) {
                s.append(multiply(".\t", indent));
                s.append(String.valueOf(node));
                s.append("\n");
            }

            @Override
            public void visitErrorNode(ErrorNode node) {
            }

            @Override
            public void enterEveryRule(final ParserRuleContext ctx) {
                s.append(multiply(".\t", indent));
                s.append(GroovyLangParser.ruleNames[ctx.getRuleIndex()] + ": {");
                s.append("\n");
                indent++;
            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                indent--;
                s.append(multiply(".\t", indent));
                s.append("}");
                s.append("\n");
            }

            public int getIndent() {
                return indent;
            }

            public void setIndent(int indent) {
                this.indent = indent;
            }

            private int indent;
        }, tree);

        log.fine((multiply("=", 60)) + "\n" + String.valueOf(s) + "\n" + (multiply("=", 60)));
    }

    private void logTokens(String text) {
        final GroovyLangLexer lexer = new GroovyLangLexer(new ANTLRInputStream(text));
        log.fine(multiply("=", 60) + "\n" + text + "\n" + multiply("=", 60));
        log.fine("\nLexer TOKENS:\n\t" + DefaultGroovyMethods.join(collect(lexer.getAllTokens(), new Closure<String>(this, this) {
            public String doCall(Token it) { return String.valueOf(it.getLine()) + ", " + String.valueOf(it.getStartIndex()) + ":" + String.valueOf(it.getStopIndex()) + " " + GroovyLangLexer.tokenNames[it.getType()] + " " + it.getText(); }
        }), "\n\t") + multiply("=", 60));
    }

    private CompilationFailedException createParsingFailedException(Throwable cause) {
        return new CompilationFailedException(CompilePhase.PARSING.getPhaseNumber(), this.sourceUnit, cause);
    }

    private String createExceptionMessage(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            t.printStackTrace(pw);
        } finally {
            pw.close();
        }

        return sw.toString();
    }

    private String genAnonymousClassName(String outerClassName) {
        return outerClassName + "$" + this.genAnonymousClassSeq(outerClassName);
    }

    private Integer genAnonymousClassSeq(String outerClassName) {
        Matcher matcher = Pattern.compile("\\$\\d").matcher(outerClassName);

        String outestNonAnonymousClassName;
        if (matcher.find()) {
            outestNonAnonymousClassName = outerClassName.substring(0, matcher.start());
        } else {
            outestNonAnonymousClassName = outerClassName;
        }

        Integer seq = anonymousClassToSeqMap.get(outestNonAnonymousClassName);

        if (null == seq) {
            seq = 0;
        }

        anonymousClassToSeqMap.put(outestNonAnonymousClassName, ++seq);

        return seq;
    }

    private void addClasses() {
        if (this.classes.size() > 1) {
            Collections.sort(this.classes, new Comparator<ClassNode>() {
                private ClassNode findOutestClass(ClassNode cn) {
                    ClassNode outerClass = cn.getOuterClass();

                    if (null == outerClass) {
                        return cn;
                    }

                    return findOutestClass(outerClass);
                }

                private static final String SEPARATOR = "@";

                private String convert(ClassNode cn) {
                    return DefaultGroovyMethods.padLeft(findOutestClass(cn).getLineNumber() + "", 10, "0") + SEPARATOR
                            + (cn.isInterface() || cn.isEnum() ? "1" : "0") + SEPARATOR
                            + cn.getName();
                }

                @Override
                public int compare(ClassNode cn1, ClassNode cn2) {
                    return convert(cn1).compareTo(convert(cn2));
                }
            });
        }

        for (ClassNode cn : this.classes) {
            moduleNode.addClass(cn);
        }
    }

    private final GroovyLangLexer lexer;
    private final GroovyLangParser parser;
    private final ModuleNode moduleNode;
    private final SourceUnit sourceUnit;
    private final ClassLoader classLoader;
    private final List<ClassNode> classes = new LinkedList<ClassNode>();
    private final Stack<ClassNode> classNodeStack = new Stack<ClassNode>();
    private final Stack<List<InnerClassNode>> innerClassesDefinedInMethodStack = new Stack<List<InnerClassNode>>();
    private final Map<String, Integer> anonymousClassToSeqMap = new HashMap<String, Integer>();
    private final Logger log = Logger.getLogger(ASTBuilder.class.getName());
}
