package by.profs.rowgame.utils

import junit.framework.TestCase
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.lessThan
import org.junit.Assert
import org.junit.Test

class NumberGeneratorTest : TestCase() {

    @Test
    fun testGenerateAnyPositiveInt() {
        val generated = NumberGenerator.generatePositiveIntOrNull()
        Assert.assertThat(generated, greaterThanOrEqualTo(0))
    }

    @Test
    fun testGenerateLimitedPositiveInt() {
        val limit = 6
        val generated = NumberGenerator.generatePositiveIntOrNull(limit)
        Assert.assertThat(generated, greaterThanOrEqualTo(0))
        Assert.assertThat(generated, lessThan(limit)) // Because generate  some_int%limit
    }
}