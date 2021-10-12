package by.profs.rowgame.data.items

import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test

class RowerTest : TestCase() {

    private val endurance = 15
    private val technics = 5
    private val power = 20
    private val rower = Rower(null, "Hg Hg", Rower.MALE, 19, 200, 84, power, technics, endurance, 1)

    @Test
    fun testUpEndurance() {
        rower.upEndurance()
        // Check that we successfully add necessary characteristic ...
        Assert.assertEquals(endurance + 1, rower.endurance)
        // ... and didn't made impact on others
        Assert.assertEquals(technics, rower.technics)
        Assert.assertEquals(power, rower.power)
    }

    @Test
    fun testUpTechnics() {
        rower.upTechnics()
        // Check that we successfully add necessary characteristic ...
        Assert.assertEquals(technics + 1, rower.technics)
        // ... and didn't made impact on others
        Assert.assertEquals(endurance, rower.endurance)
        Assert.assertEquals(power, rower.power)
    }

    @Test
    fun testUpPower() {
        rower.upPower()
        // Check that we successfully add necessary characteristic ...
        Assert.assertEquals(power + 1, rower.power)
        // ... and didn't made impact on others
        Assert.assertEquals(technics, rower.technics)
        Assert.assertEquals(endurance, rower.endurance)
    }

    @Test
    fun testHurt() {
        val injury = 4
        rower.hurt(injury)
        Assert.assertEquals(endurance - injury, rower.endurance)
        Assert.assertEquals(power - injury, rower.power)
        Assert.assertEquals(technics - injury, rower.technics)
        Assert.assertFalse(rower.hurt(rower.endurance + 1))
    }
}