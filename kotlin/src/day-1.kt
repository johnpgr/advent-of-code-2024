import kotlin.math.abs

fun main() {
    fun getCols(input: String): Pair<List<Int>, List<Int>> {
        val columns = input.lines().fold(Pair(listOf<Int>(), listOf<Int>())) { acc, line ->
            val split = line.split("   ")
            Pair(acc.first + split[0].toInt(), acc.second + split[1].toInt())
        }
        return columns
    }

    fun partOne(input: String): Int {
        val columns = getCols(input)
        val sortedLeft = columns.first.sorted()
        val sortedRight = columns.second.sorted()
        val distances = sortedLeft.mapIndexed { i, left ->
            val right = sortedRight.getOrNull(i) ?: 0
            abs(left - right)
        }
        val sum = distances.sum()
        return sum
    }

    fun partTwo(input: String): Int {
        val columns = getCols(input)
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

    solve(::partOne, "day-1.test", 11)
    solve(::partOne, "day-1")
    solve(::partTwo, "day-1.test", 31)
    solve(::partTwo, "day-1")
}
