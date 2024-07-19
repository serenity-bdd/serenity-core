package net.serenitybdd.screenplay.ensure

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.regex.Pattern

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithStrings {

    @Nested
    inner class WeCanCheckFor {

        @Nested
        inner class EmptyValues {

            @Test
            fun `when the string is empty`() {
                shouldPassWhenChecking(that("").isEmpty())
            }

            @Test
            fun `when the string is not empty`() {
                shouldFailWithMessage("""|Expected: a value that is empty
                                         |Actual:   "Foo""""
                        .trimMargin())
                        .whenChecking(that("Foo").isEmpty())
            }

        }

        @Nested
        inner class NullOrEmptyValues {

            @Test
            fun `when the string is empty`() {
                shouldPassWhenChecking(that("").isNullOrEmpty())
            }

            @Test
            fun `when the string is null`() {
                val nullValue : String? = null;
                shouldPassWhenChecking(that(nullValue).isNullOrEmpty())
            }

            @Test
            fun `when the string is neither empty nor null`() {
                shouldFailWithMessage("""|Expected: a value that is null or empty
                                         |Actual:   "Foo""""
                        .trimMargin())
                        .whenChecking(that("Foo").isNullOrEmpty())
            }

            @Test
            fun `when the string is unexpectedly neither empty nor null`() {
                shouldFailWithMessage("""|Expected: a value that is not null or empty
                                         |Actual:   """""
                        .trimMargin())
                        .whenChecking(that("").not().isNullOrEmpty())
            }
        }

        @Nested
        inner class Contains {

            @Test
            fun `when the string contains the text`() {
                shouldPassWhenChecking(that("red green blue").contains("blue"))
            }

            @Test
            fun `when the value is null`() {
                val nullValue : String? = null;
                shouldFailWhenChecking(that(nullValue).contains("blue"))
            }

            @Test
            fun `when the value is null for obscure edge cases`() {
                val nullValue : String? = null;
                shouldFailWhenChecking(that(nullValue).contains("null"))
            }

            @Test
            fun `when the expected list is empty`() {
                shouldFailWhenChecking(that("red green blue").contains())
            }

            @Test
            fun `when we are looking for several strings`() {
                shouldPassWhenChecking(that("red green blue").contains("blue", "red"))
            }

            @Test
            fun `when the string does not contain the text`() {
                shouldFailWithMessage("""|Expected: a value that contains: <[yellow]>
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").contains("yellow"))
            }
            @Test
            fun `when the string is expected not to contain the text`() {
                shouldFailWithMessage("""|Expected: a value that does not contain: <[red]>
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").not().contains("red"))
            }
        }

        @Nested
        inner class DoesNotContain {

            @Test
            fun `when the string does not contain the text`() {
                shouldPassWhenChecking(that("red green blue").doesNotContain("yellow"))
            }

            @Test
            fun `when the value is null`() {
                val nullValue : String? = null
                shouldPassWhenChecking(that(nullValue).doesNotContain("blue"))
            }

            @Test
            fun `when the expected list is empty`() {
                shouldPassWhenChecking(that("").doesNotContain("blue"))
            }

            @Test
            fun `when we are looking for several strings`() {
                shouldPassWhenChecking(that("white yellow purple").doesNotContain("blue", "red"))
            }

            @Test
            fun `when the text contains the string`() {
                shouldFailWithMessage("""|Expected: a value that does not contain: <[red]>
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").doesNotContain("red"))
            }
        }

        @Nested
        inner class ContainsIgnoringCase {

            @Test
            fun `when the string contains the text`() {
                shouldPassWhenChecking(that("Red Green Blue").containsIgnoringCase("blue"))
            }

            @Test
            fun `when the value is null`() {
                val nullValue : String? = null;
                shouldFailWhenChecking(that(nullValue).containsIgnoringCase("blue"))
            }

            @Test
            fun `when the expected list is empty`() {
                val nullValue : String? = null;
                shouldFailWhenChecking(that(nullValue).containsIgnoringCase())
            }

            @Test
            fun `when we are looking for several strings`() {
                shouldPassWhenChecking(that("red green blue").containsIgnoringCase("Blue", "RED"))
            }

            @Test
            fun `when the string does not contain the text`() {
                shouldFailWithMessage("""|Expected: a value that contains (ignoring case): <[yellow]>
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").containsIgnoringCase("yellow"))
            }
        }
        @Nested
        inner class EqualsIgnoringCase {

            @Test
            fun `when the strings match`() {
                shouldPassWhenChecking(that("Red Bus").isEqualToIgnoringCase("rED buS"))
            }

            @Test
            fun `when the strings don't match`() {
                shouldFailWithMessage("""|Expected: a value that is equal to (ignoring case): <"Bar">
                                         |Actual:   <"foo">"""
                        .trimMargin())
                        .whenChecking(that("foo").isEqualToIgnoringCase("Bar"))
            }
        }

        @Nested
        inner class UpperCase {

            @Test
            fun `when the string is in upper case`() {
                shouldPassWhenChecking(that("RED BUS").isInUppercase())
            }

            @Test
            fun `when the string is not in upper case`() {
                shouldFailWithMessage("""|Expected: a value that is uppercase
                                         |Actual:   "foo""""
                        .trimMargin())
                        .whenChecking(that("foo").isInUppercase())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWithMessage("""|Expected: a value that is uppercase
                                         |Actual:   """""
                        .trimMargin())
                        .whenChecking(that("").isInUppercase())
            }
        }

        @Nested
        inner class LowerCase {

            @Test
            fun `when the string is in lower case`() {
                shouldPassWhenChecking(that("foo bar").isInLowercase())
            }

            @Test
            fun `when the string is not in lower case`() {
                shouldFailWithMessage("""|Expected: a value that is lowercase
                                         |Actual:   "FOO""""
                        .trimMargin())
                        .whenChecking(that("FOO").isInLowercase())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWithMessage("""|Expected: a value that is lowercase
                                         |Actual:   """""
                        .trimMargin())
                        .whenChecking(that("").isInLowercase())
            }
        }

        @Nested
        inner class BlankStrings {

            @Test
            fun `when the string is blank`() {
                shouldPassWhenChecking(that("").isBlank())
            }

            @Test
            fun `when the string has only white spaces`() {
                shouldPassWhenChecking(that("    ").isBlank())
            }

            @Test
            fun `when the string should not be blank`() {
                shouldPassWhenChecking(that("NOT BLANK").isNotBlank())
            }

            @Test
            fun `when the string should be blank but is not`() {
                shouldFailWhenChecking(that("").isNotBlank())
            }

            @Test
            fun `when the string is not blank`() {
                shouldFailWithMessage("""|Expected: a value that is blank
                                         |Actual:   "FOO""""
                        .trimMargin())
                        .whenChecking(that("FOO").isBlank())
            }
        }


        @Nested
        inner class EmptyStrings {

            @Test
            fun `when the string is empty`() {
                shouldPassWhenChecking(that("").isEmpty())
            }

            @Test
            fun `when the string has only white spaces`() {
                shouldFailWithMessage("""|Expected: a value that is empty
                                         |Actual:   "   """"
                        .trimMargin())
                        .whenChecking(that("   ").isEmpty())
            }

            @Test
            fun `when the string should not be empty`() {
                shouldPassWhenChecking(that("NOT EMPTY").isNotEmpty())
            }

            @Test
            fun `when the string is empty but should not be empty`() {
                shouldFailWhenChecking(that("").isNotEmpty())
            }


            @Test
            fun `when the string is not empty`() {
                shouldFailWithMessage("""|Expected: a value that is empty
                                         |Actual:   "FOO""""
                        .trimMargin())
                        .whenChecking(that("FOO").isEmpty())
            }
        }


        @Nested
        inner class IsASubstring {

            @Test
            fun `when the string is a substring of the text`() {
                shouldPassWhenChecking(that("green").isSubstringOf("red green blue"))
            }

            @Test
            fun `when the string is equal to the text`() {
                shouldPassWhenChecking(that("red green blue").isSubstringOf("red green blue"))
            }

            @Test
            fun `when the string is not a substring of the text`() {
                shouldFailWithMessage("""|Expected: a value that is a substring of: <"red green blue">
                                         |Actual:   <"yellow">"""
                        .trimMargin())
                        .whenChecking(that("yellow").isSubstringOf("red green blue"))
            }
        }

        @Nested
        inner class StartsWith {

            @Test
            fun `when the string starts with text`() {
                shouldPassWhenChecking(that("red green blue").startsWith("red"))
            }

            @Test
            fun `when the string does not start with text`() {
                shouldFailWithMessage("""|Expected: a value that starts with: <"yellow">
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").startsWith("yellow"))
            }
        }

        @Nested
        inner class EndsWith {

            @Test
            fun `when the string ends with text`() {
                shouldPassWhenChecking(that("red green blue").endsWith("blue"))
            }

            @Test
            fun `when the string does not ends with text`() {
                shouldFailWithMessage("""|Expected: a value that ends with: <"yellow">
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").endsWith("yellow"))
            }
        }

        @Nested
        inner class MatchesARegEx {

            @Test
            fun `when the string matches`() {
                shouldPassWhenChecking(that("red green blue").matches("red.*"))
            }

            @Test
            fun `when the string does not match`() {
                shouldFailWithMessage("""|Expected: a value that is a match for: <"yellow.*">
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").matches("yellow.*"))
            }
        }

        @Nested
        inner class MatchesAPattern {

            @Test
            fun `when the string matches`() {
                shouldPassWhenChecking(that("red green blue").matches(Pattern.compile("red.*")))
            }

            @Test
            fun `when the string does not match`() {
                shouldFailWithMessage("""|Expected: a value that is a match for: <yellow.*>
                                         |Actual:   <"red green blue">"""
                        .trimMargin())
                        .whenChecking(that("red green blue").matches(Pattern.compile("yellow.*")))
            }
        }

        @Nested
        inner class ContainsWhitespaces {

            @Test
            fun `when the string has only white spaces`() {
                shouldPassWhenChecking(that("    ").containsWhitespaces())
            }

            @Test
            fun `when the string has some white spaces`() {
                shouldPassWhenChecking(that("red green").containsWhitespaces())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWhenChecking(that("").containsWhitespaces())
            }

            @Test
            fun `when the string has no whitespaces`() {
                shouldFailWithMessage("""|Expected: a value that contains whitespaces
                                         |Actual:   "FOO""""
                        .trimMargin())
                        .whenChecking(that("FOO").containsWhitespaces())
            }

            @Test
            fun `when the string has some whitespaces`() {
                shouldFailWithMessage("""|Expected: a value that contains no whitespaces
                                         |Actual:   "A B""""
                        .trimMargin())
                        .whenChecking(that("A B").not().containsWhitespaces())
            }
        }


        @Nested
        inner class ContainsOnlyWhitespaces {

            @Test
            fun `when the string has only white spaces`() {
                shouldPassWhenChecking(that("    ").containsOnlyWhitespaces())
            }

            @Test
            fun `when the string has some white spaces`() {
                shouldFailWhenChecking(that("red green").containsOnlyWhitespaces())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWhenChecking(that("").containsOnlyWhitespaces())
            }

            @Test
            fun `when the string has no whitespaces`() {
                shouldFailWithMessage("""|Expected: a value that contains only whitespaces
                                         |Actual:   "FOO""""
                        .trimMargin())
                        .whenChecking(that("FOO").containsOnlyWhitespaces())
            }

            @Test
            fun `when the string does not contain only whitespaces`() {
                shouldFailWithMessage("""|Expected: a value that does not contain only whitespaces
                                         |Actual:   "   """"
                        .trimMargin())
                        .whenChecking(that("   ").not().containsOnlyWhitespaces())
            }

        }

        @Nested
        inner class DoesNotContainAnyWhitespaces {

            @Test
            fun `when the string has no whitespace`() {
                shouldPassWhenChecking(that("foo").doesNotContainAnyWhitespaces())
            }

            @Test
            fun `when the string is empty`() {
                shouldPassWhenChecking(that("").doesNotContainAnyWhitespaces())
            }

            @Test
            fun `when the string has only white spaces`() {
                shouldFailWhenChecking(that("    ").doesNotContainAnyWhitespaces())
            }

            @Test
            fun `when the string has some white spaces`() {
                shouldFailWhenChecking(that("red green").doesNotContainAnyWhitespaces())
            }

            @Test
            fun `when the string has no whitespaces`() {
                shouldFailWithMessage("""|Expected: a value that contains no whitespace
                                         |Actual:   "foo bar""""
                        .trimMargin())
                        .whenChecking(that("foo bar").doesNotContainAnyWhitespaces())
            }
        }

        @Nested
        inner class ContainsOnlyDigits {

            @Test
            fun `when the string has only digits`() {
                shouldPassWhenChecking(that("123").containsOnlyDigits())
            }

            @Test
            fun `when the string has some white spaces`() {
                shouldFailWhenChecking(that("123 34").containsOnlyDigits())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWhenChecking(that("").containsOnlyDigits())
            }

            @Test
            fun `when the string has no digits`() {
                shouldFailWithMessage("""|Expected: a value that contains only digits
                                         |Actual:   "FOO""""
                        .trimMargin())
                        .whenChecking(that("FOO").containsOnlyDigits())
            }
        }


        @Nested
        inner class ContainsOnlyLetters {

            @Test
            fun `when the string has only letters`() {
                shouldPassWhenChecking(that("abc").containsOnlyLetters())
            }

            @Test
            fun `when the string has some white spaces`() {
                shouldFailWhenChecking(that("abc de").containsOnlyLetters())
            }

            @Test
            fun `when the string has punctuation`() {
                shouldFailWhenChecking(that("abc$").containsOnlyLetters())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWhenChecking(that("").containsOnlyLetters())
            }

            @Test
            fun `when the string has no letters`() {
                shouldFailWithMessage("""|Expected: a value that contains only letters
                                         |Actual:   "123""""
                        .trimMargin())
                        .whenChecking(that("123").containsOnlyLetters())
            }
        }

        @Nested
        inner class ContainsOnlyLettersOrDigits {

            @Test
            fun `when the string has only letters`() {
                shouldPassWhenChecking(that("abc").containsOnlyLettersOrDigits())
            }

            @Test
            fun `when the string has only digits`() {
                shouldPassWhenChecking(that("123").containsOnlyLettersOrDigits())
            }

            @Test
            fun `when the string has letters and digits`() {
                shouldPassWhenChecking(that("123ABC").containsOnlyLettersOrDigits())
            }

            @Test
            fun `when the string has some white spaces`() {
                shouldFailWhenChecking(that("abc 123").containsOnlyLettersOrDigits())
            }

            @Test
            fun `when the string has punctuation`() {
                shouldFailWhenChecking(that("abc$123").containsOnlyLettersOrDigits())
            }

            @Test
            fun `when the string is empty`() {
                shouldFailWhenChecking(that("").containsOnlyLettersOrDigits())
            }

            @Test
            fun `when the string has no letters`() {
                shouldFailWithMessage("""|Expected: a value that contains only letters or digits
                                         |Actual:   "$$$""""
                        .trimMargin())
                        .whenChecking(that("$$$").containsOnlyLettersOrDigits())
            }
        }

        @Nested
        inner class HasSize {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSize(3))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that has size: <3>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSize(3))
            }

            @Test
            fun `when the string does not have the expected size (negated)`() {
                shouldFailWithMessage("""|Expected: a value that does not have size: <5>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").not().hasSize(5))
            }
        }

        @Nested
        inner class HasSizeGreaterThan {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSizeGreaterThan(2))
            }

            @Test
            fun `when the string has a size too low`() {
                shouldFailWhenChecking(that("red").hasSizeGreaterThan(3))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that has size greater than: <10>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSizeGreaterThan(10))
            }
        }


        @Nested
        inner class HasSizeGreaterThanOrEqualTo {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSizeGreaterThanOrEqualTo(2))
                shouldPassWhenChecking(that("red").hasSizeGreaterThanOrEqualTo(3))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that has size greater than or equal to: <10>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSizeGreaterThanOrEqualTo(10))
            }

            @Test
            fun `when the string does not have the expected size (negated)`() {
                shouldFailWithMessage("""|Expected: a value that has size less than: <3>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").not().hasSizeGreaterThanOrEqualTo(3))
            }

        }


        @Nested
        inner class HasSizeLessThan {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSizeLessThan(5))
            }


            @Test
            fun `when the string has a size too low`() {
                shouldFailWhenChecking(that("red").hasSizeLessThan(3))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that has size less than: <3>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSizeLessThan(3))
            }

            @Test
            fun `when the string does not have the expected size (negated)`() {
                shouldFailWithMessage("""|Expected: a value that has size greater than or equal to: <4>
                                         |Actual:   <"red">"""
                        .trimMargin())
                        .whenChecking(that("red").not().hasSizeLessThan(4))
            }
        }


        @Nested
        inner class HasSizeLessThanOrEqualTo {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSizeLessThanOrEqualTo(5))
                shouldPassWhenChecking(that("red").hasSizeLessThanOrEqualTo(3))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that has size less than or equal to: <3>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSizeLessThanOrEqualTo(3))
            }

            @Test
            fun `when the string does not have the expected size (negated)`() {
                shouldFailWithMessage("""|Expected: a value that has size greater than: <3>
                                         |Actual:   <"red">"""
                        .trimMargin())
                        .whenChecking(that("red").not().hasSizeLessThanOrEqualTo(3))
            }
        }


        @Nested
        inner class HasSizeBetween {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSizeBetween(2,4))
            }


            @Test
            fun `when the string has a size too low`() {
                shouldFailWhenChecking(that("red").hasSizeBetween(5,6))
                shouldFailWhenChecking(that("red").hasSizeBetween(0,1))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that is of size between 1 and 4
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSizeBetween(1,4))
            }

            @Test
            fun `when the string does not have the expected size (negated form)`() {
                shouldFailWithMessage("""|Expected: a value that is not of size between 1 and 4
                                         |Actual:   <"red">"""
                        .trimMargin())
                        .whenChecking(that("red").not().hasSizeBetween(1,4))
            }

        }


        @Nested
        inner class HasSameSizeAs {

            @Test
            fun `when the string has the expected size`() {
                shouldPassWhenChecking(that("red").hasSameSizeAs("toe"))
            }


            @Test
            fun `when the string has a size too low`() {
                shouldFailWhenChecking(that("red").hasSameSizeAs("to"))
                shouldFailWhenChecking(that("red").hasSizeBetween(0,1))
            }

            @Test
            fun `when the string does not have the expected size`() {
                shouldFailWithMessage("""|Expected: a value that has the same size as: <"red">
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasSameSizeAs("red"))
            }

            @Test
            fun `when the string does not have the expected size (negated)`() {
                shouldFailWithMessage("""|Expected: a value that does not have the same size as: <"red">
                                         |Actual:   <"RED">"""
                        .trimMargin())
                        .whenChecking(that("RED").not().hasSameSizeAs("red"))
            }
        }

        @Nested
        inner class HasLineCount {

            @Test
            fun `when the string has one line`() {
                shouldPassWhenChecking(that("red").hasLineCount(1))
            }

            @Test
            fun `when the string has several lines`() {
                shouldPassWhenChecking(that("""line one
                    |line two
                    |line 3
                """.trimMargin()).hasLineCount(3))
            }

            @Test
            fun `when the string does not have the expected line count`() {
                shouldFailWithMessage("""|Expected: a value that has a line count of: <3>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").hasLineCount(3))
            }

            @Test
            fun `when the string does not have the expected line count (negated)`() {
                shouldFailWithMessage("""|Expected: a value that does not have a line count of: <1>
                                         |Actual:   <"green">"""
                        .trimMargin())
                        .whenChecking(that("green").not().hasLineCount(1))
            }

        }
    }
}
