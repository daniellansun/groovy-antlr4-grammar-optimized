@Annotated package org.codehaus.groovy.parser.antlr4;

import groovy.transform.Canonical
import groovy.util.logging.Log

@Annotated import java.beans.Transient

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Canonical(
    includes = ['a', 'b'], excludes = ['c']
) class AnnotatedClass {}

@Log class ClassMarkerAnnotation {}

@      Log class ClassMarkerAnnotationWithSpaces {}


@groovy.transform.ToString class ClassMarkerAnnotationFull {}

@Category(a) class ClassSingleValueAnnotation { }
@Category(a.b.c.d) class PathValueAnnotation { }

@Ann(
    @Retention(value = RetentionPolicy.CLASS)
) class ClassNestedAnnotation {}

class A {
    @Deprecated def markerField

    @Transient(1) @Log private <T> T singleValueMethod(@Deprecated int a) {}
}

@Field a = 1;
@Field final a = 1;
@Field final @Field a = 1;
@Field final int a = 1;
@Field final @Field int a = 1;
