package by.profs.rowgame.view.activity

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.data.preferences.MoneyFameEditor
import by.profs.rowgame.databinding.ActivityMainBinding
import by.profs.rowgame.reminder.ReminderReceiver
import by.profs.rowgame.view.fragments.extensions.makeInvisible
import by.profs.rowgame.view.fragments.extensions.makeVisible
import by.profs.rowgame.view.fragments.extensions.showToast

class MainActivity : ActivityWithInfoBar(), FullScreenAble {
    private val navController by lazy(LazyThreadSafetyMode.NONE) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navHostFragment.navController
    }
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = requireNotNull(_binding)
    private val calendar: Calendar = Calendar
    private val prefEditor: MoneyFameEditor = MoneyFameEditor

    override fun changeMode(isFullScreen: Boolean) =
        if (isFullScreen) binding.navView.makeInvisible() else binding.navView.makeVisible()

    override val infoBar = object : InfoBar {
        override fun showDay() = binding.day.setText(getString(R.string.day, getDay()))

        override fun showFame() = binding.fame.setText(getString(R.string.fame_balance, getFame()))

        override fun showMoney() =
            binding.money.setText(getString(R.string.money_balance, getMoney()))

        override fun nextAndShowDay() {
            calendar.nextDay()
            showDay()
        }

        override fun changeFame(amount: Int) {
            prefEditor.setFame(getFame() + amount)
            showFame()
        }

        override fun changeMoney(amount: Int) {
            prefEditor.setMoney(getMoney() + amount)
            showMoney()
        }

        override fun getDay(): Int = calendar.getDayOfYear()

        override fun getFame(): Int = prefEditor.getFame()

        override fun getMoney(): Int = prefEditor.getMoney()
    }

    override fun setSubtitle(string: String) = binding.toolbar.setSubtitle(string)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        setupBottomNavigation()

        infoBar.showAll()
        setContentView(binding.root)
        getDailyReward()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.changeTheme -> changeTheme(resources)
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Warning: can be hacked by scrolling date on phone
    private fun getDailyReward() {
        val currentDate = calendar.getToday()

        if (currentDate <= calendar.getLastDailyDay()) return

        calendar.setLastDailyDay(currentDate)
        ReminderReceiver.setNotification(applicationContext)
        infoBar.changeFame(bonusFame)
        infoBar.changeMoney(bonusMoney)
        applicationContext.showToast(R.string.daily_collected, bonusMoney, bonusFame)
    }

    private fun setupBottomNavigation() {
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.inventoryFragment, R.id.shopFragment, R.id.pairingFragment, R.id.trainingFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
    }

    companion object {
        private const val bonusMoney = 600
        private const val bonusFame = 1

        private fun changeTheme(resources: Resources): Boolean {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Configuration.UI_MODE_NIGHT_NO ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            return true
        }
    }
}