import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private enum class Tile(val char: Char) {
    EMPTY('.'), OBSTACLE('#'), GUARD('^');

    companion object {
        fun fromChar(char: Char): Tile {
            return when (char) {
                '.' -> EMPTY
                '#' -> OBSTACLE
                '^' -> GUARD
                else -> error("UNKNOWN TILE")
            }
        }
    }
}

private class Lab(tiles: List<MutableList<Char>>) : Grid<Char>(tiles) {
    val guard: Guard = Guard(this, GuardState(Vec2(-1, -1), Direction.NORTH)).apply {
        tiles.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                if (Tile.fromChar(char) == Tile.GUARD) {
                    state.pos = Vec2(x, y)
                    startingPosition = state.pos
                }
            }
        }
    }
}

private fun Lab.copy(): Lab {
    val tilesCopy = tiles.map { it.toMutableList() }
    return Lab(tilesCopy)
}

private fun Lab.forEach(cb: (Vec2, Tile) -> Unit) {
    tiles.forEachIndexed { y, line ->
        line.forEachIndexed { x, char -> cb(Vec2(x, y), Tile.fromChar(char)) }
    }
}

private operator fun Lab.get(pos: Vec2): Tile? {
    val char = tiles.getOrNull(pos.y)?.getOrNull(pos.x) ?: return null
    return Tile.fromChar(char)
}

private operator fun Lab.set(pos: Vec2, tile: Tile) {
    if (pos.y !in tiles.indices || pos.x !in tiles[0].indices) {
        return
    }
    tiles[pos.y][pos.x] = tile.char
}

private data class GuardState(var pos: Vec2, var facing: Direction)

private fun GuardState.turnRight() {
    facing = when (facing) {
        Direction.NORTH -> Direction.EAST
        Direction.EAST -> Direction.SOUTH
        Direction.SOUTH -> Direction.WEST
        Direction.WEST -> Direction.NORTH
        else -> error("UNKNOWN DIRECTION")
    }
}

private class Guard(val lab: Lab, val state: GuardState) {
    lateinit var startingPosition: Vec2
}

private fun Guard.inMapBounds(): Boolean = lab[state.pos] != null

private fun Guard.getNextTile(): Tile? = lab[state.pos + state.facing.value]

private fun Guard.step() {
    if (getNextTile() == Tile.OBSTACLE) {
        state.turnRight()
        return step()
    }

    state.pos += state.facing.value
}

/** Simulate the path of the guard and return the set of visited positions */
private fun Guard.simulatePath(): Set<Vec2> {
    val visited = mutableSetOf<Vec2>()
    while (inMapBounds()) {
        step()
        visited.add(state.pos)
    }
    return visited
}

/** Return true if the guard has gotten stuck in a loop */
private fun Guard.simulatePathUntilLoop(): Boolean {
    val visited = mutableSetOf<GuardState>()

    while (inMapBounds()) {
        val currentState = GuardState(state.pos.copy(), state.facing)
        if (!visited.add(currentState)) {
            return true
        }
        step()
    }

    return false
}

private fun partOne(input: String): Int {
    val tiles = input.lines().map { it.toMutableList() }
    val lab = Lab(tiles)
    val guard = lab.guard
    val visited = guard.simulatePath()

    return visited.size - 1 // start position offset
}

private fun partTwo(input: String): Int {
    val tiles = input.lines().map { it.toMutableList() }
    val lab = Lab(tiles)
    val guard = lab.guard
    val count = AtomicInteger(0)

    runBlocking(Dispatchers.Default) {
        lab.forEach { pos, _ ->
            if (pos == guard.startingPosition) return@forEach
            if (lab[pos] == Tile.OBSTACLE) return@forEach

            launch {
                val copy = lab.copy()
                copy[pos] = Tile.OBSTACLE

                val stuck = copy.guard.simulatePathUntilLoop()
                if (stuck) count.incrementAndGet()
            }
        }
    }

    return count.get()
}

fun main() {
    solve(::partOne, "day-6.test", 41)
    solve(::partOne, "day-6")
    solve(::partTwo, "day-6.test", 6)
    solve(::partTwo, "day-6")
}
