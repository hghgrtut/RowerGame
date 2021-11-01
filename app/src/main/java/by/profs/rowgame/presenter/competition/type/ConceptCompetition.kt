package by.profs.rowgame.presenter.competition.type

import android.content.Context
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.Ages
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.competition.CompetitionLevel
import by.profs.rowgame.data.competition.CompetitionLevel.Companion.isRegional
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.presenter.competition.RaceCalculator
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConceptCompetition(private val competition: CompetitionInfo) : AbstractCompetition {
    private val comboDao: ComboDao by ServiceLocator.locateLazy()
    private val competitionDao: CompetitionDao by ServiceLocator.locateLazy()
    private val rowerDao: RowerDao by ServiceLocator.locateLazy()
    private val allRowers = mutableListOf<Rower>()
    override var raceCalculator: RaceCalculator? = null

    override fun getRaceRowers(): MutableList<Rower> = allRowers

    override suspend fun setupRace() {
        (if (competition.level.isRegional()) {
            comboDao.getCombosToAge(Ages.values()[competition.age].age)
        } else {
            competitionDao.getParticipants(competition.level, competition.age)
        }).forEach { combo ->
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
        raceCalculator = RaceCalculator(AbstractCompetition.CONCEPT, allRowers)
    }

    override fun raceTitle(): String = ServiceLocator.get(Context::class).getString(
        when (raceCalculator!!.phase) {
            AbstractCompetition.BEFORE -> R.string.concept
            AbstractCompetition.START -> R.string.phase_start
            AbstractCompetition.HALF -> R.string.phase_half
            AbstractCompetition.ONE_AND_HALF -> R.string.phase_one_and_half
            else -> R.string.phase_finish
    })
}