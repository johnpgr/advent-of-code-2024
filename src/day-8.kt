import kotlin.math.abs

private fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}

private fun Vec2.collinearityPoints(other: Vec2): Pair<Vec2, Vec2> {
    val dx = other.x - x
    val dy = other.y - y

    // Get direction vector using GCD for smallest step
    val gcd = gcd(abs(dx), abs(dy))
    val dirX = dx / gcd
    val dirY = dy / gcd

    // Calculate points one step before first point and one step after second point
    val beforeFirst = Vec2(x - dirX, y - dirY)
    val afterSecond = Vec2(other.x + dirX, other.y + dirY)

    return Pair(beforeFirst, afterSecond)
}

private fun <T> Vec2.collinearityPoints(other: Vec2, grid: Grid<T>): List<Vec2> {
    val dx = other.x - x
    val dy = other.y - y
    val gridWidth = grid.tiles[0].size
    val gridHeight = grid.tiles.size

    // Get direction vector using GCD for smallest step
    val gcd = gcd(abs(dx), abs(dy))
    val dirX = dx / gcd
    val dirY = dy / gcd

    val points = mutableListOf<Vec2>()

    // Start from one step before first point
    var currentX = x - dirX
    var currentY = y - dirY

    // Add points before the first point while in bounds
    while (currentX in 0 until gridWidth && currentY in 0 until gridHeight) {
        points.add(Vec2(currentX, currentY))
        currentX -= dirX
        currentY -= dirY
    }

    // Start from one step after second point
    currentX = other.x + dirX
    currentY = other.y + dirY

    // Add points after the second point while in bounds
    while (currentX in 0 until gridWidth && currentY in 0 until gridHeight) {
        points.add(Vec2(currentX, currentY))
        currentX += dirX
        currentY += dirY
    }

    return points
}

private fun partOne(input: String): Int {
    val grid = Grid<Char>(input.lines().map { it.toMutableList() })
    val antennas = mutableMapOf<Char, MutableList<Vec2>>()
    grid.forEach { pos, char ->
        if (char == '.') return@forEach
        antennas.getOrPut(char) { mutableListOf() }.add(pos)
    }

    val uniqueCollinearPoints = mutableSetOf<Vec2>()
    antennas.forEach { (_, positions) ->
        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val pos1 = positions[i]
                val pos2 = positions[j]
                val (colP1, colP2) = pos1.collinearityPoints(pos2)

                if (grid.isInBounds(colP1)) {
                    uniqueCollinearPoints.add(colP1)
                }
                if (grid.isInBounds(colP2)) {
                    uniqueCollinearPoints.add(colP2)
                }
            }
        }
    }

    return uniqueCollinearPoints.size
}

private fun partTwo(input: String): Int {
    val grid = Grid<Char>(input.lines().map { it.toMutableList() })
    val antennas = mutableMapOf<Char, MutableList<Vec2>>()
    grid.forEach { pos, char ->
        if (char == '.') return@forEach
        antennas.getOrPut(char) { mutableListOf() }.add(pos)
    }

    val uniqueCollinearPoints = mutableSetOf<Vec2>()

    antennas.forEach { (_, positions) ->
        if (positions.size < 2) return@forEach
        for (i in positions.indices) {
            uniqueCollinearPoints.add(positions[i])
            for (j in i + 1 until positions.size) {
                val pos1 = positions[i]
                val pos2 = positions[j]
                val points = pos1.collinearityPoints(pos2, grid)

                points.forEach { point -> uniqueCollinearPoints.add(point) }
            }
        }
    }

    return uniqueCollinearPoints.size
}

fun main() {
    solve(::partOne, "day-8.test", 14)
    solve(::partOne, "day-8")
    solve(::partTwo, "day-8.test", 34)
    solve(::partTwo, "day-8")
}
