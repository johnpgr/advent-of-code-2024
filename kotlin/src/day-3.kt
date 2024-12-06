enum class Instruction {
    MUL, DO, DONT;

    companion object {
        fun fromString(str: String): Instruction = when (str) {
            "mul()" -> MUL
            "do()" -> DO
            "don't()" -> DONT
            else -> throw IllegalArgumentException("Unknown instruction: $str")
        }
    }
}

data class Mul(val a: Int, val b: Int) {
    companion object {
        fun fromMatch(match: MatchResult): Mul {
            val (a, b) = match.destructured
            return Mul(a.toInt(), b.toInt())
        }
    }
}

fun Mul.calculate(): Int = a * b

data class State(val enabled: Boolean, val sum: Int)

fun main() {
    val mulPattern = Regex("""mul\((\d+),(\d+)\)""")
    val controlPattern = Regex("""(don't|do)\(\)""")

    fun partOne(input: String): Int = mulPattern.findAll(input).fold(0) { acc, res ->
        val (a, b) = res.destructured
        acc + (a.toInt() * b.toInt())
    }

    fun partTwo(input: String): Int =
        (mulPattern.findAll(input).map { Instruction.MUL to it } + controlPattern.findAll(input)
            .map { Instruction.fromString(it.value) to it }).sortedBy { it.second.range.first }
            .scan(State(enabled = true, sum = 0)) { state, match ->
                when (match.first) {
                    Instruction.MUL -> state.copy(
                        sum = if (state.enabled) state.sum + Mul.fromMatch(match.second).calculate()
                        else state.sum
                    )

                    Instruction.DO -> state.copy(enabled = true)
                    Instruction.DONT -> state.copy(enabled = false)
                }
            }.last().sum

    solve(::partOne, "day-3")
    solve(::partTwo, "day-3")
}
