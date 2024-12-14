private const val HIGHEST = 9

private val VALID_DIRS = listOf(
    Direction.NORTH,
    Direction.EAST,
    Direction.SOUTH,
    Direction.WEST,
)

private fun Grid<Int>.calculateTrailHeadScore(value: Int, pos: Vec2): Long {
    var score = 0L
    val visited = mutableSetOf<Vec2>()
    val reachedHighest = mutableListOf<Vec2>()

    // println("Starting calculation for value: $value at position: $pos")

    fun step(pos: Vec2, currentHeight: Int) {
        if (pos in visited) {
            // println("  Already visited position: $pos")
            return
        }

        visited += pos

        if (this[pos] == HIGHEST && pos !in reachedHighest) {
            // println("  Reached highest point at position: $pos")
            reachedHighest += pos
            score++
        }

        for (dir in VALID_DIRS) {
            val nextPos = pos + dir.value
            val nextHeight = this[nextPos]
            // println("    Checking direction $dir: position $nextPos has value $nextHeight")

            if (nextHeight == currentHeight + 1) {
                // println("    Found valid step in direction: $dir")
                step(nextPos, nextHeight)
            }
        }
    }

    step(pos, value)
    // println("Final score for trailhead at $pos: $score (reached 9s: $reachedHighest)")

    return score
}

private fun partOne(input: String): Long {
    val parsed = input.lines().map { it.map { it.digitToInt() }.toMutableList() }
    val map = Grid<Int>(parsed)
    var scoreSum = 0L

    map.forEach { pos, value ->
        if (value != 0) return@forEach
        scoreSum += map.calculateTrailHeadScore(value, pos)
    }

    return scoreSum
}

private fun partTwo(input: String): Long {
    return 0
}

fun main() {
    solve(::partOne, "day-10.test", 36)
    solve(::partOne, "day-10")
}
