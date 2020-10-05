package by.profs.rowgame.presenter.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import by.profs.rowgame.utils.ITEM
import by.profs.rowgame.view.shop.ShopActivity

class ShopNavigation(private val context: Context) {

    fun goToBoatShop() {
        val intent = Intent(context, ShopActivity::class.java)
        intent.putExtra(ITEM, INTENT_BOATS)
        ContextCompat.startActivity(context, intent, null)
    }

    fun goToOarShop() {
        val intent = Intent(context, ShopActivity::class.java)
        intent.putExtra(ITEM, INTENT_OARS)
        ContextCompat.startActivity(context, intent, null)
    }
}