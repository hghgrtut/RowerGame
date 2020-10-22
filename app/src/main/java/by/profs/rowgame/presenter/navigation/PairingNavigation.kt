package by.profs.rowgame.presenter.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import by.profs.rowgame.utils.ITEM
import by.profs.rowgame.view.CompetitionActivity
import by.profs.rowgame.view.PairingActivity
import by.profs.rowgame.view.TrainingActivity

class PairingNavigation(private val context: Context) {
    fun goToDetach() { startActivity(context, Intent(context, TrainingActivity::class.java), null) }

    fun goToPairingBoat() {
        val intent = Intent(context, PairingActivity::class.java)
        intent.putExtra(ITEM, INTENT_BOATS)
        startActivity(context, intent, null)
    }

    fun goToPairingOar() {
        val intent = Intent(context, PairingActivity::class.java)
        intent.putExtra(ITEM, INTENT_OARS)
        startActivity(context, intent, null)
    }

    fun goToPairingRower() {
        val intent = Intent(context, PairingActivity::class.java)
        intent.putExtra(ITEM, INTENT_ROWERS)
        startActivity(context, intent, null)
    }

    fun goToCompetitions() {
        startActivity(context, Intent(context, CompetitionActivity::class.java), null)
    }
}