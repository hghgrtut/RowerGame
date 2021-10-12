package by.profs.rowgame.view.fragments.competition

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.competition.License
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.databinding.FragmentCompetitionBinding
import by.profs.rowgame.presenter.competition.AbstractCompetition
import by.profs.rowgame.presenter.competition.AbstractCompetition.Companion.isOFPCompetition
import by.profs.rowgame.presenter.competition.AbstractCompetition.Companion.isWaterCompetition
import by.profs.rowgame.presenter.competition.ConceptCompetition
import by.profs.rowgame.presenter.competition.OFPCompetition
import by.profs.rowgame.presenter.competition.WaterCompetition
import by.profs.rowgame.presenter.competition.WaterCompetition.Companion.FINAL_A
import by.profs.rowgame.presenter.competition.WaterCompetition.Companion.FINAL_B
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.infobar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.adapters.StandingViewAdapter
import by.profs.rowgame.view.fragments.extensions.enableClick
import by.profs.rowgame.view.fragments.extensions.makeInvisible
import by.profs.rowgame.view.fragments.extensions.setup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompetitionFragment : Fragment(R.layout.fragment_competition) {
    private var _binding: FragmentCompetitionBinding? = null
    private val binding: FragmentCompetitionBinding get() = requireNotNull(_binding)
    private lateinit var recyclerView: RecyclerView

    private var finalists: ArrayList<Rower> = ArrayList()

    private val competitionDao: CompetitionDao = ServiceLocator.locate()
    private lateinit var competition: CompetitionInfo
    private lateinit var someCompetition: AbstractCompetition
    private lateinit var myRowers: List<Int>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompetitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainScope().launch {
            withContext(Dispatchers.IO) {
                competition = competitionDao.search(requireActivity().infobar().getDay())
                myRowers = ServiceLocator.get(ComboDao::class).getRowerIds()
            }
            someCompetition = when (competition.type) {
                AbstractCompetition.CONCEPT -> ConceptCompetition(competition)
                AbstractCompetition.OFP -> OFPCompetition(competition)
                else -> WaterCompetition(competition)
            }
            (requireActivity() as ActivityWithInfoBar).apply {
                infoBar.nextAndShowDay()
                setSubtitle(competition.getMainInfo().toString())
            }

            recyclerView = binding.list.setup()
            beforeRace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showResults() {
        recyclerView.adapter = StandingViewAdapter(finalists, StandingViewAdapter.RESULTS)
        requireActivity().setTitle(R.string.results)
        val infobar = requireActivity().infobar()
        CoroutineScope(Dispatchers.IO).launch {
            val rewarder = Rewarder(finalists, competition)
            withContext(Dispatchers.Main) { infobar.changeFame(rewarder.calculateFame()) }
            rewarder.giveItems()
            val prize = rewarder.giveLicensesAndCalculateMoney()
            MainScope().launch { infobar.changeMoney(prize) }
        }
        hideRaceButtons()
        binding.buttonReward.enableClick {
            findNavController().navigate(R.id.action_competitionFragment_to_trainingFragment)
            (requireActivity() as ActivityWithInfoBar).setSubtitle("")
        }
    }

    private fun beforeRace() = MainScope().launch {
        withContext(Dispatchers.IO) { someCompetition.setupRace() }
        requireActivity().title = someCompetition.raceTitle()
        val raceRowers = ArrayList(someCompetition.getRaceRowers())
        (someCompetition as? WaterCompetition)?.let {
            recyclerView.adapter =
                ComboViewAdapter(it.getRaceBoats(), it.getRaceOars(), raceRowers, myRowers)
            binding.buttonRaceFull.setOnClickListener { showRace() }
            binding.buttonRace.enableClick {
                while (someCompetition.raceCalculator!!.phase < AbstractCompetition.FINISH) {
                    it.calculateRace() }
                endRace(true)
            }
        } ?: recyclerView.setAdapter(StandingViewAdapter(raceRowers, StandingViewAdapter.RESULTS))
        binding.buttonRaceFull.setOnClickListener { showRace() }
    }

    private fun showRace() {
        val isWater = competition.type.isWaterCompetition()
        when (someCompetition.raceCalculator!!.phase) {
            AbstractCompetition.BEFORE -> if (isWater) hideRaceButtons()
            AbstractCompetition.FINISH -> {
                endRace()
                return
            }
        }
        someCompetition.calculateRace()
        recyclerView.setViewAdapter(
            if (competition.type.isOFPCompetition()) StandingViewAdapter.SCORE
            else StandingViewAdapter.RACE
        )
        requireActivity().title = someCompetition.raceTitle()
        if (isWater && someCompetition.raceCalculator!!.phase <= AbstractCompetition.FINISH) {
            Handler(Looper.getMainLooper()).postDelayed({ showRace() }, delay) }
    }

    private fun endRace(isShort: Boolean = false) {
        val rating = ArrayList(getRating().map { it.first })
        (someCompetition as? WaterCompetition)?.let {
            when (it.raceNumber) {
                FINAL_B -> finalists = rating
                FINAL_A -> {
                    finalists.addAll(0, rating)
                    showResults()
                    return
                }
                else -> it.calculateSemifinal(rating)
            }
            it.raceNumber++
            if (isShort) beforeRace() else binding.buttonRaceFull.enableClick { beforeRace() }
        } ?: run {
            finalists = rating
            showResults()
        }
    }

    private fun hideRaceButtons() =
        listOf(binding.buttonRace, binding.buttonRaceFull).forEach { it.makeInvisible() }

    private fun RecyclerView.setViewAdapter(mode: Int) {
        val rating = getRating()
        adapter = StandingViewAdapter(
            ArrayList(rating.map { it.first }),
            mode,
            ArrayList(rating.map { it.second }))
    }

    private fun getRating() = someCompetition.raceCalculator!!.sortedRating()

    companion object {
        private const val delay = 650L

        // for Rewarder class
        private const val fameForWin = 4
        private const val quota = 7
        private const val basicPrize = 500
        private const val FIRST = 0
        private const val SECOND = 1
        private const val THIRD = 2
    }

    inner class Rewarder(private val standing: ArrayList<Rower>, private val competition: CompetitionInfo) {
        fun calculateFame(): Int = if (isMine(FIRST)) fameForWin else 0

        fun giveItems() {
            if (isMine(FIRST)) ServiceLocator.get(BoatDao::class).insert(Randomizer.getRandomBoat())
            if (isMine(SECOND)) giveRandomOar()
            if (isMine(THIRD)) giveRandomOar()
        }

        suspend fun giveLicensesAndCalculateMoney(): Int {
            val competitionDao = ServiceLocator.get(CompetitionDao::class)
            var totalPrize = 0
            var rewardForPlace = basicPrize * competition.level * competition.age
            withContext(Dispatchers.IO) {
                standing.take(quota).forEach {
                    val rowerId = it.id
                    if (rowerId != null && myRowers.contains(rowerId)) {
                        competitionDao.addLicenses(
                            listOf(
                                License(null, rowerId, competition.level + 1, competition.age),
                                License(null, rowerId, competition.level, competition.age + 1)
                            )
                        )
                        totalPrize += rewardForPlace
                    }
                    rewardForPlace /= 2
                }
            }
            return totalPrize
        }

        private fun isMine(position: Int) = myRowers.contains(standing[position].id)
        private fun giveRandomOar() = ServiceLocator.get(OarDao::class).insert(Randomizer.getRandomOar())
    }
}