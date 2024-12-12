import kotlin.math.abs

private data class Cols(val left: List<Int>, val right: List<Int>) {
    companion object {
        fun decode(str: String): Cols {
            val columns = str.lines().fold(Pair(listOf<Int>(), listOf<Int>())) { acc, line ->
                val split = line.split("   ")
                Pair(acc.first + split[0].toInt(), acc.second + split[1].toInt())
            }
            return Cols(columns.first, columns.second)
        }
    }
}

private fun partOne(input: String): Int {
    val columns = Cols.decode(input)
    val sortedLeft = columns.left.sorted()
    val sortedRight = columns.right.sorted()
    val distances = sortedLeft.mapIndexed { i, left ->
        val right = sortedRight.getOrNull(i) ?: 0
        abs(left - right)
    }
    val sum = distances.sum()
    return sum
}

private fun partTwo(input: String): Int {
    val columns = Cols.decode(input)
    val (left, right) = columns
    val similarityScores = left.map { value ->
        val appearanceOnRight = right.fold(0) { acc, rValue ->
            if (rValue == value) {
                acc + 1
            } else {
                acc
            }
        }

        appearanceOnRight * value
    }
    val sum = similarityScores.sum()
    return sum
}

fun main() {
    solve(::partOne, "day-1.test", 11)
    solve(::partOne, "day-1")
    solve(::partTwo, "day-1.test", 31)
    solve(::partTwo, "day-1")
}
