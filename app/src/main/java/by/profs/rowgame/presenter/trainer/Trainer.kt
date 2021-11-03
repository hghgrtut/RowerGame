package by.profs.rowgame.presenter.trainer

import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.presenter.database.dao.BoatDao
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
            var random = (1..rowerUpChance).random()
            val rower = rowerDao.search(combo.rowerId) ?: return@withContext
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

            random = getDamage()
            if (random < acceptableDamage) {
                val item = boatDao.search(combo.boatId)!!
                if (item.broke(random)) boatDao.updateItem(item)
                else {
                    deleteCombo(combo)
                    oarDao.deleteItem(item.id!!)
                }
            }

            random = getDamage()
            if (random < acceptableDamage) {
                val item = oarDao.search(combo.oarId)!!
                if (item.broke(random)) oarDao.updateItem(item)
                else {
                    deleteCombo(combo)
                    oarDao.deleteItem(item.id!!)
                }
            }
        }
    }

    private fun deleteCombo(combo: Combo) = deleteRowerFun(combo.rowerId)

    private fun getDamage() = (1..maxDamage).random()

    companion object {
        private const val acceptableDamage = 11
        private const val injuryChance = 99 // resultChance = 1 : injuryChance
        private const val maxInjury = 4 // resultChance = 1 : injuryChance
        private const val maxDamage = 365 // chance to damage = (acceptableDamage-1) : maxDamage
        private const val rowerCharacteristicsNumber = 3
        private const val rowerUpChance = 22 // resultChance = CharacteristicNumber : rowerUpChance
    }
}