package by.profs.rowgame.presenter.competition

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
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class WaterCompetition(private val competition: CompetitionInfo) : AbstractCompetition {
    override var raceCalculator: RaceCalculator? = null

    private val comboDao: ComboDao by ServiceLocator.locateLazy()
    private val competitionDao: CompetitionDao by ServiceLocator.locateLazy()
    private val boatDao: BoatDao by ServiceLocator.locateLazy()
    private val oarDao: OarDao by ServiceLocator.locateLazy()

    private val allBoats = mutableListOf<Boat>()
    private val allOars = mutableListOf<Oar>()
    private val allRowers = mutableListOf<Rower>()
    private val finalABoats = mutableListOf<Boat>()
    private val finalAOars = mutableListOf<Oar>()
    private val finalARowers = mutableListOf<Rower>()
    private val finalBBoats = mutableListOf<Boat>()
    private val finalBOars = mutableListOf<Oar>()
    private val finalBRowers = mutableListOf<Rower>()

    private var raceBoats = mutableListOf<Boat>()
    private var raceOars = mutableListOf<Oar>()
    private var raceRowers = mutableListOf<Rower>()

    var raceNumber = 0

    suspend fun initCompetitors() {
        awaitAll(CoroutineScope(Dispatchers.IO).async { (if (competition.level.isRegional()) {
            comboDao.getCombosToAge(Ages.values()[competition.age].age)
        } else {
            competitionDao.getParticipants(competition.level, competition.age)
        }).forEach { combo ->
            val rower = withContext(Dispatchers.IO) {
                ServiceLocator.get(RowerDao::class).search(combo.rowerId)!! }
            allBoats.add(withContext(Dispatchers.IO) { boatDao.search(combo.boatId)!! })
            allOars.add(withContext(Dispatchers.IO) { oarDao.search(combo.oarId)!! })
            allRowers.add(rower)
        } })
        val free = AbstractCompetition.TOTAL_ROWERS - allBoats.size
        allBoats.addAll(List(free) { Randomizer.getRandomBoat() })
        allOars.addAll(List(free) { Randomizer.getRandomOar() })
        val basicLevel = CompetitionLevel.values()[competition.level]
        val age = Ages.values()[competition.age]
        allRowers.addAll(List(free) { Randomizer.getRandomRower(
            minSkill = (basicLevel.minRowerSkill * age.skillCoef).toInt(),
            maxSkill = (basicLevel.maxRowerSkill * age.skillCoef).toInt(),
            maxAge = age.age
        ) })
    }

    fun getRaceBoats(): MutableList<Boat> = when (raceNumber) {
        FINAL_B -> finalBBoats
        FINAL_A -> finalABoats
        else -> {
            val from = raceNumber * raceSize
            allBoats.subList(from, from + raceSize) } }

    fun getRaceOars(): MutableList<Oar> = when (raceNumber) {
        FINAL_B -> finalBOars
        FINAL_A -> finalAOars
        else -> {
            val from = raceNumber * raceSize
            allOars.subList(from, from + raceSize) } }

    override fun getRaceRowers(): MutableList<Rower> = when (raceNumber) {
        FINAL_B -> finalBRowers
        FINAL_A -> finalARowers
        else -> {
            val from = raceNumber * raceSize
            allRowers.subList(from, from + raceSize) } }

    fun calculateSemifinal(rating: ArrayList<Rower>) {
        var finalistA = 0
        var finalistB = 0
        while (raceRowers[finalistA].name != rating[0].name) finalistA++
        while (raceRowers[finalistB].name != rating[1].name) finalistB++
        finalABoats.add(raceBoats[finalistA])
        finalAOars.add(raceOars[finalistA])
        finalARowers.add(raceRowers[finalistA])
        finalBBoats.add(raceBoats[finalistB])
        finalBOars.add(raceOars[finalistB])
        finalBRowers.add(raceRowers[finalistB])
    }

    override suspend fun setupRace() {
        if (allRowers.isEmpty()) initCompetitors()
        raceRowers = getRaceRowers()
        raceBoats = getRaceBoats()
        raceOars = getRaceOars()
        raceCalculator = RaceCalculator(AbstractCompetition.WATER, raceRowers, raceBoats, raceOars)
    }

    override fun raceTitle(): String {
        val context = ServiceLocator.get(Context::class)
        return when {
            raceCalculator!!.phase == AbstractCompetition.START ->
                context.getString(R.string.phase_start)
            raceCalculator!!.phase == AbstractCompetition.HALF ->
                context.getString(R.string.phase_half)
            raceCalculator!!.phase == AbstractCompetition.ONE_AND_HALF ->
                context.getString(R.string.phase_one_and_half)
            raceCalculator!!.phase == AbstractCompetition.FINISH ->
                context.getString(R.string.phase_finish)
            raceNumber == FINAL_B -> context.getString(R.string.final_, 'B')
            raceNumber == FINAL_A -> context.getString(R.string.final_, 'A')
            else -> context.getString(R.string.semifinal)
        }
    }

    companion object {
        const val FINAL_B = 6
        const val FINAL_A = 7

        private const val raceSize = 6
    }
}