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
import by.profs.rowgame.presenter.competition.RaceCalculator.calculateRace
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.adapters.CompetitionViewAdapter
import by.profs.rowgame.view.adapters.StandingViewAdapter
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
    private var finalists: ArrayList<Pair<Rower, Int>>? = null
    private var rating: ArrayList<Pair<Rower, Int>> = ArrayList()

    private lateinit var raceBoats: MutableList<Boat>
    private lateinit var raceOars: MutableList<Oar>
    private lateinit var raceRowers: MutableList<Rower>

    private var from = 0
    private var phase: Int = START
    private val race: Race = Race()
    private var competitionNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, MODE_PRIVATE))

        val day = prefEditor.getDay()
        if (day % raceDay != 0) {
            setContentView(R.layout.error_network_layout)
            findViewById<TextView>(R.id.error).text = getString(R.string.error_wrong_day)
            return
        }
        competitionNum = day / raceDay

        prefEditor.nextDay()
        binding = ActivityCompetitionBinding.inflate(layoutInflater)
        layoutManager = LinearLayoutManager(this)

        setContentView(binding.root)

        boatDao = BoatRoomDatabase.getDatabase(this, scope).boatDao()
        oarDao = OarRoomDatabase.getDatabase(this, scope).oarDao()
        val rowerDao = RowerRoomDatabase.getDatabase(this, scope).rowerDao()
        singleComboDao = SingleComboRoomDatabase.getDatabase(this, scope).singleComboDao()

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

            newSemifinal()
        }

        binding.buttonRace.setOnClickListener { race.raceShort() }
        binding.buttonRaceFull.setOnClickListener { race.raceFull() }
    }

    private fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>
    ) {
        rating = ArrayList(calculateRace(boats, oars, rowers, rating))
        val excessGap: Int = rating.map { it.second }.minOrNull()!!
        rating.forEachIndexed { index, it -> rating[index] = Pair(it.first, it.second - excessGap) }
    }

    private fun calculateSemifinal(boats: List<Boat>, oars: List<Oar>, rowers: List<Rower>) {
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
    }

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

    private fun newSemifinal() {
        MainScope().launch { setTitle(R.string.semifinal) }
        val to = from + raceSize
        val free = to - allBoats.size
        CoroutineScope(Dispatchers.Default).launch {
            if (free > 0) {
                allBoats.addAll(List(free) { Randomizer.getRandomBoat() })
                allOars.addAll(List(free) { Randomizer.getRandomOar() })
                allRowers.addAll(List(free) { Randomizer.getRandomRower(
                    minSkill = competitionNum, maxSkill = competitionNum * maxSkillCoef) })
            }

            raceBoats = allBoats.subList(from, to)
            raceOars = allOars.subList(from, to)
            raceRowers = allRowers.subList(from, to)

            MainScope().launch {
                val viewAdapter = CompetitionViewAdapter(raceBoats, raceOars, raceRowers)
                recyclerView.apply { adapter = viewAdapter }
            }
        }
    }

    private fun showResults() {
        recyclerView.apply {
            adapter = StandingViewAdapter(finalists!!, StandingViewAdapter.RESULTS) }
        setTitle(R.string.results)
        binding.buttonRace.visibility = View.GONE
        binding.buttonRaceFull.visibility = View.GONE
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
        CoroutineScope(Dispatchers.IO).launch {
            val myRowers = singleComboDao.getRowerIds()
            if (myRowers.contains(finalists!![FIRST].first.name)) {
                boatDao.insert(Randomizer.getRandomBoat())
                prefEditor.setFame(prefEditor.getFame() + competitionNum / 2)
            }
            if (myRowers.contains(finalists!![SECOND].first.name)) {
                oarDao.insert(Randomizer.getRandomOar())
            }
            if (myRowers.contains(finalists!![THIRD].first.name)) {
                oarDao.insert(Randomizer.getRandomOar())
            }
        }
    }

    inner class Race {
        private fun setupRace() {
            when (title as String) {
                getString(R.string.semifinal) -> {
                    val to = from + raceSize
                    raceBoats = allBoats.subList(from, to)
                    raceOars = allOars.subList(from, to)
                    raceRowers = allRowers.subList(from, to)
                }
                getString(R.string.final_b) -> {
                    raceBoats = finalBBoats
                    raceOars = finalBOars
                    raceRowers = finalBRowers
                }
                getString(R.string.final_a) -> {
                    raceBoats = finalABoats
                    raceOars = finalAOars
                    raceRowers = finalARowers
                }
                else -> throw IllegalArgumentException("Title not recognized: $title")
            }
        }

        internal fun raceFull() {
            when (phase) {
                START -> {
                    rating = ArrayList()
                    binding.buttonRace.visibility = View.GONE
                    setupRace()
                    setTitle(R.string.phase_start)
                }
                HALF -> setTitle(R.string.phase_half)
                ONE_AND_HALF -> setTitle(R.string.phase_one_and_half)
                FINISH -> setTitle(R.string.phase_finish)
                else -> {
                    phase = START
                    binding.buttonRace.visibility = View.VISIBLE
                    raceCommon()
                    return
                }
            }
            calculateRace(raceBoats, raceOars, raceRowers)
            recyclerView.apply { adapter = StandingViewAdapter(
                    ArrayList(rating.sortedBy { it.second }), StandingViewAdapter.RACE) }
            if (phase == 0) rating = ArrayList()
            phase += phaseLenght
        }

        internal fun raceShort() {
            rating = ArrayList()
            setupRace()
            calculateRace(raceBoats, raceOars, raceRowers)
            raceCommon()
        }

        private fun raceCommon() {
            rating.sortBy { it.second }
            if (from <= totalRowers) {
                calculateSemifinal(raceBoats, raceOars, raceRowers)
                showToastResults(rating.map { it.first })
                from += raceSize
                if (from <= totalRowers) newSemifinal() else final('B')
            } else if (finalists == null) {
                finalists = rating
                showToastResults(finalists!!.map { it.first })
                final('A')
            } else {
                finalists!!.addAll(0, rating) // not-null check higher
                showResults()
                CoroutineScope(Dispatchers.IO).launch { reward() }
            }
        }
    }

    companion object {
        private const val phaseLenght = 500
        private const val raceDay = 30
        const val raceSize = 6
        private const val totalRowers = 30 // 6 starts
        // Numeration in rowers array
        private const val FIRST = 0
        private const val SECOND = 1
        private const val THIRD = 2
        private const val FOURTH = 3
        private const val FIFTH = 4
        private const val SIXTH = 5
        // Phases
        private const val START = phaseLenght
        private const val HALF = phaseLenght * 2
        private const val ONE_AND_HALF = phaseLenght * 3
        private const val FINISH = phaseLenght * 4

        private const val maxSkillCoef = 4
    }
}