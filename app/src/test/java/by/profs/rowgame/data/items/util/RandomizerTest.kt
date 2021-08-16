package by.profs.rowgame.data.items.util

import by.profs.rowgame.data.items.Boat
import junit.framework.TestCase

class RandomizerTest : TestCase() {

    fun testGetRandomBoat() {
        val listSize = 5
        val randomBoatList = List(listSize) { Randomizer.getRandomBoat() }
        randomBoatList.forEach {
            Manufacturer.valueOf(it.manufacturer)
            assertTrue(Boat.EXTRA_SMALL <= it.body && it.body <= Boat.UNIVERSAL)
            assertTrue(Boat.CLASSIC_STAY <= it.wing && it.wing <= Boat.BACKWING)
            assertTrue(Boat.RECREATIONAL <= it.weight && it.weight <= Boat.ELITE)
        }
    }
}