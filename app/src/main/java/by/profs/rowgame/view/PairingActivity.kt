package by.profs.rowgame.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.PreferenceEditor
import by.profs.rowgame.databinding.ActivityPairingBinding
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.utils.ITEM
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.PAIRING
import by.profs.rowgame.view.adapters.RowerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PairingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPairingBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairingBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(this)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE))
        setContentView(binding.root)
        recyclerView = findViewById<RecyclerView>(R.id.list).apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
        }
        val intentType = intent.extras?.getInt(ITEM)
        when (intentType) {
            INTENT_BOATS -> choosingBoat()
            INTENT_OARS -> choosingOar()
            INTENT_ROWERS -> choosingRower()
        }
    }

    private fun choosingBoat() {
        val dao = BoatRoomDatabase.getDatabase(application, scope).boatDao()
        val viewAdapter = BoatViewAdapter(PAIRING, prefEditor, dao, getComboDao())
        recyclerView.apply { adapter = viewAdapter }
        setTitle(R.string.choose_boat)
    }

    private fun choosingRower(number: Int = 1) {
        val dao = RowerRoomDatabase.getDatabase(application, scope).rowerDao()
        val viewAdapter = RowerViewAdapter(PAIRING, dao, getComboDao())
        recyclerView.apply { adapter = viewAdapter }
        setTitle(R.string.choose_rower)
    }

    private fun choosingOar(number: Int = 1) {
        val dao = OarRoomDatabase.getDatabase(application, scope).oarDao()
        val viewAdapter = OarViewAdapter(PAIRING, prefEditor, dao, getComboDao())
        recyclerView.apply { adapter = viewAdapter }
        setTitle(R.string.choose_oar)
    }

    private fun getComboDao() =
        SingleComboRoomDatabase.getDatabase(application, scope).singleComboDao()
}