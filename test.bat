@echo off
call gradlew clean
call gradlew -Dfile.encoding=UTF-8 :test --tests org.codehaus.groovy.parser.antlr4.MainTest
