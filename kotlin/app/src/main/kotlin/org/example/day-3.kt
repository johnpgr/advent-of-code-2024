package org.example

object DayThree {
    const val CURRENT_DAY = 3
    const val TEST_INPUT = "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"
    const val TEST_INPUT2 = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"
    val mulPattern = Regex("""mul\((\d+),(\d+)\)""")
    val controlPattern = Regex("""(don't|do)\(\)""")
    val INPUT = this::class.java.getResourceAsStream("/input/day-$CURRENT_DAY.txt")?.bufferedReader()?.readText()
        ?: error("Input file not found for day $CURRENT_DAY")

    // fun partOne(): Int {
    //     val result = mulPattern.findAll(INPUT).fold(0) { acc, res ->
    //         val (a, b) = res.destructured
    //         acc + (a.toInt() * b.toInt())
    //     }
    //
    //     return result
    // }

    fun partOne(): Int = mulPattern.findAll(INPUT).fold(0) { acc, res ->
        val (a, b) = res.destructured
        acc + (a.toInt() * b.toInt())
    }

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

        fun calculate(): Int = a * b
    }

    data class State(val enabled: Boolean, val sum: Int)

    // fun partTwo(): Int {
    //     val mulMatches = mulPattern.findAll(INPUT).map { Instruction.MUL to it }
    //     val controlMatches =
    //             controlPattern.findAll(INPUT).map { Instruction.fromString(it.value) to it }
    //
    //     val allMatches = (mulMatches + controlMatches).sortedBy { it.second.range.first }
    //     return allMatches
    //             .scan(State(enabled = true, sum = 0)) { state, match ->
    //                 when (match.first) {
    //                     Instruction.MUL ->
    //                             state.copy(
    //                                     sum =
    //                                             if (state.enabled)
    //                                                     state.sum +
    //                                                             Mul.fromMatch(match.second)
    //                                                                     .calculate()
    //                                             else state.sum
    //                             )
    //                     Instruction.DO -> state.copy(enabled = true)
    //                     Instruction.DONT -> state.copy(enabled = false)
    //                 }
    //             }
    //             .last()
    //             .sum
    // }

    fun partTwo(): Int = (
        mulPattern.findAll(INPUT).map { Instruction.MUL to it } +
        controlPattern.findAll(INPUT).map { Instruction.fromString(it.value) to it }
    ).sortedBy { it.second.range.first }
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
}
