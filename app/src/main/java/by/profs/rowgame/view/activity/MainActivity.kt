package by.profs.rowgame.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.ActivityMainBinding
import by.profs.rowgame.view.utils.HelperFuns.changeTheme

class MainActivity : ActivityWithInfoBar() {
    private val navController by lazy(LazyThreadSafetyMode.NONE) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navHostFragment.navController
    }
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = requireNotNull(_binding)
    private var _calendar: Calendar? = null
    private val calendar: Calendar get() = requireNotNull(_calendar)
    private var _prefEditor: PreferenceEditor? = null
    private val prefEditor: PreferenceEditor get() = requireNotNull(_prefEditor)

    override val infoBar = object : InfoBar {
        override fun showDay() = binding.day.setText(getString(R.string.day, getDay()))

        override fun showFame() = binding.fame.setText(getString(R.string.fame_balance, getFame()))

        override fun showMoney() =
            binding.money.setText(getString(R.string.money_balance, getMoney()))

        override fun nextAndShowDay() {
            calendar.nextDay()
            showDay()
        }

        override fun setFame(fame: Int) {
            prefEditor.setFame(fame)
            showFame()
        }

        override fun setMoney(money: Int) {
            prefEditor.setMoney(money)
            showMoney()
        }

        override fun getDay(): Int = calendar.getDayOfYear()

        override fun getFame(): Int = prefEditor.getFame()

        override fun getMoney(): Int = prefEditor.getMoney()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        _calendar = Calendar(this)
        _prefEditor = PreferenceEditor(this)
        infoBar.showAll()
        setContentView(binding.root)
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
}