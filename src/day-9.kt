private const val FREE_SPACE = Int.MIN_VALUE

private class FileSystem(input: String) {
    private data class State(val data: List<Int>, val id: Int, val file: Boolean)

    var maxId: Int = 0

    val data: MutableList<Int> = run {
        // Initial state
        val initial = State(data = emptyList<Int>(), id = 0, file = true)

        val finalState = input.fold(initial) { state, char ->
            // Number of blocks to allocate
            val count = char.digitToInt()

            when (state.file) {
                // Allocate blocks for file
                true -> State(data = state.data + List(count) { state.id }, id = state.id + 1, file = false)
                // Allocate blocks for free space
                false -> State(data = state.data + List(count) { FREE_SPACE }, id = state.id, file = true)
            }
        }

        maxId = finalState.id - 1

        // Return the final state
        finalState.data.toMutableList()
    }

    override fun toString(): String = data.joinToString("") { element ->
        when (element) {
            FREE_SPACE -> "."
            else -> element.toString()
        }
    }
}

private fun FileSystem.moveByBytes() {
    var left = data.indexOf(FREE_SPACE)
    var right = data.indexOfLast { it != FREE_SPACE }

    while (left < right) {
        val current = data[right]
        if (current != FREE_SPACE) {
            data[right] = FREE_SPACE
            data[left] = current
            left = data.indexOf(FREE_SPACE)
        }
        right--
    }
}

private fun FileSystem.getLeftMostFreeSpace(minSize: Int): IntRange? {
    // println("Looking for free space of size $minSize")
    var freeStart = -1
    
    data.forEachIndexed { i, x ->
        if (x == FREE_SPACE && freeStart == -1) {
            freeStart = i
        } else if (x != FREE_SPACE && freeStart != -1) {
            val range = freeStart until i
            if (range.count() >= minSize) {
                // println("Found free space: $range")
                return range
            }
            freeStart = -1
        }
    }
    
    val result = if (freeStart != -1) {
        val range = freeStart until data.size
        if (range.count() >= minSize) range else null
    } else null

    // println("Found free space: $result")

    return result
}

private fun FileSystem.getFile(id: Int): IntRange? {
    var fileStart = -1
    
    data.forEachIndexed { i, x ->
        if (x == id && fileStart == -1) {
            fileStart = i
        } else if (x != id && fileStart != -1) {
            return fileStart until i
        }
    }
    
    return if (fileStart != -1) {
        fileStart until data.size
    } else null
}


private fun FileSystem.moveFile(fileRange: IntRange, freeSpace: IntRange){
    val fileId = data[fileRange.first]

    // println("Moving file $fileId from $fileRange to $freeSpace")

    for (i in fileRange) {
        data[i] = FREE_SPACE
    }

    for (i in freeSpace) {
        if (i < freeSpace.first + (fileRange.last - fileRange.first + 1)) {
            data[i] = fileId
        }
    }
}

private fun FileSystem.moveByFiles() {
    for (id in maxId downTo 0) {
        val file = getFile(id) ?: error("This shouldn't happen")
        val freeSpace = getLeftMostFreeSpace(file.count()) ?: continue

        if (freeSpace.first < file.first) {
            moveFile(file, freeSpace)
        }
    }
}

private fun FileSystem.checkSum(): Long = data.indices.sumOf { 
    it.toLong() * (if (data[it] != FREE_SPACE) data[it].toLong() else 0L) 
}

private fun partOne(input: String): Long {
    return FileSystem(input).let { it.moveByBytes(); it.checkSum() }
}

private fun partTwo(input: String): Long {
    return FileSystem(input).let { it.moveByFiles(); it.checkSum() }
}

fun main() {
    // solve(::partOne, "day-9.test", 1928)
    // solve(::partOne, "day-9")
    solve(::partTwo, "day-9.test", 2858)
    solve(::partTwo, "day-9")
}
