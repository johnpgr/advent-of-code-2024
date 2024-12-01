package org.example

object Day {
    const val CURRENT_DAY = -1
    val INPUT =
            Day1::class.java.getResourceAsStream("/input/day-$CURRENT_DAY.txt")?.bufferedReader()?.readLines()
                    ?: error("Input file not found for day $CURRENT_DAY")

    fun partOne():Int { return -1 }
    fun partTwo():Int { return -1 }
}
