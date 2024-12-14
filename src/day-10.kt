private const val HIGHEST = 9

private val VALID_DIRS = listOf(
    Direction.NORTH,
    Direction.EAST,
    Direction.SOUTH,
    Direction.WEST,
)

private fun Grid<Int>.calculateTrailHeadScore(value: Int, pos: Vec2): Int {
    var score = 0
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

private fun Grid<Int>.findDistinctPaths(value: Int, pos: Vec2): Set<List<Vec2>> {
    val paths = mutableSetOf<List<Vec2>>()

    fun dfs(current: Vec2, currentHeight: Int, currentPath: List<Vec2>) {
        if (this[current] == HIGHEST) {
            paths.add(currentPath)
            return
        }

        for (dir in VALID_DIRS) {
            val next = current + dir.value
            val nextHeight = this[next]

            if (nextHeight == currentHeight + 1 && next !in currentPath) {
                dfs(next, nextHeight, currentPath + next)
            }
        }
    }

    dfs(pos, value, listOf(pos))

    return paths
}

private fun partOne(input: String): Int {
    val parsed = input.lines().map { it.map { it.digitToInt() }.toMutableList() }
    val map = Grid<Int>(parsed)
    var scoreSum = 0

    map.forEach { pos, value ->
        if (value != 0) return@forEach
        scoreSum += map.calculateTrailHeadScore(value, pos)
    }

    return scoreSum
}

private fun partTwo(input: String): Int {
    val parsed = input.lines().map { it.map { it.digitToInt() }.toMutableList() }
    val map = Grid<Int>(parsed)
    var rating = 0

    map.forEach { pos, value ->
        if (value != 0) return@forEach

        val distinctPaths = map.findDistinctPaths(value, pos)
        rating += distinctPaths.size
    }

    return rating
}

fun main() {
    solve(::partOne, "day-10.test", 36)
    solve(::partOne, "day-10")
    solve(::partTwo, "day-10.test", 81)
    solve(::partTwo, "day-10")
}
