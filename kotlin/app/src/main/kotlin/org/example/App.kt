/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example

class App {}

fun main(args: Array<String>) {
    val day = args.getOrNull(0)?.toInt() ?: 1

    when (day) {
        1 -> {
            println("day 1 part 1: ${DayOne.partOne()}")
            println("day 1 part 2: ${DayOne.partTwo()}")
        }
        2 -> {
            DayTwo.partOne()
            // println("day 1 part 1: ${DayTwo.partOne()}")
            // println("day 1 part 2: ${DayTwo.partTwo()}")
        }
        else -> println("Day $day not implemented")
    }
}
