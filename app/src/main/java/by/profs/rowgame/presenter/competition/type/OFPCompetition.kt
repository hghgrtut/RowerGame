package by.profs.rowgame.presenter.competition.type

import android.content.Context
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.app.ServiceLocator.locateLazy
import by.profs.rowgame.data.competition.Ages
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.competition.CompetitionLevel
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.presenter.competition.RaceCalculator
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OFPCompetition(private val competition: CompetitionInfo) : AbstractCompetition {
    private val comboDao: ComboDao by locateLazy()
    private val rowerDao: RowerDao by locateLazy()
    private val allRowers = mutableListOf<Rower>()
    private var _raceCalculator: RaceCalculator? = null

    override fun getRaceRowers(): List<Rower> = allRowers

    override fun getRaceCalculator(): RaceCalculator {
        if (_raceCalculator == null) _raceCalculator =
            RaceCalculator(AbstractCompetition.OFP, allRowers)
        return _raceCalculator!!
    }

    override fun deleteRaceCalculator() { _raceCalculator = null }

    override val changeStrategy: (Int, Int) -> Unit = { rowerId, strategy ->
        var i = 0
        while (allRowers[i].id != rowerId) i++
        allRowers[i].strategy = strategy
    }

    override suspend fun setupRace() {
        comboDao.getCombosToAge(Ages.values()[competition.age].age).forEach { combo ->
            allRowers.add(withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId)!! })
        }
        val basicLevel = CompetitionLevel.values()[competition.level]
        val age = Ages.values()[competition.age]
        allRowers.addAll(List(AbstractCompetition.TOTAL_ROWERS - allRowers.size) {
            Randomizer.getRandomRower(
                minSkill = (basicLevel.minRowerSkill * age.skillCoef).toInt(),
                maxSkill = (basicLevel.maxRowerSkill * age.skillCoef).toInt(),
                maxAge = age.age)
        })
    }

    override fun raceTitle(): String = ServiceLocator.get(Context::class).getString(
        when (_raceCalculator?.phase) {
            null -> R.string.OFP
            AbstractCompetition.START -> R.string.phase_tyaga
            AbstractCompetition.HALF -> R.string.phase_jumping
            AbstractCompetition.ONE_AND_HALF -> R.string.phase_jump_series
            else -> R.string.phase_running
        })
}