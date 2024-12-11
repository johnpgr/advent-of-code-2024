import kotlin.math.abs

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

private fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}

fun main() {
    fun partOne(input: String): Int {
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

    fun partTwo(): Int {
        return -1
    }

    solve(::partOne, "day-8.test", 14)
    solve(::partOne, "day-8")
}
