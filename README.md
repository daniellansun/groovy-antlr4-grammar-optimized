To implement Groovy grammar with the optimized antlr4(com.tunnelvisionlabs:antlr4)

// 1) clean the project
```
gradlew clean
```

// 2) generate codes with antlr4 and run the test cases
```
gradlew -Dfile.encoding=UTF-8 :test --tests org.codehaus.groovy.parser.antlr4.MainTest
```

// generate codes with antlr4 separately
```
gradlew antlr4
```
