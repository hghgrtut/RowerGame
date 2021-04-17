package by.profs.rowgame.view.competition

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentCompetitionBinding
import by.profs.rowgame.presenter.competition.RaceCalculator
import by.profs.rowgame.presenter.competition.WaterRaceCalculator
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.dao.SingleComboDao
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.database.SingleComboRoomDatabase
import by.profs.rowgame.view.adapters.CompetitionViewAdapter
import by.profs.rowgame.view.adapters.StandingViewAdapter
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompetitionFragment : Fragment(R.layout.fragment_competition) {
    private var binding: FragmentCompetitionBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendar: Calendar
    private lateinit var prefEditor: PreferenceEditor
    private lateinit var raceCalculator: RaceCalculator
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle(R.string.semifinal)
        binding = FragmentCompetitionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        prefEditor = PreferenceEditor(context)
        val args by navArgs<CompetitionFragmentArgs>()
        raceCalculator = when (args.type) {
            else ->  WaterRaceCalculator
        }
        calendar = Calendar(context)
        val day = calendar.getDayOfYear()
        calendar.nextDay()

        competitionNum = day / raceDay
        boatDao = BoatRoomDatabase.getDatabase(context, scope).boatDao()
        oarDao = OarRoomDatabase.getDatabase(context, scope).oarDao()
        val rowerDao = RowerRoomDatabase.getDatabase(context, scope).rowerDao()
        singleComboDao = SingleComboRoomDatabase.getDatabase(context, scope).singleComboDao()

        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val globalDay = calendar.getGlobalDay()
            singleComboDao.getAllCombos().forEach { combo ->
                val rower = withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId)!! }
                if (rower.injury < globalDay) {
                    allBoats.add(withContext(Dispatchers.IO) { boatDao.search(combo.boatId)!! })
                    allOars.add(withContext(Dispatchers.IO) { oarDao.search(combo.oarId)!! })
                    allRowers.add(rower)
                }
            }
            Race().setupRace()
        }

        binding?.buttonRace?.setOnClickListener { race.raceShort() }
        binding?.buttonRaceFull?.setOnClickListener { race.raceFull() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>
    ) { rating = ArrayList(raceCalculator.calculateRace(boats, oars, rowers, rating)) }

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

    private fun showResults() {
        recyclerView.apply {
            adapter = StandingViewAdapter(finalists!!, StandingViewAdapter.RESULTS) }
        requireActivity().setTitle(R.string.results)
        binding?.buttonRace?.visibility = View.GONE
        binding?.buttonRaceFull?.visibility = View.GONE
    }

    private fun showToastResults(rowers: List<Rower>) {
        val context = requireContext()
        showToast(context, context.getString(
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
            if (myRowers.contains(finalists!![FIRST].first.id)) {
                boatDao.insert(Randomizer.getRandomBoat())
                prefEditor.setFame(prefEditor.getFame() + competitionNum / 2)
            }
            if (myRowers.contains(finalists!![SECOND].first.id)) {
                oarDao.insert(Randomizer.getRandomOar())
            }
            if (myRowers.contains(finalists!![THIRD].first.id)) {
                oarDao.insert(Randomizer.getRandomOar())
            }
        }
    }

    inner class Race {
         fun setupRace() {
            when (val title: String = requireActivity().title as String) {
                getString(R.string.semifinal) -> {
                    val to = from + raceSize
                    val free = to - allBoats.size
                    if (free > 0) {
                            allBoats.addAll(List(free) { Randomizer.getRandomBoat() })
                            allOars.addAll(List(free) { Randomizer.getRandomOar() })
                            allRowers.addAll(List(free) { Randomizer.getRandomRower(
                                    minSkill = competitionNum,
                                    maxSkill = competitionNum * maxSkillCoef) })
                    }
                    raceBoats = allBoats.subList(from, to)
                    raceOars = allOars.subList(from, to)
                    raceRowers = allRowers.subList(from, to)
                }
                getString(R.string.final_, 'B') -> {
                    raceBoats = finalBBoats
                    raceOars = finalBOars
                    raceRowers = finalBRowers
                }
                getString(R.string.final_, 'A') -> {
                    raceBoats = finalABoats
                    raceOars = finalAOars
                    raceRowers = finalARowers
                }
                else -> return
            }
            MainScope().launch {
                val viewAdapter = CompetitionViewAdapter(raceBoats, raceOars, raceRowers)
                recyclerView.apply { adapter = viewAdapter }
            }
        }

        internal fun raceFull() {
            when (phase) {
                START -> {
                    rating = ArrayList()
                    binding?.buttonRace?.visibility = View.GONE
                    binding?.buttonRaceFull?.visibility = View.GONE
                    requireActivity().setTitle(R.string.phase_start)
                }
                HALF -> requireActivity().setTitle(R.string.phase_half)
                ONE_AND_HALF -> requireActivity().setTitle(R.string.phase_one_and_half)
                FINISH -> {
                    requireActivity().setTitle(R.string.phase_finish)
                    binding?.buttonRaceFull?.visibility = View.VISIBLE
                }
                else -> {
                    phase = START
                    binding?.buttonRace?.visibility = View.VISIBLE
                    raceCommon()
                    setupRace()
                    return
                }
            }
            calculateRace(raceBoats, raceOars, raceRowers)
            recyclerView.apply { adapter = StandingViewAdapter(
                ArrayList(rating.sortedBy { it.second }), StandingViewAdapter.RACE) }
            if (phase == 0) rating = ArrayList()
            phase += phaseLength
            if (phase <= FINISH) Handler().postDelayed({ raceFull() }, delay)
        }

        internal fun raceShort() {
            rating = ArrayList()
            calculateRace(raceBoats, raceOars, raceRowers)
            raceCommon()
            setupRace()
        }

        private fun raceCommon() {
            rating.sortBy { it.second }
            if (from <= totalRowers) {
                calculateSemifinal(raceBoats, raceOars, raceRowers)
                showToastResults(rating.map { it.first })
                from += raceSize
                requireActivity().title =
                    if (from > totalRowers) getString(R.string.final_, 'B')
                    else getString(R.string.semifinal)
            } else if (finalists == null) {
                finalists = rating
                showToastResults(finalists!!.map { it.first })
                requireActivity().title = getString(R.string.final_, 'A')
            } else {
                finalists!!.addAll(0, rating) // not-null check higher
                showResults()
                CoroutineScope(Dispatchers.IO).launch { reward() }
            }
        }
    }

    companion object {
        private const val phaseLength = 500
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
        private const val START = phaseLength
        private const val HALF = phaseLength * 2
        private const val ONE_AND_HALF = phaseLength * 3
        private const val FINISH = phaseLength * 4

        private const val maxSkillCoef = 4
        private const val delay = 650L
    }
}