package by.profs.rowgame.view.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.ActivityInventoryBinding
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.presenter.navigation.ItemDetailNavigation
import by.profs.rowgame.presenter.navigation.ShopNavigation
import by.profs.rowgame.utils.ITEM
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.INVENTORY
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.RowerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, MODE_PRIVATE))
        layoutManager = LinearLayoutManager(this)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.money.text = this.getString(R.string.money_balance, prefEditor.getBalance())
        recyclerView = findViewById<RecyclerView>(R.id.list).apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
        }

        val shopNavigator = ShopNavigation(this)
        val intentType = intent.extras?.getInt(ITEM)
        when (intentType) {
            INTENT_OARS -> {
                val dao = OarRoomDatabase
                    .getDatabase(application, CoroutineScope(Dispatchers.IO)).oarDao()
                val viewAdapter = OarViewAdapter(INVENTORY, prefEditor, dao)
                recyclerView.apply { adapter = viewAdapter }
                binding.fab.setOnClickListener { shopNavigator.goToOarShop() }
                setTitle(R.string.oar_inventory)
            }
            INTENT_BOATS -> {
                val dao = BoatRoomDatabase
                    .getDatabase(application, CoroutineScope(Dispatchers.IO)).boatDao()
                val viewAdapter = BoatViewAdapter(INVENTORY, prefEditor, dao)
                recyclerView.apply { adapter = viewAdapter }
                binding.fab.setOnClickListener { shopNavigator.goToBoatShop() }
                setTitle(R.string.boat_inventory)
            }
            INTENT_ROWERS -> {
                val dao = RowerRoomDatabase
                    .getDatabase(application, CoroutineScope(Dispatchers.IO)).rowerDao()
                val viewAdapter = RowerViewAdapter(INVENTORY, dao)
                recyclerView.apply { adapter = viewAdapter }
                binding.fab.setOnClickListener { ItemDetailNavigation(this).goToNewRower() }
                setTitle(R.string.rower_inventory)
            }
        }
    }
}