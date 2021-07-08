package by.profs.rowgame.presenter.trainer

import by.profs.rowgame.data.combos.Combo
import by.profs.rowgame.data.items.Damageable
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.MyDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Trainer(database: MyRoomDatabase, private val deleteRowerFun: (Int?) -> Unit) {
    private val boatDao: BoatDao = database.boatDao()
    private val oarDao: OarDao = database.oarDao()
    private val rowerDao: RowerDao = database.rowerDao()

    suspend fun startTraining(mode: Int, combos: MutableList<Combo>) {
        combos.forEach { combo ->
            val boat = withContext(Dispatchers.IO) { boatDao.search(combo.boatId) } ?: return
            val oar = withContext(Dispatchers.IO) { oarDao.search(combo.oarId) } ?: return
            val rower = withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId) } ?: return
            var random = (1..rowerUpChance).random()
            if (random < rowerCharacteristicsNumber) {
                when (mode) {
                    TRAIN_ENDURANCE -> rower.upEndurance()
                    TRAIN_POWER -> rower.upPower()
                    TRAIN_TECHNICALITY -> rower.upTechnics()
                }
                rowerDao.updateItem(rower)
            }
            random = (1..injuryChance).random()
            if (random == 1) {
                if (rower.hurt((1..maxInjury).random())) rowerDao.updateItem(rower)
                else {
                    deleteCombo(combo)
                    rowerDao.deleteItem(rower.id!!)
                }
            }
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
        private const val injuryChance = 90 // resultChance = 1 : injuryChance
        private const val maxInjury = 4 // resultChance = 1 : injuryChance
        private const val maxDamage = 365 // chance to damage = (acceptableDamage-1) : maxDamage
        private const val rowerCharacteristicsNumber = 3
        private const val rowerUpChance = 33 // resultChance = CharacteristicNumber : rowerUpChance
    }
}