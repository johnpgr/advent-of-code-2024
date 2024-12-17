private val cache = mutableMapOf<Pair<Long, Long>, Long>()

private fun Long.splitInHalf(): Pair<Long, Long> {
    val firstHalf = this.toString().substring(0, this.toString().length / 2).toLong()
    val secondHalf = this.toString().substring(this.toString().length / 2).toLong()
    return Pair(firstHalf, secondHalf)
}

private fun count(stone: Long, steps: Long): Long {
    val key = Pair(stone, steps)
    return cache.getOrPut(key) {
        when {
            (steps == 0L) -> 1
            (stone == 0L) -> count(1, steps - 1)
            ("$stone".length % 2 == 0) -> {
                val (x1, x2) = stone.splitInHalf()
                count(x1, steps - 1) + count(x2, steps - 1)
            }
            else -> count(stone * 2024, steps - 1)
        }
    }
}

private fun partOne(input: String): Long {
    val stones = input.split(" ").map(String::toLong).toMutableList()
    return stones.sumOf { count(it, 25) }
}

private fun partTwo(input: String): Long {
    val stones = input.split(" ").map(String::toLong).toMutableList()
    return stones.sumOf { count(it, 75) }
}

fun main() {
    solve(::partOne, "day-11")
    solve(::partTwo, "day-11")
}
