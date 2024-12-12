import kotlin.math.abs

private typealias Report = List<Int>

private fun Report.isSafe(): Boolean {
    if (size < 2) return true

    val diffs = (1 until size).map { i -> this[i] - this[i - 1] }

    return (diffs.all { it > 0 } || diffs.all { it < 0 }) && diffs.all { diff -> abs(diff) in 1..3 }
}

private fun partOne(input: String): Int {
    val reports: List<Report> = input.lines().map { line -> line.split(" ").map { n -> n.toInt() } }

    val safeReports = reports.count { it.isSafe() }

    return safeReports
}

private fun partTwo(input: String): Int {
    val reports: List<Report> = input.lines().map { line -> line.split(" ").map { n -> n.toInt() } }
    val safeReports = reports.count { report ->
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

fun main() {
    solve(::partOne, "day-2")
    solve(::partOne, "day-2.test", 2)
    solve(::partTwo, "day-2")
    solve(::partTwo, "day-2.test", 4)
}
