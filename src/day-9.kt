private const val FREE_SPACE = Long.MIN_VALUE

private class FileSystem(input: String) {
    private data class State(val data: List<Long>, val id: Long, val file: Boolean)

    val data: MutableList<Long> = run {
        // Initial state
        val initial = State(data = emptyList<Long>(), id = 0, file = true)

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

private fun FileSystem.moveByFiles() {
    var fileEnd = data.indexOfLast { it != FREE_SPACE }
    
    while (fileEnd > 0) {
        val currentFile = data[fileEnd]
        
        var fileStart = fileEnd
        while(fileStart > 0 && data[fileStart - 1] == currentFile) {
            fileStart--
        }
        
        val fileSize = fileEnd - fileStart + 1
        var freeStart = data.indexOf(FREE_SPACE)
        
        if (freeStart >= 0 && freeStart < fileStart) {
            for (i in 0 until fileSize) {
                data[freeStart + i] = currentFile
                data[fileStart + i] = FREE_SPACE
            }
        }
        
        // Update fileEnd to point to the position before the current file
        fileEnd = fileStart - 1
    }
}


private fun FileSystem.checkSum(): Long = data.indices.sumOf { it * (if (data[it] != FREE_SPACE) data[it] else 0) }

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
    // solve(::partTwo, "day-9")
}
