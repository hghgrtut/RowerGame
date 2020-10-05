package by.profs.rowgame.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.profs.rowgame.R
import by.profs.rowgame.data.PreferenceEditor
import by.profs.rowgame.databinding.ActivityMainBinding
import by.profs.rowgame.presenter.navigation.InventoryNavigation
import by.profs.rowgame.presenter.navigation.PairingNavigation
import by.profs.rowgame.presenter.utils.Resetter.giveInitMoney
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.utils.HelperFuns.changeTheme
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefEditor: PreferenceEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE))
        binding = ActivityMainBinding.inflate(layoutInflater)

        val inventoryNav = InventoryNavigation(this)
        val pairNav = PairingNavigation(this)

        binding.goToBoats.setOnClickListener { inventoryNav.goToBoats() }
        binding.goToOars.setOnClickListener { inventoryNav.goToOars() }
        binding.goToRowers.setOnClickListener { inventoryNav.goToRowers() }
        binding.goToLegends.setOnClickListener { inventoryNav.goToLegends() }
        binding.goToNewPair.setOnClickListener { pairNav.goToPairingBoat() }
        binding.goToExistingPairs.setOnClickListener { pairNav.goToDetach() }
        binding.goToCompetitions.setOnClickListener { pairNav.goToCompetitions() }

        binding.changeTheme.setOnClickListener { changeTheme(resources) }
        binding.giveMoney.setOnClickListener { MainScope().launch { resetMoney() } }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        val balance = prefEditor.getBalance()
        binding.money.text = this.getString(R.string.money_balance, balance)
        showDay()
    }

    private fun showDay() {
        binding.day.text = this.getString(R.string.day_with_instruction, prefEditor.getDay())
    }

    private suspend fun resetMoney() {
        val flag: Boolean = withContext(Dispatchers.IO) { giveInitMoney(prefEditor, application) }
        showToast(this, if (flag) R.string.money_reseted else R.string.money_not_reseted)
        onResume()
    }
}