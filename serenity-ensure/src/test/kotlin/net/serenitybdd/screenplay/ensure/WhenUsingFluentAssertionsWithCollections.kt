package net.serenitybdd.screenplay.ensure

import org.assertj.core.util.Lists.emptyList
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenUsingFluentAssertionsWithCollections {


    @Nested
    inner class WeCanCheckFor {

        @Nested
        inner class EqualLists {

            val someColors = listOf("red", "green", "blue");
            val someEquivalentColors = listOf("red", "green", "blue");
            val someDifferentColors = listOf("yellow", "cyan", "magenta");

            val someColorsWithIdenticalLetterCounts = listOf("red", "brown", "cyan");

            @Test
            fun `when two lists contain the same elements`() {
                shouldPassWhenChecking(that(someColors).isEqualTo(someEquivalentColors))
            }

            @Test
            fun `when two lists contain the same elements using a custom comparator`() {
                val byLength = Comparator.comparingInt<String> { it.length }

                shouldPassWhenChecking(that(someColors).usingComparator(byLength).isEqualTo(someColorsWithIdenticalLetterCounts))
            }

            @Test
            fun `when two lists contain different elements`() {
                shouldPassWhenChecking(that(someColors).isNotEqualTo(someDifferentColors))
                shouldFailWhenChecking(that(someColors).isNotEqualTo(someEquivalentColors))
            }


            @Test
            fun `when two lists contain different elements but should contain the same`() {
                shouldFailWithMessage("""|Expecting a collection that is equal to: <[yellow, cyan, magenta]>
                                         |But got................................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(someColors).isEqualTo(someDifferentColors))
            }
        }

        @Nested
        inner class EmptyLists {

            val colors = listOf("red", "green", "blue");
            val noColors = Collections.emptyList<String>();

            @Test
            fun `when a list is empty`() {
                shouldPassWhenChecking(that(noColors).isEmpty())
            }

            @Test
            fun `when a list is null or empty`() {
                val nullList : List<String>? = null
                val emptyList : List<String> = Collections.emptyList()

                shouldPassWhenChecking(that(nullList).isNullOrEmpty())
                shouldPassWhenChecking(that(emptyList).isNullOrEmpty())
                shouldFailWhenChecking(that(colors).isNullOrEmpty())
            }

            @Test
            fun `when a list is not empty`() {
                shouldPassWhenChecking(that(colors).not().isEmpty())
                shouldPassWhenChecking(that(colors).isNotEmpty())
            }

            @Test
            fun `when a list is not empty but should be`() {
                shouldFailWithMessage("""|Expecting a collection that is empty
                                         |But got: [red, green, blue]"""
                        .trimMargin())
                        .whenChecking(that(colors).isEmpty())
            }

        }

        @Nested
        inner class AListOfSize {

            val colors = listOf("red", "green", "blue");
            val otherColors = listOf("red", "cyan", "pink");
            val fewerColors = listOf("red", "cyan");
            val moreColors = listOf("red", "green", "blue","pink");

            @Test
            fun `equal to a given value`() {
                shouldPassWhenChecking(that(colors).hasSize(3))
                shouldFailWhenChecking(that(colors).hasSize(2))
            }

            @Test
            fun `greater than a given value`() {
                shouldPassWhenChecking(that(colors).hasSizeGreaterThan(2))
            }

            @Test
            fun `greater than a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that is of size greater than: <3>
                                         |But got............................................: <"a list with 3 elements containing [red, green, blue]">"""
                        .trimMargin())
                        .whenChecking(that(colors).hasSizeGreaterThan(3))
            }

            @Test
            fun `not greater than a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that is not of size greater than: <3>
                                         |But got................................................: <"a list with 4 elements containing [red, green, blue, pink]">"""
                        .trimMargin())
                        .whenChecking(that(moreColors).not().hasSizeGreaterThan(3))
            }

            @Test
            fun `greater than or equal to a given value`() {
                shouldPassWhenChecking(that(colors).hasSizeGreaterThanOrEqualTo(2))
                shouldPassWhenChecking(that(colors).hasSizeGreaterThanOrEqualTo(3))
            }

            @Test
            fun `greater than or equal to a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that is of size greater than or equal to: <4>
                                         |But got........................................................: <"a list with 3 elements containing [red, green, blue]">"""
                        .trimMargin())
                        .whenChecking(that(colors).hasSizeGreaterThanOrEqualTo(4))
            }

            @Test
            fun `not greater than or equal to a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that is not of size greater than or equal to: <4>
                                         |But got............................................................: <"a list with 4 elements containing [red, green, blue, pink]">"""
                        .trimMargin())
                        .whenChecking(that(moreColors).not().hasSizeGreaterThanOrEqualTo(4))
            }

            @Test
            fun `less than a given value`() {
                shouldPassWhenChecking(that(colors).hasSizeLessThan(4))
                shouldFailWhenChecking(that(colors).hasSizeLessThan(3))
            }

            @Test
            fun `less than or equal to a given value`() {
                shouldPassWhenChecking(that(colors).hasSizeLessThanOrEqualTo(4))
                shouldPassWhenChecking(that(colors).hasSizeLessThanOrEqualTo(3))
                shouldFailWhenChecking(that(colors).hasSizeLessThanOrEqualTo(2))
            }

            @Test
            fun `between two values`() {
                shouldPassWhenChecking(that(colors).hasSizeBetween(2, 4))
                shouldPassWhenChecking(that(colors).hasSizeBetween(3, 4))
                shouldFailWhenChecking(that(colors).hasSizeBetween(5, 6))
            }

            @Test
            fun `that is the same size as another list`() {
                shouldPassWhenChecking(that(colors).hasSameSizeAs(otherColors))
                shouldFailWhenChecking(that(colors).hasSameSizeAs(fewerColors))
            }


            @Test
            fun `when the size is not what it should be`() {
                shouldFailWithMessage("""|Expecting a collection that is of size: <10>
                                         |But got...............................: <"a list with 3 elements containing [red, green, blue]">"""
                        .trimMargin())
                        .whenChecking(that(colors).hasSize(10))
            }

            @Test
            fun `when the size is not what it should be and there is only one element`() {
                shouldFailWithMessage("""|Expecting a collection that is of size greater than: <10>
                                         |But got............................................: <"a list with 1 element containing [red]">"""
                        .trimMargin())
                        .whenChecking(that(listOf("red")).hasSizeGreaterThan(10))
            }

        }

        @Nested
        inner class AListThatContains {

            val colors = listOf("red", "green", "blue");

            @Test
            fun `a given value`() {
                shouldPassWhenChecking(that(colors).contains("red"))
                shouldFailWhenChecking(that(colors).contains("purple"))
            }

            @Test
            fun `a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that contains: <[purple]>
                                         |But got.............................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(colors).contains("purple"))
            }

            @Test
            fun `several values`() {
                shouldPassWhenChecking(that(colors).contains("red", "blue"))
                shouldFailWhenChecking(that(colors).contains("red", "purple"))
            }

            @Test
            fun `values in a list`() {
                shouldPassWhenChecking(that(colors).containsElementsFrom(listOf("red", "blue")))
            }

            @Test
            fun `any values in a list`() {
                shouldPassWhenChecking(that(colors).containsAnyElementsOf(listOf("red", "pink")))
                shouldFailWhenChecking(that(colors).containsAnyElementsOf(listOf("purple", "pink")))
            }

            @Test
            fun `does not contain a given value`() {
                shouldPassWhenChecking(that(colors).doesNotContain("purple"))
            }

            @Test
            fun `does not contain a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that does not contain: <[red]>
                                         |But got.....................................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(colors).not().contains("red"))
            }

            @Test
            fun `does not contain any of several given values`() {
                shouldPassWhenChecking(that(colors).doesNotContain("purple", "cyan"))
                shouldPassWhenChecking(that(colors).doesNotContain("red", "purple"))
                shouldFailWhenChecking(that(colors).doesNotContain("red", "blue"))
            }

            @Test
            fun `does not contain any of values from a list`() {
                shouldPassWhenChecking(that(colors).doesNotContainElementsFrom(listOf("purple", "cyan")))
            }

        }

        @Nested
        inner class AListThatContainsOnly {

            val colors = listOf("red", "green", "blue", "red", "blue");
            val reds = listOf("red", "red", "red");

            @Test
            fun `a given value`() {
                shouldPassWhenChecking(that(reds).containsOnly("red"))
                shouldFailWhenChecking(that(colors).containsOnly("red"))
            }

            @Test
            fun `a given value (negative case)`() {
                shouldFailWithMessage("""|Expecting a collection that contains only: <[red]>
                                         |But got..................................: <[red, green, blue, red, blue]>"""
                .trimMargin()).whenChecking(that(colors).containsOnly("red"))
            }

            @Test
            fun `several values`() {
                shouldPassWhenChecking(that(colors).containsOnly("red", "blue", "green"))
                shouldFailWhenChecking(that(colors).containsOnly("red", "purple"))
            }

            @Test
            fun `several values in a list`() {
                val redGreenAndBlue = listOf("red", "green", "blue");
                val redAndPurple = listOf("red", "blue");

                shouldPassWhenChecking(that(colors).containsOnlyElementsFrom(redGreenAndBlue))
                shouldFailWhenChecking(that(colors).containsOnlyElementsFrom(redAndPurple))
            }
        }

        @Nested
        inner class AListThatContainsExactly {

            val colors = listOf("red", "green", "blue");
            val expectedColorsInTheSameOrder = listOf("red", "green", "blue");
            val expectedColorsInADifferentOrder = listOf("green", "blue", "red");
            val differentColors = listOf("red", "green", "yellow");

            @Test
            fun `the same elements in the same order`() {
                shouldPassWhenChecking(that(colors).containsExactly("red", "green", "blue"))
            }

            @Test
            fun `the same elements in a different order`() {
                shouldFailWithMessage("""|Expecting a collection that contains exactly: <[green, blue, red]>
                                         |But got.....................................: <[red, green, blue]>"""
                        .trimMargin()).whenChecking(that(colors).containsExactly("green", "blue", "red"))
            }

            @Test
            fun `different elements`() {
                shouldFailWhenChecking(that(colors).containsExactly("red", "green", "yellow"))
            }

            @Test
            fun `the same elements from a list`() {
                shouldPassWhenChecking(that(colors).containsExactlyElementsFrom(expectedColorsInTheSameOrder))
                shouldFailWhenChecking(that(colors).containsExactlyElementsFrom(expectedColorsInADifferentOrder))
            }
        }


        @Nested
        inner class AListThatContainsAnyOf {

            val colors = listOf("red", "green", "blue");

            @Test
            fun `where the list contains some of the elements`() {
                shouldPassWhenChecking(that(colors).containsAnyOf("red", "blue","pink"))
            }

            @Test
            fun `where the list contains none of the elements`() {
                shouldFailWithMessage("""|Expecting a collection that contains any of: <[pink, orange, cyan]>
                                         |But got....................................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(colors).containsAnyOf("pink", "orange","cyan"))
            }

        }

        @Nested
        inner class AListThatContainsExactlyInAnyOrder {

            val colors = listOf("red", "green", "blue");
            val expectedColorsInTheSameOrder = listOf("red", "green", "blue");
            val expectedColorsInADifferentOrder = listOf("green", "blue", "red");
            val differentColors = listOf("red", "green", "yellow");

            @Test
            fun `the same elements in the same order`() {
                shouldPassWhenChecking(that(colors).containsExactlyInAnyOrder("red", "green", "blue"))
            }

            @Test
            fun `the same elements in a different order`() {
                shouldPassWhenChecking(that(colors).containsExactlyInAnyOrder("green", "blue", "red"))
            }

            @Test
            fun `different elements`() {
                shouldFailWithMessage("""|Expecting a value that contains exactly in any order: <[red, green, yellow]>
                                         |But got.............................................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(colors).containsExactlyInAnyOrder("red", "green", "yellow"))
            }

            @Test
            fun `the same elements from a list`() {
                shouldPassWhenChecking(that(colors).containsExactlyInAnyOrderElementsFrom(expectedColorsInTheSameOrder))
            }

            @Test
            fun `the same elements in a different order from a list`() {
                shouldPassWhenChecking(that(colors).containsExactlyInAnyOrderElementsFrom(expectedColorsInADifferentOrder))
            }

            @Test
            fun `different elements from a list`() {
                shouldFailWhenChecking(that(colors).containsExactlyInAnyOrderElementsFrom(differentColors))
            }

        }


        @Nested
        inner class AListThatIsASubsetOf {

            val redGreenBlue = listOf("red", "green", "blue", "orange", "pink");
            val moreColors = listOf("red", "green", "blue", "orange", "pink");
            val notColors = listOf("cat", "dog");

            @Test
            fun `the same list`() {
                shouldPassWhenChecking(that(listOf("red","green","blue")).isASubsetOf(redGreenBlue))
            }

            @Test
            fun `a larger list`() {
                shouldPassWhenChecking(that(listOf("red","green","blue")).isASubsetOf(moreColors))
            }

            @Test
            fun `a list of objects`() {
                shouldPassWhenChecking(that(listOf("red","green","blue")).isASubsetOf("red","green","blue","orange"))
            }

            @Test
            fun `a different list`() {
                shouldFailWithMessage("""|Expecting a value that is a subset of: <[cat, dog]>
                                         |But got..............................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(listOf("red","green","blue")).isASubsetOf(notColors))
            }
        }

        @Nested
        inner class AListThatHasDuplicates {
            @Test
            fun `when duplicates not are present`() {
                shouldPassWhenChecking(that(listOf("red", "green", "blue")).doesNotHaveDuplicates())
            }

            @Test
            fun `when duplicates are present`() {
                shouldFailWithMessage("""|Expecting a collection that contains no duplicates
                                         |But got: [red, green, blue, blue]"""
                        .trimMargin())
                        .whenChecking(that(listOf("red", "green", "blue", "blue")).doesNotHaveDuplicates())
            }
        }

        @Nested
        inner class AListThatStartsWith {

            @Test
            fun `when the values match`() {
                shouldPassWhenChecking(that(listOf("red","green","blue")).startsWith("red","green"))
                shouldPassWhenChecking(that(listOf("red","green","blue")).startsWithElementsFrom(listOf("red","green")))
            }

            @Test
            fun `when the values don't match`() {
                shouldFailWithMessage("""|Expecting a value that starts with: <[red, pink]>
                                         |But got...........................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(listOf("red","green","blue")).startsWith("red","pink"))
            }

            @Test
            fun `when the values in a collection don't match`() {
                shouldFailWhenChecking(that(listOf("red","green","blue")).startsWithElementsFrom(listOf("red","pink")))
            }

            @Test
            fun `when the list is too small`() {
                shouldFailWhenChecking(that(listOf("red")).startsWith("red","green"))
            }

            @Test
            fun `when the list is empty`() {
                shouldFailWhenChecking(that(emptyList<String>()).startsWith("red","green"))
            }

            @Test
            fun `when the expected start sequence is empty`() {
                shouldFailWhenChecking(that(listOf("red","green")).startsWith())
            }
        }


        @Nested
        inner class AListThatEndsWith {

            @Test
            fun `when the values match`() {
                shouldPassWhenChecking(that(listOf("red","green","blue")).endsWith("green","blue"))
                shouldPassWhenChecking(that(listOf("red","green","blue")).endsWithElementsFrom(listOf("green","blue")))
            }

            @Test
            fun `when the values don't match`() {
                shouldFailWithMessage("""|Expecting a value that ends with: <[red, pink]>
                                         |But got.........................: <[red, green, blue]>"""
                        .trimMargin())
                        .whenChecking(that(listOf("red","green","blue")).endsWithElementsFrom(listOf("red","pink")))
            }

            @Test
            fun `when the list is too small`() {
                shouldFailWhenChecking(that(listOf("red")).endsWith("red","green"))
            }

            @Test
            fun `when the list is empty`() {
                shouldFailWhenChecking(that(emptyList<String>()).endsWith("red","green"))
            }

            @Test
            fun `when the expected start sequence is empty`() {
                shouldFailWhenChecking(that(listOf("red","green")).endsWith())
            }
        }

        @Nested
        inner class AListWithElementsThatMatchAPredicate {
            val hasALengthOfFour = {it : String -> it.length == 4}

            @Test
            fun `when all the values match`() {

                shouldPassWhenChecking(that(listOf("blue","cyan","pink")).allMatch("4 characters long", hasALengthOfFour))
            }

            @Test
            fun `when the list is empty`() {

                shouldPassWhenChecking(that(emptyList<String>()).allMatch("4 characters long", hasALengthOfFour))
            }


            @Test
            fun `when not all the values match`() {
                shouldFailWithMessage("""|Expecting a collection that matches: each element is 4 characters long
                                         |But got: [red, green, blue]"""
                        .trimMargin())
                        .whenChecking(that(listOf("red","green","blue")).allMatch("4 characters long", hasALengthOfFour))
            }
        }

        @Nested
        inner class AListWhereSomeElementsMatch {

            val isRed = {it : String -> it == "red"}
            val emptyList = emptyList<String>()

            @Test
            fun `when there are matching elements`() {

                shouldPassWhenChecking(that(listOf("red","cyan","blue")).anyMatch("is red", isRed))
            }

            @Test
            fun `when the list is empty`() {

                shouldFailWhenChecking(that(emptyList).anyMatch("4 characters long", isRed))
            }

            @Test
            fun `when there are no matching elements`() {
                shouldFailWithMessage("""|Expecting a collection that matches: at least one element is pink
                                         |But got: [red, green, blue]"""
                        .trimMargin())
                        .whenChecking(that(listOf("red","green","blue")).anyMatch("pink") { it : String -> it == "pink"})
            }
        }


        @Nested
        inner class AListWhereNoElementsMatch {

            val isPurple = {it : String -> it == "purple"}
            val isRed = {it : String -> it == "red"}
            val emptyList = emptyList<String>()

            @Test
            fun `when there are matching elements`() {

                shouldPassWhenChecking(that(listOf("red","cyan","blue")).noneMatch("purple", isPurple))
            }

            @Test
            fun `when the list is empty`() {

                shouldPassWhenChecking(that(emptyList).noneMatch("purple", isPurple))
            }

            @Test
            fun `when there are no matching elements`() {
                shouldFailWithMessage("""|Expecting a collection that matches: no elements are red
                                         |But got: [red, green, blue]"""
                        .trimMargin())
                        .whenChecking(that(listOf("red","green","blue")).noneMatch("red", isRed))
            }
        }


        @Nested
        inner class AListWhereAMinimumNumberOfElementsMatch {

            val isRed = {it : String -> it == "red"}
            val isBlue = {it : String -> it == "blue"}
            val emptyList = emptyList<String>()

            @Test
            fun `when there are enough matching elements`() {
                shouldPassWhenChecking(that(listOf("red","red","cyan","blue")).atLeast(2,"red", isRed))
            }

            @Test
            fun `when there are not enough matching elements`() {
                shouldPassWhenChecking(that(listOf("red","red","blue")).noMoreThan(2,"blue", isBlue))
            }

            @Test
            fun `when the list is empty`() {
                shouldFailWhenChecking(that(emptyList).atLeast(2,"red", isRed))
            }
        }


        @Nested
        inner class AListWhereAMaximumNumberOfElementsMatch {

            val isRed = {it : String -> it == "red"}
            val emptyList = emptyList<String>()

            @Test
            fun `when there are enough matching elements`() {
                shouldPassWhenChecking(that(listOf("red","red","blue")).noMoreThan(2,"red", isRed))
            }

            @Test
            fun `when there are notenough matching elements`() {
                shouldFailWhenChecking(that(listOf("red","red","red","blue")).noMoreThan(2,"red", isRed))
            }

            @Test
            fun `when the list is empty`() {
                shouldPassWhenChecking(that(emptyList).noMoreThan(2,"red", isRed))
            }
        }

        @Nested
        inner class AListWhereAGivenNumberOfElementsMatch {

            val isRed = {it : String -> it == "red"}
            val isBlue = {it : String -> it == "blue"}
            val emptyList = emptyList<String>()

            @Test
            fun `when there are exactly N matching elements`() {
                shouldPassWhenChecking(that(listOf("red","red","cyan","blue")).exactly(2,"red", isRed))
            }

            @Test
            fun `when there are not exactly N matching elements`() {
                shouldFailWhenChecking(that(listOf("red","red","blue")).exactly(2,"blue", isBlue))
            }

            @Test
            fun `when the list is empty`() {
                shouldFailWhenChecking(that(emptyList).exactly(2,"red", isRed))
            }
        }


    }
}