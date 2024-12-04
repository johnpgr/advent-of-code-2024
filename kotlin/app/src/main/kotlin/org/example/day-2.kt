package org.example

import kotlin.math.abs

object DayTwo {
    const val CURRENT_DAY = 2
    val INPUT = this::class.java.getResourceAsStream("/input/day-$CURRENT_DAY.txt")?.bufferedReader()?.readLines()
        ?: error("Input file not found for day $CURRENT_DAY")

    const val MAX_DIFF = 3

    fun partOne(): Int {
        val reports = INPUT.map { line -> line.split(" ").map { n -> n.toInt() } }
        val safeReports = reports.fold(0) { acc, report ->
            if (report.size < 2) {
                // println("Report $report is not safe (size < 2)")
                return@fold acc
            }

            val firstDiff = report[1] - report[0]
            val isIncreasing = firstDiff > 0

            if (firstDiff == 0) {
                // println("Report $report is not safe (first diff == 0)")
                return@fold acc
            }

            if (abs(firstDiff) > MAX_DIFF) {
                return@fold acc
            }

            for (i in 2..<report.size) {
                val currentDiff = report[i] - report[i - 1]
                val currentIsIncreasing = currentDiff > 0

                if (currentDiff == 0) {
                    // println("Report $report is not safe (current diff == 0)")
                    return@fold acc
                }

                if (abs(currentDiff) > MAX_DIFF) {
                    // println("Report $report is not safe (current diff > $MAX_DIFF)")
                    return@fold acc
                }

                if (currentIsIncreasing != isIncreasing) {
                    // println("Report $report is not safe (current isIncreasing != isIncreasing)")
                    return@fold acc
                }
            }

            println("Report $report")

            return@fold acc + 1
        }

        return safeReports
    }

    fun partTwo(): Int {
        return -1
    }
}
