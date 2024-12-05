package org.example

import kotlin.math.abs

typealias Report = List<Int>

fun Report.isSafe(): Boolean {
    if (size < 2) return true

    val diffs = (1 until size).map { i -> this[i] - this[i - 1] }

    return (diffs.all { it > 0 } || diffs.all { it < 0 }) && 
           diffs.all { diff -> abs(diff) in 1..3 }
}

object DayTwo {
    const val CURRENT_DAY = 2
    val INPUT =
            this::class
                    .java
                    .getResourceAsStream("/input/day-$CURRENT_DAY.txt")
                    ?.bufferedReader()
                    ?.readLines()
                    ?: error("Input file not found for day $CURRENT_DAY")

    const val MAX_DIFF = 3

    fun partOne(): Int {
        val reports: List<Report> = INPUT.map { line -> line.split(" ").map { n -> n.toInt() } }

        val safeReports = reports.count { it.isSafe() }

        return safeReports
    }

    fun partTwo(): Int {
        val reports: List<Report> = INPUT.map { line -> line.split(" ").map { n -> n.toInt() } }
        val safeReports =
                reports.count { report ->
                    if (report.isSafe()) {
                        true
                    } else {
                        report.indices.any { i ->
                            val newReport = report.toMutableList().apply { removeAt(i) }
                            newReport.isSafe()
                        }
                    }
                }

        return safeReports
    }
}
