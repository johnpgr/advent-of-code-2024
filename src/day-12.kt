private data class Region(val area: Int, val perimeter: Int)

private val DIRS = listOf(Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST)

private fun Grid<Char>.countPerimeterContribution(
    pos: Vec2,
    letter: Char,
    visited: MutableSet<Vec2>,
    queue: ArrayDeque<Vec2>
): Int {
    var perimeterCount = 0
    
    for (direction in DIRS) {
        val newPos = pos + direction.value
        when {
            !isInBounds(newPos) -> perimeterCount++

            this[newPos] != letter -> perimeterCount++

            newPos !in visited -> {
                queue.add(newPos)
                visited.add(newPos)
            }
        }
    }
    
    return perimeterCount
}

private fun Grid<Char>.getRegion(
    pos: Vec2,
    visited: MutableSet<Vec2>
): Region {
    val letter = this[pos] ?: error("Grid.getRegion with Position out of bounds")
    var area = 0
    var perimeter = 0
    val queue = ArrayDeque<Vec2>()

    queue.add(pos)
    visited.add(pos)

    while (queue.isNotEmpty()) {
        val currentPos = queue.removeFirst()
        area++
        
        perimeter += countPerimeterContribution(currentPos, letter, visited, queue)
    }

    return Region(area, perimeter)
}

private fun partOne(input: String): Int {
    val map = Grid<Char>(input.lines().map { it.map { it }.toMutableList() })
    val visited = mutableSetOf<Vec2>()
    val regions = mutableListOf<Region>()

    for (pos in map) {
        if (pos in visited) continue
        regions.add(map.getRegion(pos, visited))
    }

    return regions.sumOf { it.area * it.perimeter }
}

private fun partTwo(input: String): Int { return -1 }

fun main() {
    solve(::partOne, "day-12.test", 1930)
    solve(::partOne, "day-12")
}
