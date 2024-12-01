package org.example

object Day1 {
    const val CURRENT_DAY = 1
    val TEST_INPUT = "3   4\n4   3\n2   5\n1   3\n3   9\n3   3".split("\n")
    val INPUT =
            Day1::class.java.getResourceAsStream("/input/day-$CURRENT_DAY.txt")?.bufferedReader()?.readLines()
                    ?: error("Input file not found for day $CURRENT_DAY")
    val columns =
            INPUT.fold(Pair(listOf<Int>(), listOf<Int>())) { acc, line ->
                val split = line.split("   ")
                Pair(acc.first + split[0].toInt(), acc.second + split[1].toInt())
            }

    fun partOne():Int {
        val sortedLeft = columns.first.sorted()
        val sortedRight = columns.second.sorted()
        val distances =
                sortedLeft.mapIndexed { i, left ->
                    val right = sortedRight.getOrNull(i) ?: 0
                    Math.abs(left - right)
                }
        val sum = distances.sum()
        return sum
    }

    fun partTwo():Int {
        val (left, right) = columns
        val similarityScores =
                left.map { value ->
                    val appearenceOnRight =
                            right.fold(0) { acc, rValue ->
                                if (rValue == value) {
                                    acc + 1
                                } else {
                                    acc
                                }
                            }

                    appearenceOnRight * value
                }
        val sum = similarityScores.sum()
        return sum
    }
}
