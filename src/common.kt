open class Grid<T>(val tiles: List<MutableList<T>>)

operator fun <T> Grid<T>.get(pos: Vec2): T? {
    return tiles.getOrNull(pos.y)?.getOrNull(pos.x)
}

operator fun <T> Grid<T>.set(pos: Vec2, value: T) {
    tiles.getOrNull(pos.y)?.set(pos.x, value)
}

fun <T> Grid<T>.forEach(cb: (pos: Vec2, value: T) -> Unit) {
    for (y in tiles.indices) {
        for (x in tiles[y].indices) {
            cb(Vec2(x, y), tiles[y][x])
        }
    }
}

fun <T> Grid<T>.map(cb: (pos: Vec2, value: T) -> T): Grid<T> {
    val newTiles = tiles.mapIndexed { y, row ->
        row.mapIndexed { x, value ->
            cb(Vec2(x, y), value)
        }.toMutableList()
    }

    return Grid(newTiles)
}

fun <T, I> Grid<T>.fold(initial: I, cb: (acc: I, pos: Vec2, value: T) -> I): I {
    var acc = initial

    forEach { pos, value ->
        acc = cb(acc, pos, value)
    }

    return acc
}

fun <T> Grid<T>.count(cb: (pos: Vec2, value: T) -> Boolean): Int {
    return fold(0) { acc, pos, value ->
        if (cb(pos, value)) acc + 1 else acc
    }
}

fun <T> Grid<T>.sumOf(cb: (pos: Vec2, value: T) -> Int): Int {
    return fold(0) { acc, pos, value ->
        acc + cb(pos, value)
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
