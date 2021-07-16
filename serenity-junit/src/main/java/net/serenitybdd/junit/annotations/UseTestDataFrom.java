package net.serenitybdd.junit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lets you to perform data-driven tests using CSV file in the specified location with the first row acting as header.
 * Default separator is comma, which could be overridden by specifying the <b>separator</b> attribute.<br>
 * You can specify multiple file paths separated by path separators – colon, semi-colon or comma.<br>
 * You can also configure an arbitrary directory using system property <b>serenity.data.dir</b> and then refer
 * to it as <b><i>$DATADIR</i></b> variable in the annotation.<br>
 *  Example usage:
 *  <b>@UseTestDataFrom(value = "test-data/simple-data.csv,$DATADIR/simple-data.csv", separator=";")</b>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UseTestDataFrom {
   String value();
   char separator() default ',';
}
