package by.profs.rowgame.view.competition

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentCompetitionBinding
import by.profs.rowgame.presenter.competition.RaceCalculator
import by.profs.rowgame.presenter.dao.BoatDao
import by.profs.rowgame.presenter.dao.ComboDao
import by.profs.rowgame.presenter.dao.OarDao
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.adapters.StandingViewAdapter
import by.profs.rowgame.view.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompetitionFragment : Fragment(R.layout.fragment_competition) {
    private val args by navArgs<CompetitionFragmentArgs>()
    private var binding: FragmentCompetitionBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var raceCalculator: RaceCalculator

    private lateinit var comboDao: ComboDao
    private lateinit var boatDao: BoatDao
    private lateinit var oarDao: OarDao

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
    private lateinit var rating: ArrayList<Pair<Rower, Int>>

    private var raceBoats = mutableListOf<Boat>()
    private var raceOars = mutableListOf<Oar>()
    private var raceRowers = mutableListOf<Rower>()

    private var from = 0
    private var phase: Int = START
    private val race: Race = Race()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle(when (args.type) {
            CONCEPT -> R.string.concept
            OFP -> R.string.OFP
            else -> R.string.semifinal
        })
        binding = FragmentCompetitionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        raceCalculator = RaceCalculator(args.type)
        (requireActivity() as ActivityWithInfoBar).infoBar.nextAndShowDay()

        val database: MyRoomDatabase = ServiceLocator.locate()
        boatDao = database.boatDao()
        oarDao = database.oarDao()
        val rowerDao = database.rowerDao()
        comboDao = database.comboDao()

        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        CoroutineScope(Dispatchers.IO).launch {
            comboDao.getAllCombos().forEach { combo ->
                val rower = withContext(Dispatchers.IO) { rowerDao.search(combo.rowerId)!! }
                allBoats.add(withContext(Dispatchers.IO) { boatDao.search(combo.boatId)!! })
                allOars.add(withContext(Dispatchers.IO) { oarDao.search(combo.oarId)!! })
                allRowers.add(rower)
            }
            Race().setupRace()
        }

        binding?.buttonRaceFull?.setOnClickListener { race.showRace() }
        if (args.type == WATER) {
            binding?.buttonRace?.visibility = View.VISIBLE
            binding?.buttonRace?.setOnClickListener { race.shortRace() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun calculateRace(
        boats: List<Boat>,
        oars: List<Oar>,
        rowers: List<Rower>
    ) { rating = raceCalculator.calculateRace(boats, oars, rowers, rating) }

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
        reward()
        binding?.run {
            buttonRace.visibility = View.GONE
            buttonRaceFull.visibility = View.GONE
            buttonReward.visibility = View.VISIBLE
            buttonReward.setOnClickListener {
                findNavController().navigate(R.id.action_competitionFragment_to_trainingFragment)
            }
        }
    }

    private fun showToastResults(rowers: List<Rower>) {
        val context = requireContext()
        context.showToast(context.getString(
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
        val prefEditor = PreferenceEditor(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            val myRowers = comboDao.getRowerIds()
            if (myRowers.contains(finalists!![FIRST].first.id)) {
                boatDao.insert(Randomizer.getRandomBoat())
                prefEditor.setFame(prefEditor.getFame() + fameForWin)
            }
            if (myRowers.contains(finalists!![SECOND].first.id))
                oarDao.insert(Randomizer.getRandomOar())
            if (myRowers.contains(finalists!![THIRD].first.id))
                oarDao.insert(Randomizer.getRandomOar())
        }
    }

    private fun addRandomRowers(howMany: Int) {
        val minSkill = 2 + (0..maxMinSkill).random()
        allRowers.addAll(List(howMany) { Randomizer.getRandomRower(
            minSkill = minSkill,
            maxSkill = minSkill * maxSkillCoef
        )
        })
    }

    private fun sortedRating() =
        ArrayList(rating.sortedBy { if (args.type == OFP) -it.second else it.second })

    inner class Race {
        fun setupRace() {
            rating = ArrayList()
            if (args.type == WATER) {
                when (requireActivity().title as String) {
                    getString(R.string.semifinal) -> {
                        val to = from + raceSize
                        val free = to - allBoats.size
                        if (free > 0) {
                            allBoats.addAll(List(free) { Randomizer.getRandomBoat() })
                            allOars.addAll(List(free) { Randomizer.getRandomOar() })
                            addRandomRowers(free)
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
                }
                raceRowers.forEach { rower -> rating.add(Pair(rower, 0)) }
                MainScope().launch {
                    val viewAdapter = ComboViewAdapter(raceBoats, raceOars, raceRowers)
                    recyclerView.apply { adapter = viewAdapter }
                }
            } else {
                addRandomRowers(totalRowers - allRowers.size)
                raceRowers = allRowers
                for (i in raceRowers.indices) rating.add(Pair(raceRowers[i], 0))
                MainScope().launch {
                    val viewAdapter = StandingViewAdapter(rating, StandingViewAdapter.RESULTS)
                    recyclerView.apply { adapter = viewAdapter }
                }
            }
        }

        internal fun showRace() {
            changeRaceTitle()
            when {
                phase == START && args.type == WATER -> {
                        binding?.buttonRace?.visibility = View.GONE
                        binding?.buttonRaceFull?.visibility = View.GONE }
                phase == FINISH -> binding?.buttonRaceFull?.visibility = View.VISIBLE
                phase > FINISH -> {
                    phase = START
                    if (args.type == WATER) binding?.buttonRace?.visibility = View.VISIBLE
                    endRace()
                    return
                }
            }
            calculateRace(raceBoats, raceOars, raceRowers)
            phase += phaseLength
            recyclerView.apply { adapter = StandingViewAdapter(sortedRating(),
                if (args.type == OFP) StandingViewAdapter.SCORE else StandingViewAdapter.RACE)
            }
            if (args.type == WATER && phase <= FINISH)
                Handler(Looper.getMainLooper()).postDelayed({ showRace() }, delay)
        }

        private fun endRace() {
            rating = sortedRating()
            if (args.type == WATER) {
                if (from <= totalRowers) {
                    calculateSemifinal(raceBoats, raceOars, raceRowers)
                    showToastResults(rating.map { it.first })
                    from += raceSize
                    requireActivity().title =
                        if (from > totalRowers) getString(R.string.final_, 'B')
                        else getString(R.string.semifinal)
                    setupRace()
                } else if (finalists == null) {
                    finalists = ArrayList(rating)
                    showToastResults(rating.map { it.first })
                    requireActivity().title = getString(R.string.final_, 'A')
                    setupRace()
                } else {
                    finalists!!.addAll(0, rating) // not-null check higher
                    showResults()
                }
            } else {
                finalists = rating
                showResults()
            }
        }

        internal fun shortRace() {
            for (i in 1..shortRaceIterations) calculateRace(raceBoats, raceOars, raceRowers)
            endRace()
        }

        private fun changeRaceTitle() = requireActivity().setTitle(when (phase) {
            START -> if (args.type == OFP) R.string.phase_tyaga else R.string.phase_start
            HALF -> if (args.type == OFP) R.string.phase_jumping else R.string.phase_half
            ONE_AND_HALF ->
                if (args.type == OFP) R.string.phase_jump_series else R.string.phase_one_and_half
            else -> if (args.type == OFP) R.string.phase_running else R.string.phase_finish
        })
    }

    companion object {
        private const val phaseLength = 500
        const val raceSize = 6
        private const val totalRowers = 30 // 6 starts
        private const val fameForWin = 4
        private const val shortRaceIterations = 4 // for more precise results
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

        private const val maxMinSkill = 16
        private const val maxSkillCoef = 4
        private const val delay = 650L
        // Type of competition
        const val CONCEPT = 1
        const val OFP = 2
        const val WATER = 3
    }
}