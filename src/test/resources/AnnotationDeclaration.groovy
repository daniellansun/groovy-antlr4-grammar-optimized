@Annotated package org.codehaus.groovy.parser.antlr4;

import groovy.transform.Canonical
import groovy.util.logging.Log
import org.codehaus.groovy.transform.GroovyASTTransformationClass

@Annotated import java.beans.Transient
import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Canonical(
    includes = ['a', 'b'], excludes = ['c']
)
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@GroovyASTTransformationClass('Lulz')
@interface FunnyAnnotation {
     /* This is a comment
     */
    String name() default ""

    /**
     * This has a default, too
     */
    boolean synchronize() default false
}
