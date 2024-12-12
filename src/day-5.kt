private fun List<Int>.inOrder(rules: List<List<Int>>): Boolean {
    for (rule in rules) {
        val (a, b) = rule
        if (contains(a) && contains(b)) {
            if (indexOf(a) > indexOf(b)) {
                return false
            }
        }
    }
    return true
}

private fun List<Int>.sortWithRules(rules: List<List<Int>>): List<Int> {
    tailrec fun sort(current: List<Int>, hasSwapped: Boolean): List<Int> = when {
        !hasSwapped -> current
        else -> {
            val (newList, swapped) = rules.fold(Pair(current, false)) { (acc, didSwap), rule ->
                val (first, second) = rule
                when {
                    !acc.contains(first) || !acc.contains(second) -> Pair(acc, didSwap)

                    acc.indexOf(first) > acc.indexOf(second) -> {
                        val swappedList = acc.toMutableList().apply {
                            val firstIndex = indexOf(first)
                            val secondIndex = indexOf(second)
                            set(firstIndex, second)
                            set(secondIndex, first)
                        }
                        Pair(swappedList, true)
                    }

                    else -> Pair(acc, didSwap)
                }
            }
            sort(newList, swapped)
        }
    }

    return sort(this, true)
}

private fun partOne(input: String): Int {
    val splits = input.split("\n\n")
    val pageOrderingRules = splits[0].lines().map { it.split("|").map(String::toInt) }
    val pages = splits[1].lines().map { it.split(",").map(String::toInt) }
    val correctlyOrdered = pages.filter { it.inOrder(pageOrderingRules) }
    val middleSum = correctlyOrdered.fold(0) { acc, list -> acc + list[list.size / 2] }

    return middleSum
}

private fun partTwo(input: String): Int {
    val splits = input.split("\n\n")
    val pageOrderingRules = splits[0].lines().map { it.split("|").map(String::toInt) }
    val pages = splits[1].lines().map { it.split(",").map(String::toInt) }
    val incorrectlyOrdered = pages.filterNot { it.inOrder(pageOrderingRules) }
    val ordered = incorrectlyOrdered.map { it.sortWithRules(pageOrderingRules) }
    val middleSum = ordered.fold(0) { acc, list -> acc + list[list.size / 2] }

    return middleSum
}

fun main() {
    solve(::partOne, "day-5.test", 143)
    solve(::partOne, "day-5")
    solve(::partTwo, "day-5.test", 123)
    solve(::partTwo, "day-5")
}
