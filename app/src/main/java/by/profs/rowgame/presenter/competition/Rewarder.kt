package by.profs.rowgame.presenter.competition

import by.profs.rowgame.app.ServiceLocator.get
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.competition.License
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Rewarder(private val standing: ArrayList<Rower>, private val competition: CompetitionInfo) {
    private val myRowers = get(ComboDao::class).getRowerIds()

    fun calculateFame(): Int = if (isMine(FIRST)) fameForWin else 0

    fun giveItems() = CoroutineScope(Dispatchers.IO).launch {
        if (isMine(FIRST)) get(BoatDao::class).insert(Randomizer.getRandomBoat())
        if (isMine(SECOND)) giveRandomOar()
        if (isMine(THIRD)) giveRandomOar()
    }

    suspend fun giveLicensesAndCalculateMoney(): Int {
        val competitionDao = get(CompetitionDao::class)
        var totalPrize = 0
        var rewardForPlace = basicPrize * competition.level * competition.age
        withContext(Dispatchers.IO) {
            standing.take(quota).forEach {
                val rowerId = it.id
                if (rowerId != null && myRowers.contains(rowerId)) {
                    competitionDao.addLicenses(
                        listOf(
                            License(null, rowerId, competition.level + 1, competition.age),
                            License(null, rowerId, competition.level, competition.age + 1)
                        )
                    )
                    totalPrize += rewardForPlace
                }
                rewardForPlace /= 2
            }
        }
        return totalPrize
    }

    private fun isMine(position: Int) = myRowers.contains(standing[position].id)
    private fun giveRandomOar() = get(OarDao::class).insert(Randomizer.getRandomOar())

    companion object {
        private const val fameForWin = 4
        private const val quota = 7
        private const val basicPrize = 500
        // Numeration in rowers array
        private const val FIRST = 0
        private const val SECOND = 1
        private const val THIRD = 2
    }
}