package by.profs.rowgame.utils

object NumberGenerator {
    private const val BIG = 100000000

    // return random_number%module
    fun generatePositiveIntOrNull(module: Int = BIG): Int = (Math.random() * BIG).toInt() % module
}