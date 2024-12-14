open class Grid<T>(val tiles: List<MutableList<T>>)

operator fun <T> Grid<T>.get(pos: Vec2): T? {
    return tiles.getOrNull(pos.y)?.getOrNull(pos.x)
}

operator fun <T> Grid<T>.set(pos: Vec2, value: T) {
    tiles.getOrNull(pos.y)?.set(pos.x, value)
}

fun <T> Grid<T>.asSequence(): Sequence<Pair<Vec2, T>> = sequence {
    for (y in tiles.indices) {
        for (x in tiles[y].indices) {
            val pos = Vec2(x, y)
            val value = this@asSequence[pos]
            if (value != null) {
                yield(pos to value)
            }
        }
    }
}

fun <T> Grid<T>.isInBounds(point: Vec2): Boolean {
    return point.x in 0 until tiles[0].size && point.y in 0 until tiles.size
}

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)
}

enum class Direction(val value: Vec2) {
    NORTH(Vec2(0, -1)),
    SOUTH(Vec2(0, 1)),
    EAST(Vec2(1, 0)),
    WEST(Vec2(-1, 0)),
    NORTH_EAST(Vec2(1, -1)),
    NORTH_WEST(Vec2(-1, -1)),
    SOUTH_EAST(Vec2(1, 1)),
    SOUTH_WEST(Vec2(-1, 1));

    val x: Int get() = value.x
    val y: Int get() = value.y
}
