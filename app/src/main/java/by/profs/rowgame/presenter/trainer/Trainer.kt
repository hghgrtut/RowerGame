package by.profs.rowgame.presenter.trainer

import by.profs.rowgame.data.combos.CombinationSingleScull
import by.profs.rowgame.data.items.Damageable
import by.profs.rowgame.data.items.util.Randomizer.generatePositiveIntOrNull
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.MyDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Trainer(
    private val boatDao: BoatDao,
    private val oarDao: OarDao,
    private val rowerDao: RowerDao,
    private val singleComboDao: SingleComboDao
) {
    suspend fun startTraining(mode: Int, combos: MutableList<CombinationSingleScull>, today: Int) {
        combos.forEach { combo ->
            val boat = withContext(Dispatchers.IO) { boatDao.search(combo.boatId) } ?: return
            val oar = withContext(Dispatchers.IO) { oarDao.search(combo.oarId) } ?: return
            val rower = withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId) } ?: return
            var random = generatePositiveIntOrNull(rowerUpChance)
            if (random < rowerCharacteristicsNumber) {
                when (mode) {
                    TRAIN_ENDURANCE -> rower.upEndurance()
                    TRAIN_POWER -> rower.upPower()
                    TRAIN_TECHNICALITY -> rower.upTechnics()
                }
                rowerDao.updateItem(rower)
            }
            random = generatePositiveIntOrNull((injuryChance / rower.injurability).toInt())
            if (random == 0) {
                val seekDays = minSeekDays + generatePositiveIntOrNull(rangeSeekDays)
                if (rower.hurt(seekDays, today)) rowerDao.updateItem(rower)
                else {
                    deleteCombo(combo)
                    rowerDao.deleteItem(rower.id!!)
                }
            }
            brokeItem(combo, boat, boatDao as MyDao<Damageable>)
            brokeItem(combo, oar, oarDao as MyDao<Damageable>)
        }
    }

    private fun deleteCombo(combo: CombinationSingleScull) {
        CoroutineScope(Dispatchers.IO).launch { singleComboDao.deleteCombo(combo.combinationId!!) }
    }

    private fun brokeItem(combo: CombinationSingleScull, item: Damageable, dao: MyDao<Damageable>) {
        val random = generatePositiveIntOrNull(maxDamage)
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
        private const val maxDamage = 365 // chance to damage = (acceptableDamage-1) : maxDamage
        private const val rowerCharacteristicsNumber = 3
        private const val rowerUpChance = 33 // resultChance = CharacteristicNumber : rowerUpChance

        private const val rangeSeekDays = 37
        private const val minSeekDays = 7
    }
}