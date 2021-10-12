package by.profs.rowgame.presenter.trainer

import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.items.Damageable
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.MyDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Trainer(private val deleteRowerFun: (Int?) -> Unit) {
    private val boatDao: BoatDao = ServiceLocator.locate()
    private val oarDao: OarDao = ServiceLocator.locate()
    private val rowerDao: RowerDao = ServiceLocator.locate()

    suspend fun startTraining(mode: Int, combos: MutableList<Combo>) = withContext(Dispatchers.IO) {
        combos.forEach { combo ->
            val boat = boatDao.search(combo.boatId) ?: return@withContext
            val oar = oarDao.search(combo.oarId) ?: return@withContext
            val rower = rowerDao.search(combo.rowerId) ?: return@withContext
            var random = (1..rowerUpChance).random()
            if (random < rowerCharacteristicsNumber) {
                when (mode) {
                    TRAIN_ENDURANCE -> rower.upEndurance()
                    TRAIN_POWER -> rower.upPower()
                    TRAIN_TECHNICALITY -> rower.upTechnics()
                }
                rower.saveUpdate()
            }
            random = (1..injuryChance).random()
            if (random == 1 && !rower.hurt((1..maxInjury).random())) deleteCombo(combo)
            brokeItem(combo, boat, boatDao as MyDao<Damageable>)
            brokeItem(combo, oar, oarDao as MyDao<Damageable>)
        }
    }

    private fun deleteCombo(combo: Combo) = deleteRowerFun(combo.rowerId)

    private fun brokeItem(combo: Combo, item: Damageable, dao: MyDao<Damageable>) {
        val random = (1..maxDamage).random()
        if (random < acceptableDamage) {
            if (item.broke(random)) dao.updateItem(item)
            else {
                deleteCombo(combo)
                dao.deleteItem(item.id!!)
            }
        }
    }

    companion object {
        private const val acceptableDamage = 11
        private const val injuryChance = 99 // resultChance = 1 : injuryChance
        private const val maxInjury = 4 // resultChance = 1 : injuryChance
        private const val maxDamage = 365 // chance to damage = (acceptableDamage-1) : maxDamage
        private const val rowerCharacteristicsNumber = 3
        private const val rowerUpChance = 22 // resultChance = CharacteristicNumber : rowerUpChance
    }
}