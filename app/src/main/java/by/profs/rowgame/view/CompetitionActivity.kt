package by.profs.rowgame.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.PreferenceEditor
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.databinding.ActivityCompetitionBinding
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.utils.NumberGenerator.generatePositiveIntOrNull
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.adapters.CompetitionViewAdapter
import by.profs.rowgame.view.adapters.FinalStandingViewAdapter
import by.profs.rowgame.view.utils.HelperFuns
import kotlin.collections.ArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompetitionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompetitionBinding
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var boatDao: BoatDao
    private lateinit var oarDao: OarDao
    private lateinit var singleComboDao: SingleComboDao

    private val allBoats = mutableListOf<Boat>()
    private val allOars = mutableListOf<Oar>()
    private val allRowers = mutableListOf<Rower>()
    private val finalABoats = mutableListOf<Boat>()
    private val finalAOars = mutableListOf<Oar>()
    private val finalARowers = mutableListOf<Rower>()
    private val finalBBoats = mutableListOf<Boat>()
    private val finalBOars = mutableListOf<Oar>()
    private val finalBRowers = mutableListOf<Rower>()
    private var finalists: ArrayList<Rower>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, MODE_PRIVATE))

        if (prefEditor.getDay() % raceDay != 0) {
            setContentView(R.layout.error_network_layout)
            findViewById<TextView>(R.id.error).text = getString(R.string.error_wrong_day)
            return
        }

        prefEditor.nextDay()
        binding = ActivityCompetitionBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(this)

        setContentView(binding.root)

        boatDao = BoatRoomDatabase.getDatabase(this, scope).boatDao()
        oarDao = OarRoomDatabase.getDatabase(this, scope).oarDao()
        val rowerDao = RowerRoomDatabase.getDatabase(this, scope).rowerDao()
        singleComboDao = SingleComboRoomDatabase.getDatabase(this, scope).singleComboDao()
        var from = 0

        recyclerView = findViewById<RecyclerView>(R.id.list).apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
        }

        CoroutineScope(Dispatchers.IO).launch {
            singleComboDao.getAllCombos().forEach { combo ->
                allBoats.add(withContext(Dispatchers.IO) { boatDao.search(combo.boatId)[0] })
                allOars.add(withContext(Dispatchers.IO) { oarDao.search(combo.oarId)[0] })
                allRowers.add(withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId)[0] })
            }

            semifinal(from)
        }

        binding.buttonRace.setOnClickListener {
            if (from < totalRowers) {
                val to = from + raceSize
                calculateSemifinal(
                    allBoats.subList(from, to),
                    allOars.subList(from, to),
                    allRowers.subList(from, to))
                from = to
                semifinal(from)
            } else if (from == totalRowers) {
                val to = from + raceSize
                calculateSemifinal(
                    allBoats.subList(from, to),
                    allOars.subList(from, to),
                    allRowers.subList(from, to))
                final('B')
                from ++
            } else if (finalists == null) {
                finalists = calculateFinal(finalBBoats, finalBOars, finalBRowers)
                final('A')
            } else { // not-null check higher
                finalists!!.addAll(0, calculateFinal(finalABoats, finalAOars, finalARowers))
                showResults()
                CoroutineScope(Dispatchers.IO).launch { reward() }
            }
        }
    }

    private fun calculateFinal(boats: List<Boat>, oars: List<Oar>, rowers: List<Rower>):
            ArrayList<Rower> {
        val rating = calculateRace(boats, oars, rowers)
        showToastResults(rating.map { it.first })
        return ArrayList(rating.map { it.first })
    }

    private fun calculateSemifinal(boats: List<Boat>, oars: List<Oar>, rowers: List<Rower>) {
        val rating = calculateRace(boats, oars, rowers).sortedBy { it.second }
        var finalistA = 0
        var finalistB = 0
        while (rowers[finalistA].name != rating[FIRST].first.name) finalistA++
        while (rowers[finalistB].name != rating[SECOND].first.name) finalistB++
        finalABoats.add(boats[finalistA])
        finalAOars.add(oars[finalistA])
        finalARowers.add(rowers[finalistA])
        finalBBoats.add(boats[finalistB])
        finalBOars.add(oars[finalistB])
        finalBRowers.add(rowers[finalistB])
        showToastResults(rating.map { it.first })
    }

    private fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>,
        rating: ArrayList<Pair<Rower, Int>> = ArrayList()
    ): ArrayList<Pair<Rower, Int>> {

        if (rating.isEmpty()) for (i in rowers.indices) rating.add(Pair(rowers[i], 0))
        val distances = IntArray(raceSize) { generatePositiveIntOrNull(MAX_GAP) }
        val chances: MutableList<Int> = MutableList(raceSize) { pos ->
            calculatePower(boats[pos], oars[pos], rowers[pos])
        }
        distances.sort()
        var j = 0
        var total = chances.sum()
        while (total > 0) {
            var rand = generatePositiveIntOrNull(total)
            var i = -1
            while (rand >= 0) {
                i++
                rand -= chances[i]
            }
            rating[i] = Pair(rowers[i], rating[i].second + distances[j])
            total -= chances[i]
            chances[i] = 0
            j++
        }
        return rating
    }

    private fun calculatePower(boat: Boat, oar: Oar, rower: Rower) = rower.power + rower.technics +
            rower.endurance + (boat.weight + boat.wing + oar.blade + oar.weight) * BOAT_OAR_COEF

    private fun final(char: Char) {
        MainScope().launch { when (char.toUpperCase()) {
            'A' -> {
                val viewAdapter = CompetitionViewAdapter(finalABoats, finalAOars, finalARowers)
                recyclerView.apply { adapter = viewAdapter }
                setTitle(R.string.final_a)
            }
            'B' -> {
                val viewAdapter = CompetitionViewAdapter(finalBBoats, finalBOars, finalBRowers)
                recyclerView.apply { adapter = viewAdapter }
                setTitle(R.string.final_b)
            }
            }
        }
    }

    private fun semifinal(from: Int) {
        val to = from + raceSize
        val free = to - allBoats.size
        CoroutineScope(Dispatchers.Default).launch {
            allBoats.addAll(List(free) { Randomizer.getRandomBoat() })
            allOars.addAll(List(free) { Randomizer.getRandomOar() })
            allRowers.addAll(List(free) { Randomizer.getRandomRower() })

            MainScope().launch {
                val viewAdapter = CompetitionViewAdapter(
                    allBoats.subList(from, to),
                    allOars.subList(from, to),
                    allRowers.subList(from, to)
                )
                recyclerView.apply { adapter = viewAdapter }
            }
        }
    }

    private fun showResults() {
        recyclerView.apply { adapter = FinalStandingViewAdapter(finalists!!) }
        setTitle(R.string.results)
        binding.buttonRace.visibility = View.GONE
    }

    private fun showToastResults(rowers: List<Rower>) {
        HelperFuns.showToast(this, this.getString(
            R.string.race_results_list,
            rowers[FIRST].name,
            rowers[SECOND].name,
            rowers[THIRD].name,
            rowers[FOURTH].name,
            rowers[FIFTH].name,
            rowers[SIXTH].name
        ))
    }

    private fun reward() {
        if (finalists == null) return
        val myRowers = singleComboDao.getRowerIds()
        if (myRowers.contains(finalists!![0].name)) {
            boatDao.insert(Randomizer.getRandomBoat())
            prefEditor.setFame(prefEditor.getFame() + 1)
        }
        if (myRowers.contains(finalists!![1].name)) oarDao.insert(Randomizer.getRandomOar())
        if (myRowers.contains(finalists!![2].name)) oarDao.insert(Randomizer.getRandomOar())
    }

    companion object {
        const val BOAT_OAR_COEF = 10
        const val MAX_GAP = 25 // max increase in distance between leader and last boat every 500 m
        const val raceDay = 30
        const val raceSize = 6
        const val totalRowers = 30 // 6 starts
        // Numeration in rowers array
        const val FIRST = 0
        const val SECOND = 1
        const val THIRD = 2
        const val FOURTH = 3
        const val FIFTH = 4
        const val SIXTH = 5
    }
}