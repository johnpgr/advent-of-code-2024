import java.util.concurrent.atomic.AtomicLong 
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class Equation(val testValue: Long, val operators: List<Long>) {
    companion object {
        fun fromLine(line: String): Equation {
            val (testInputRaw, opsRaw) = line.split(": ")
            val ops = opsRaw.split(" ").map(String::toLong)
            val testInput = testInputRaw.toLong()
            return Equation(testInput, ops)
        }
    }
}


fun Equation.evaluate(): Boolean {
    var acc = operators[0]

    for (i in 1 until operators.size) {
        val sum = acc + operators[i]
        val prod = acc * operators[i]

        if (i == operators.size - 1) {
            return sum == testValue || prod == testValue
        }

        val sumBranch = Equation(testValue, listOf(sum) + operators.drop(i + 1))
        val prodBranch = Equation(testValue, listOf(prod) + operators.drop(i + 1))

        return (sumBranch.evaluate() || prodBranch.evaluate())
    }

    return acc == testValue
}

fun Equation.evaluate2(): Boolean {
    var acc = operators[0]

    for (i in 1 until operators.size) {
        val sum = acc + operators[i]
        val prod = acc * operators[i]
        val concat = (acc.toString() + operators[i].toString()).toLong()

        if (i == operators.size - 1) {
            return sum == testValue || prod == testValue || concat == testValue
        }

        val sumBranch = Equation(testValue, listOf(sum) + operators.drop(i + 1))
        val prodBranch = Equation(testValue, listOf(prod) + operators.drop(i + 1))
        val concatBranch = Equation(testValue, listOf(concat) + operators.drop(i + 1))

        return (sumBranch.evaluate2() || prodBranch.evaluate2() || concatBranch.evaluate2())
    }

    return acc == testValue
}

fun main() {
    fun partOne(input: String): Long {
        val equations = input.lines().map(Equation::fromLine)
        return equations.filter(Equation::evaluate).sumOf(Equation::testValue)
    }

    fun partTwo(input: String): Long {
        val equations = input.lines().map(Equation::fromLine)
        return equations.filter(Equation::evaluate2).sumOf(Equation::testValue)
    }

    solve(::partOne, "day-7.test", 3749)
    solve(::partOne, "day-7")
    solve(::partTwo, "day-7.test", 11387)
    solve(::partTwo, "day-7")
}
