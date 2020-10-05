package by.profs.rowgame.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.PreferenceEditor
import by.profs.rowgame.databinding.ActivityTrainingBinding
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.utils.TRAIN_ALL
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.adapters.PairViewAdapter
import by.profs.rowgame.view.utils.HelperFuns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var prefEditor: PreferenceEditor
    private lateinit var recyclerView: RecyclerView
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, MODE_PRIVATE))
        binding = ActivityTrainingBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(this)
        setContentView(binding.root)
        showDay()

        val boatDao = BoatRoomDatabase.getDatabase(this, scope).boatDao()
        val oarDao = OarRoomDatabase.getDatabase(this, scope).oarDao()
        val rowerDao = RowerRoomDatabase.getDatabase(this, scope).rowerDao()
        val singleComboDao = SingleComboRoomDatabase.getDatabase(this, scope).singleComboDao()

        val viewAdapter = PairViewAdapter(boatDao, oarDao, rowerDao, singleComboDao)
        recyclerView = findViewById<RecyclerView>(R.id.list).apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
            adapter = viewAdapter
        }

        binding.buttonTrainAll.setOnClickListener { train(viewAdapter, TRAIN_ALL) }
        binding.buttonTrainEndurance.setOnClickListener { train(viewAdapter, TRAIN_ENDURANCE) }
        binding.buttonTrainPower.setOnClickListener { train(viewAdapter, TRAIN_POWER) }
        binding.buttonTrainTechnicality.setOnClickListener { train(viewAdapter, TRAIN_TECHNICALITY) }
    }

    fun showDay() { binding.day.text = this.getString(R.string.day, prefEditor.getDay()) }

    fun train(viewAdapter: PairViewAdapter, mode: Int = TRAIN_ALL) {
        scope.launch { viewAdapter.startTraining(mode) }
        prefEditor.nextDay()
        showDay()
        HelperFuns.showToast(this, R.string.train_sucess)
    }
}