package by.profs.rowgame.presenter.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import by.profs.rowgame.utils.NAME_ROWER
import by.profs.rowgame.utils.ROWER_SOURCE
import by.profs.rowgame.view.inventory.RowerDetailsActivity

class ItemDetailNavigation(private val context: Context) {
    fun goToRowerFromList(name: String) {
        val intent = Intent(context, RowerDetailsActivity::class.java)
        intent.putExtra(ROWER_SOURCE, RowerDetailsActivity.FROM_LIST)
        intent.putExtra(NAME_ROWER, name)
        ContextCompat.startActivity(context, intent, null)
    }

    fun goToNewRower() {
        val intent = Intent(context, RowerDetailsActivity::class.java)
        intent.putExtra(ROWER_SOURCE, RowerDetailsActivity.RANDOM_ROWER)
        ContextCompat.startActivity(context, intent, null)
    }
}