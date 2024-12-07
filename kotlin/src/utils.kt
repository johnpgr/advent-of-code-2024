import java.lang.management.ManagementFactory
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("input/$name.txt").readText().trim()

fun getFunctionName(fn: (String) -> Int): String = fn.toString().split("function ")[1].split(" ")[0]

fun solve(fn: (String) -> Int, input: String, assert: Int? = null) {
    val fnName = getFunctionName(fn)
    val memoryMXBean = ManagementFactory.getMemoryMXBean()
    System.gc()
    val beforeUsedHeapMemory = memoryMXBean.heapMemoryUsage.used
    val startTime = System.currentTimeMillis()
    val result = fn(readInput(input))
    val endTime = System.currentTimeMillis()
    val runtimeMs = endTime - startTime
    val afterUsedHeapMemory = memoryMXBean.heapMemoryUsage.used
    val usedMemory = afterUsedHeapMemory - beforeUsedHeapMemory

    assert?.let {
        require(result == it) { "Expected $it but got $result" }
    }

    println("[$input]: $fnName -> $result (runtime: ${runtimeMs}ms, heap: ${usedMemory / 1024}MB)")
}