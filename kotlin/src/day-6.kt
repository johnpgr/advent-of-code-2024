data class Vec2(val x: Int, val y: Int) {
    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)

    /**
     * Rotates clock-wise
     */
    fun rot90(): Vec2 = Vec2(-y, x)
}

enum class Tile {
    NOTHING, OBSTACLE, GUARD;

    companion object {
        fun fromChar(char: Char): Tile {
            return when (char) {
                '.' -> NOTHING
                '#' -> OBSTACLE
                '^', '>', '<', 'v' -> GUARD
                else -> error("UNKNOWN TILE")
            }
        }
    }
}

class Lab(input: String) {
    val tiles: List<MutableList<Tile>> = input.lines().map { line ->
        line.map { char ->
            Tile.fromChar(char)
        }.toMutableList()
    }

    val guard = Guard(this, Vec2(-1, -1), NORTH_FACING)

    init {
        tiles.forEachIndexed { y, line ->
            line.forEachIndexed { x, tile ->
                if (tile == Tile.GUARD) {
                    guard.pos = Vec2(x, y)
                }
            }
        }
    }
}

operator fun Lab.get(pos: Vec2): Tile? {
    return tiles.getOrNull(pos.y)?.getOrNull(pos.x)
}

operator fun Lab.set(pos: Vec2, tile: Tile) {
    if (pos.y !in tiles.indices || pos.x !in tiles[0].indices) {
        return
    }
    tiles[pos.y][pos.x] = tile
}

fun Lab.print(visitedPositions: Set<Vec2>) {
    print("\u001b[H\u001b[2J")  // Moves cursor to home position and clears screen
    print("\u001b[3J")          // Clears scrollback buffer
    tiles.forEachIndexed { y, row ->
        row.forEachIndexed { x, tile ->
            val pos = Vec2(x, y)
            val char = when {
                (visitedPositions.contains(pos) && tile != Tile.GUARD) -> 'X'
                (visitedPositions.contains(pos) && tile == Tile.GUARD) -> guard.char
                tile == Tile.NOTHING -> '.'
                tile == Tile.OBSTACLE -> '#'
                else -> '?'
            }
            print(char)
        }
        print("\n")
    }
    print("\n")
}

val NORTH_FACING = Vec2(0, -1)
val SOUTH_FACING = Vec2(0, 1)
val EAST_FACING = Vec2(1, 0)
val WEST_FACING = Vec2(-1, 0)

class Guard(val lab: Lab, var pos: Vec2, var facing: Vec2) {
    var char: Char = when (facing) {
        NORTH_FACING -> '^'
        SOUTH_FACING -> 'v'
        EAST_FACING -> '>'
        WEST_FACING -> '<'
        else -> error("Invalid facing direction")
    }
}

fun Guard.inMapBounds(): Boolean = lab[pos] != null

fun Guard.getTileAhead(): Tile? = lab[pos + facing]

fun Guard.rot90() {
    facing = facing.rot90()
}

fun Guard.step() {
    if (getTileAhead() == Tile.OBSTACLE) {
        rot90()
        return step()
    }

    lab[pos] = Tile.NOTHING
    pos += facing
    char = when (facing) {
        NORTH_FACING -> '^'
        SOUTH_FACING -> 'v'
        EAST_FACING -> '>'
        WEST_FACING -> '<'
        else -> error("Invalid facing direction")
    }
    lab[pos] = Tile.GUARD
}

fun main() {
    fun partOne(input: String): Int {
        val lab = Lab(input)
        val visited = mutableSetOf(lab.guard.pos)
//        lab.print(visited)

        while (lab.guard.inMapBounds()) {
//            Thread.sleep(100)
            lab.guard.step()
            visited.add(lab.guard.pos)
//            lab.print(visited)
        }

        return visited.size - 1 //start position offset
    }

    fun partTwo(input: String): Int {
        return -1
    }

    solve(::partOne, "day-6.test", 41)
    solve(::partOne, "day-6")
}
