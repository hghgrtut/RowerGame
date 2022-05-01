package by.profs.rowgame.presenter.competition.type

import android.content.Context
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.Ages
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.competition.CompetitionLevel
import by.profs.rowgame.data.competition.CompetitionLevel.Companion.isRegional
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.presenter.competition.RaceCalculator
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WaterCompetition(private val competition: CompetitionInfo) : AbstractCompetition {
    private var _raceCalculator: RaceCalculator? = null

    private val comboDao: ComboDao by ServiceLocator.locateLazy()
    private val competitionDao: CompetitionDao by ServiceLocator.locateLazy()
    private val boatDao: BoatDao by ServiceLocator.locateLazy()
    private val oarDao: OarDao by ServiceLocator.locateLazy()
    private val rowerDao: RowerDao by ServiceLocator.locateLazy()

    private val myBoats = mutableListOf<Boat>()
    private val myOars = mutableListOf<Oar>()
    private val myRowers = mutableListOf<Rower>()
    private val finalABoats = mutableListOf<Boat>()
    private val finalAOars = mutableListOf<Oar>()
    private val finalARowers = mutableListOf<Rower>()
    private val finalBBoats = mutableListOf<Boat>()
    private val finalBOars = mutableListOf<Oar>()
    private val finalBRowers = mutableListOf<Rower>()

    private var raceBoats: List<Boat>? = null
    private var raceOars: List<Oar>? = null
    private var raceRowers: List<Rower>? = null

    var raceNumber = -1
    private var myCount: Int = UNINITIALIZED

    private val minSkill: Int
    private val maxSkil: Int
    private val age: Int

    init {
        val basicLevel = CompetitionLevel.values()[competition.level]
        val enumAge = Ages.values()[competition.age]
        minSkill = (basicLevel.minRowerSkill * enumAge.skillCoef).toInt()
        maxSkil = (basicLevel.maxRowerSkill * enumAge.skillCoef).toInt()
        age = enumAge.age
    }

    fun getRaceBoats(): List<Boat> {
        if (raceBoats == null) raceBoats = when (raceNumber) {
            FINAL_B -> finalBBoats
            FINAL_A -> finalABoats
            else -> {
                val from = raceNumber * raceSize
                val to = from + raceSize
                myBoats.mySubList(from, to) + List(getBotsCount(to)) { Randomizer.getRandomBoat() }
            }
        }
        return raceBoats!!
    }

    fun getRaceOars(): List<Oar> {
        if (raceOars == null) raceOars = when (raceNumber) {
            FINAL_B -> finalBOars
            FINAL_A -> finalAOars
            else -> {
                val from = raceNumber * raceSize
                val to = from + raceSize
                myOars.mySubList(from, to) + List(getBotsCount(to)) { Randomizer.getRandomOar() }
            }
        }
        return raceOars!!
    }

    override fun getRaceRowers(): List<Rower> {
        if (raceRowers == null) raceRowers = when (raceNumber) {
            FINAL_B -> finalBRowers
            FINAL_A -> finalARowers
            else -> {
                val from = raceNumber * raceSize
                val to = from + raceSize
                myRowers.mySubList(from, to) + List(getBotsCount(to)) {
                    Randomizer.getRandomRower(minSkill = minSkill, maxSkill = maxSkil, maxAge = age)
                }
            }
        }
        return raceRowers!!
    }

    override fun getRaceCalculator(): RaceCalculator {
        if (_raceCalculator == null) _raceCalculator =
            RaceCalculator(AbstractCompetition.WATER, raceRowers!!, raceBoats, raceOars)
        return _raceCalculator!!
    }

    override fun deleteRaceCalculator() { _raceCalculator = null }

    override val changeStrategy: (Int, Int) -> Unit = { rowerId, strategy ->
        var i = 0
        while (raceRowers!![i].id != rowerId) i++
        raceRowers!![i].strategy = strategy
    }

    fun calculateSemifinal(rating: ArrayList<Rower>) {
        var finalistA = 0
        var finalistB = 0
        while (raceRowers!![finalistA].name != rating[0].name) finalistA++
        while (raceRowers!![finalistB].name != rating[1].name) finalistB++
        finalABoats.add(raceBoats!![finalistA])
        finalAOars.add(raceOars!![finalistA])
        finalARowers.add(raceRowers!![finalistA])
        finalBBoats.add(raceBoats!![finalistB])
        finalBOars.add(raceOars!![finalistB])
        finalBRowers.add(raceRowers!![finalistB])
    }

    override suspend fun setupRace() = withContext(Dispatchers.IO) {
        raceNumber++
        if (myCount == UNINITIALIZED) {
            (if (competition.level.isRegional()) {
                comboDao.getCombosToAge(Ages.values()[competition.age].age)
            } else {
                competitionDao.getParticipants(competition.level, competition.age)
            }).forEach { combo ->
                myBoats.add(boatDao.search(combo.boatId)!!)
                myOars.add(oarDao.search(combo.oarId)!!)
                myRowers.add(rowerDao.search(combo.rowerId)!!)
            }
        }
        raceRowers = null
        raceBoats = null
        raceOars = null
        myCount = myRowers.size
        raceRowers = getRaceRowers()
        raceBoats = getRaceBoats()
        raceOars = getRaceOars()
    }

    override fun raceTitle(): String {
        val context = ServiceLocator.get(Context::class)
        return _raceCalculator?.let { when (_raceCalculator!!.phase) {
                AbstractCompetition.START -> context.getString(R.string.phase_start)
                AbstractCompetition.HALF -> context.getString(R.string.phase_half)
                AbstractCompetition.ONE_AND_HALF -> context.getString(R.string.phase_one_and_half)
                else -> context.getString(R.string.phase_finish)
            }
        } ?: when (raceNumber) {
            FINAL_B -> context.getString(R.string.final_, 'B')
            FINAL_A -> context.getString(R.string.final_, 'A')
            else -> context.getString(R.string.semifinal)
        }
    }

    private fun getBotsCount(lastIndex: Int) = minOf(maxOf(lastIndex - myCount, 0), raceSize)

    private fun <T> MutableList<T>.mySubList(from: Int, to: Int): List<T> =
        if (myCount > from) subList(from, minOf(to, myCount)).toList() else mutableListOf()

    companion object {
        const val FINAL_B = 6
        const val FINAL_A = 7

        private const val raceSize = 6
        private const val UNINITIALIZED = -19879237
    }
}