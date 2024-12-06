package org.example

object DayFour {
    const val CURRENT_DAY = 4
    val INPUT = this::class.java.getResourceAsStream("/input/day-$CURRENT_DAY.txt")?.bufferedReader()?.readText()
        ?: error("Input file not found for day $CURRENT_DAY")
    val TEST_INPUT = """
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
""".trimIndent()

    const val PATTERN = "XMAS"

    enum class Direction(val row: Int, val col: Int) {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1),
        DOWN_LEFT(1, -1),
        DOWN_RIGHT(1, 1),
        UP_LEFT(-1, -1),
        UP_RIGHT(-1, 1);
    }

    // The time complexity would be O(R * C * 8 * 4) where:
    // - R is the number of rows
    // - C is the number of columns
    // - 8 is the number of directions
    // - 4 is the length of "XMAS"
    fun partOne(): Int = INPUT.lines().map { it.toList() }.let { grid ->
        grid.indices.flatMap { row ->
            grid[row].indices.flatMap { col ->
                Direction.entries.map { direction ->
                    //hard-coded XMAS 4 characters long
                    (PATTERN.indices).all { i ->
                        val (newRow, newCol) = Pair(
                            row + direction.row * i, col + direction.col * i
                        )
                        newRow in grid.indices && newCol in grid[newRow].indices && grid[newRow][newCol] == "XMAS"[i]
                    }
                }
            }
        }
    }.count { it }

    fun List<String>.forEachChar(cb: (row: Int, col: Int, char: Char) -> Unit) {
        for (row in indices) {
            for (col in this[row].indices) {
                cb(row, col, this[row][col])
            }
        }
    }

    operator fun List<String>.get(row: Int, col: Int, dir: Direction, mult: Int = 1): Char =
        get(row + dir.row * mult, col + dir.col * mult)

    operator fun List<String>.get(row: Int, col: Int): Char =
        if (row in indices && col in this[row].indices) this[row][col] else ' '

    fun partTwo(): Int {
        val grid = INPUT.lines()
        val diagonals = listOf(
            Direction.UP_RIGHT,
            Direction.DOWN_LEFT,
            Direction.DOWN_RIGHT,
            Direction.UP_LEFT,
        )
        var count = 0

        grid.forEachChar { row, col, char ->
            if (char == 'A') {
                val corners = diagonals.map { grid[row, col, it] }
                val differ = corners[0] != corners[1] && corners[2] != corners[3]
                val patternFound = corners.count { it == 'M' } == 2 && corners.count { it == 'S' } == 2
                if (patternFound && differ) count++
            }
        }

        return count
    }
}
