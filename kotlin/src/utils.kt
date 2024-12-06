import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("input/$name.txt").readText().trim()

fun solve(fn: (String) -> Int, input: String) {
    val result = fn(readInput(input))
    println(result)
}

fun solve(fn: (String) -> Int, input: String, assert: Int) {
    val result = fn(readInput(input))
    require(result == assert) { "Expected $assert but got $result" }
    println(result)
}
