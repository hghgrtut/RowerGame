package by.profs.rowgame.view.shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.ActivityShopBinding
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.utils.ITEM
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.SHOP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, MODE_PRIVATE))
        binding = ActivityShopBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(this)
        setContentView(binding.root)

        if (intent.extras?.getInt(ITEM) == INTENT_OARS) {
            val dao =
                OarRoomDatabase.getDatabase(application, CoroutineScope(Dispatchers.IO)).oarDao()
            val viewAdapter = OarViewAdapter(SHOP, prefEditor, dao)
            recyclerView = findViewById<RecyclerView>(R.id.list).apply {
                setHasFixedSize(true)
                adapter = viewAdapter
                this.layoutManager = layoutManager
            }
            setTitle(R.string.oar_shop)
        } else {
            val dao =
                BoatRoomDatabase.getDatabase(application, CoroutineScope(Dispatchers.IO)).boatDao()
            val viewAdapter = BoatViewAdapter(SHOP, prefEditor, dao)
            recyclerView = findViewById<RecyclerView>(R.id.list).apply {
                setHasFixedSize(true)
                adapter = viewAdapter
                this.layoutManager = layoutManager
            }
            setTitle(R.string.boat_shop)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.money.text = this.getString(R.string.money_balance, prefEditor.getBalance())
    }
}