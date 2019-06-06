package net.serenitybdd.screenplay.ensure.collections

/*

 */
class CollectionsComparison<A>(val comparator: Comparator<A>? = null) {
    fun areEqual(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null && expected == null) return true
        if (actual == null || expected == null) return false

        return compareElementsOf(actual, expected)
    }

    private fun compareElementsOf(actual: Collection<A>, expected: Collection<A>): Boolean {
        return if (comparator == null) actual == expected else elementsAreIdenticalUsingComparatorIn(actual, expected)
    }

    private fun elementsAreIdenticalUsingComparatorIn(actual: Collection<A>, expected: Collection<A>): Boolean {
        if (actual.size != expected.size) return false

        actual.forEachIndexed { i, value ->
            if (!(isSame(value, expected.elementAt(i)))) return false
        }
        return true
    }

    fun isSame(actual: A, expected: A): Boolean {
        if (actual == null && expected == null) return true
        if (actual == null || expected == null) return false

        return if (comparator == null) actual == expected else (comparator.compare(actual, expected) == 0)
    }
}
