private operator fun <T> Grid<T>.get(pos: Vec2, dir: Direction, mult: Int = 1): T? =
    tiles.getOrNull(pos.y + dir.y * mult)?.getOrNull(pos.x + dir.x * mult)

const val pattern = "XMAS"

// The time complexity would be O(R * C * 8 * 4) where:
// - R is the number of rows
// - C is the number of columns
// - 8 is the number of directions
// - 4 is the length of "XMAS"
private fun partOne(input: String): Int = input.lines().map { it.toList() }.let { grid ->
    grid.indices.flatMap { y ->
        grid[y].indices.flatMap { x ->
            Direction.entries.map { direction ->
                // hard-coded XMAS 4 characters long
                (pattern.indices).all { i ->
                    val (newRow, newCol) = Pair(
                        x + direction.value.x * i, y + direction.value.y * i
                    )
                    newRow in grid.indices && newCol in grid[newRow].indices && grid[newRow][newCol] == pattern[i]
                }
            }
        }
    }
}.count { it }

@Suppress("USELESS_CAST")
private fun partTwo(input: String): Int {
    val grid = Grid(input.lines().map { it.toMutableList() })
    val diagonals = listOf(
        Direction.NORTH_EAST,
        Direction.SOUTH_WEST,
        Direction.SOUTH_EAST,
        Direction.NORTH_WEST,
    )
    val count = grid.asSequence().sumOf { (pos, char) ->
        if (char == 'A') {
            val corners = diagonals.map { grid[pos, it] }
            val differ = corners[0] != corners[1] && corners[2] != corners[3]
            val patternFound = corners.count { it == 'M' } == 2 && corners.count { it == 'S' } == 2
            if (patternFound && differ) 1 else 0
        } else 0 as Int // this cast is needed, weird jvm bug
    }


    return count
}

fun main() {
    solve(::partOne, "day-4")
    solve(::partOne, "day-4.test", 18)
    solve(::partTwo, "day-4")
    solve(::partTwo, "day-4.test", 9)
}
