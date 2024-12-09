import kotlin.io.path.Path
import kotlin.io.path.readText

/** Reads lines from the given input txt file. */
fun readInput(name: String) = Path("input/$name.txt").readText().trim()

fun <Input, Output> getFunctionName(fn: (Input) -> Output): String = fn.toString().split("function ")[1].split(" ")[0]

fun <Output> solve(fn: (String) -> Output, input: String, assert: Output? = null) {
    val fnName = getFunctionName(fn)
    val inputData = readInput(input)
    val startTime = System.nanoTime()

    val result = fn(inputData)

    val endTime = System.nanoTime()

    val runtimeMs = (endTime - startTime) / 1_000_000.0

    assert?.let { require(result == it) { "Expected $it but got $result" } }

    println("[$input]: $fnName -> $result (runtime: ${String.format("%.2f", runtimeMs)}ms)")
}
