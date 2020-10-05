package by.profs.rowgame.presenter.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import by.profs.rowgame.utils.ITEM
import by.profs.rowgame.utils.ROWER_SOURCE
import by.profs.rowgame.view.inventory.InventoryActivity
import by.profs.rowgame.view.inventory.RowerDetailsActivity

class InventoryNavigation(private val context: Context) {

    fun goToBoats() {
        val intent = Intent(context, InventoryActivity::class.java)
        intent.putExtra(ITEM, INTENT_BOATS)
        ContextCompat.startActivity(context, intent, null)
    }

    fun goToOars() {
        val intent = Intent(context, InventoryActivity::class.java)
        intent.putExtra(ITEM, INTENT_OARS)
        ContextCompat.startActivity(context, intent, null)
    }

    fun goToRowers() {
        val intent = Intent(context, InventoryActivity::class.java)
        intent.putExtra(ITEM, INTENT_ROWERS)
        ContextCompat.startActivity(context, intent, null)
    }

    fun goToLegends() {
        val intent = Intent(context, RowerDetailsActivity::class.java)
        intent.putExtra(ROWER_SOURCE, RowerDetailsActivity.FROM_EVENT)
        ContextCompat.startActivity(context, intent, null)
    }
}