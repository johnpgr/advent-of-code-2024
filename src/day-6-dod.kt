import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.*

// Packed data structures for better memory layout
private data class LabData(
    val width: Int,
    val height: Int,
    val tiles: ByteArray, // Using ByteArray instead of List<List> for contiguous memory
    val guardX: Int,
    val guardY: Int
) {
    // Efficient tile access
    operator fun get(x: Int, y: Int): Byte = if (x in 0 until width && y in 0 until height) tiles[y * width + x] else -1

    operator fun set(x: Int, y: Int, value: Byte) {
        if (x in 0 until width && y in 0 until height) {
            tiles[y * width + x] = value
        }
    }
}

// Constants for better cache utilization
private object TileTypes {
    const val EMPTY: Byte = 0
    const val OBSTACLE: Byte = 1
    const val GUARD: Byte = 2
    const val INVALID: Byte = -1

    fun fromChar(c: Char): Byte = when (c) {
        '.' -> EMPTY
        '#' -> OBSTACLE
        '^' -> GUARD
        else -> INVALID
    }
}

// Directions stored as simple integers
private object Directions {
    val DX = intArrayOf(0, 1, 0, -1) // NORTH, EAST, SOUTH, WEST
    val DY = intArrayOf(-1, 0, 1, 0)
}

private fun parseInput(input: String): LabData {
    val lines = input.lines()
    val height = lines.size
    val width = lines[0].length
    val tiles = ByteArray(width * height)
    var guardX = -1
    var guardY = -1

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            val tile = TileTypes.fromChar(char)
            tiles[y * width + x] = tile
            if (tile == TileTypes.GUARD) {
                guardX = x
                guardY = y
            }
        }
    }

    return LabData(width, height, tiles, guardX, guardY)
}

private fun simulatePath(lab: LabData): Int {
    var x = lab.guardX
    var y = lab.guardY
    var direction = 0
    val visited = mutableSetOf<Pair<Int, Int>>()

    while (x in 0 until lab.width && y in 0 until lab.height) {
        val nextX = x + Directions.DX[direction]
        val nextY = y + Directions.DY[direction]

        when (lab[nextX, nextY]) {
            TileTypes.OBSTACLE -> {
                direction = (direction + 1) % 4
            }

            TileTypes.INVALID -> break
            else -> {
                x = nextX
                y = nextY
                visited.add(x to y)
            }
        }
    }

    return visited.size
}

private fun countLoops(lab: LabData): Int {
    val count = AtomicInteger(0)

    runBlocking(Dispatchers.Default) {
        for (y in 0 until lab.height) {
            for (x in 0 until lab.width) {
                if (x == lab.guardX && y == lab.guardY) continue
                if (lab[x, y] == TileTypes.OBSTACLE) continue

                launch {
                    val labCopy = lab.copy(tiles = lab.tiles.clone())
                    labCopy[x, y] = TileTypes.OBSTACLE

                    if (detectLoop(labCopy)) {
                        count.incrementAndGet()
                    }
                }
            }
        }
    }

    return count.get()
}

private fun detectLoop(lab: LabData): Boolean {
    val visited = mutableSetOf<Triple<Int, Int, Int>>()
    var x = lab.guardX
    var y = lab.guardY
    var direction = 0

    while (x in 0 until lab.width && y in 0 until lab.height) {
        if (!visited.add(Triple(x, y, direction))) {
            return true
        }

        val nextX = x + Directions.DX[direction]
        val nextY = y + Directions.DY[direction]

        when (lab[nextX, nextY]) {
            TileTypes.OBSTACLE -> direction = (direction + 1) % 4
            TileTypes.INVALID -> return false
            else -> {
                x = nextX
                y = nextY
            }
        }
    }

    return false
}

private fun partOne(input: String): Int {
    val lab = parseInput(input)
    return simulatePath(lab)
}

private fun partTwo(input: String): Int {
    val lab = parseInput(input)
    return countLoops(lab)
}

fun main() {
    solve(::partOne, "day-6.test", 41)
    solve(::partOne, "day-6")
    solve(::partTwo, "day-6.test", 6)
    solve(::partTwo, "day-6")
}
