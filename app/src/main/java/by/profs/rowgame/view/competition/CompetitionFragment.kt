package by.profs.rowgame.view.competition

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.CompetitionInfo
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.databinding.FragmentCompetitionBinding
import by.profs.rowgame.presenter.competition.Rewarder
import by.profs.rowgame.presenter.competition.type.AbstractCompetition
import by.profs.rowgame.presenter.competition.type.AbstractCompetition.Companion.isOFPCompetition
import by.profs.rowgame.presenter.competition.type.AbstractCompetition.Companion.isWaterCompetition
import by.profs.rowgame.presenter.competition.type.ConceptCompetition
import by.profs.rowgame.presenter.competition.type.OFPCompetition
import by.profs.rowgame.presenter.competition.type.WaterCompetition
import by.profs.rowgame.presenter.competition.type.WaterCompetition.Companion.FINAL_A
import by.profs.rowgame.presenter.competition.type.WaterCompetition.Companion.FINAL_B
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.infobar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.adapters.StandingViewAdapter
import by.profs.rowgame.view.extensions.enableClick
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
            competition = withContext(Dispatchers.IO) {
                competitionDao.search((requireActivity() as ActivityWithInfoBar).infoBar.getDay()) }
            someCompetition = when (competition.type) {
                AbstractCompetition.CONCEPT -> ConceptCompetition(competition)
                AbstractCompetition.OFP -> OFPCompetition(competition)
                else -> WaterCompetition(competition)
            }
            (requireActivity() as ActivityWithInfoBar).apply {
                infoBar.nextAndShowDay()
                setSubtitle(competition.getMainInfo().toString())
            }

            recyclerView = binding.list.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(ServiceLocator.locate())
            }

            withContext(Dispatchers.IO) { someCompetition.initCompetitors() }
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
        reward()
        hideRaceButtons()
        binding.buttonReward.enableClick {
            findNavController().navigate(R.id.action_competitionFragment_to_trainingFragment)
            (requireActivity() as ActivityWithInfoBar).setSubtitle("")
        }
    }

    private fun reward() {
        val infobar = requireActivity().infobar()
        val rewarder = Rewarder(finalists, competition)
        infobar.changeFame(rewarder.calculateFame())
        rewarder.giveItems()
        MainScope().launch {
            val prize = rewarder.giveLicensesAndCalculateMoney()
            infobar.changeMoney(prize)
        }
    }

    private fun beforeRace() {
        someCompetition.phase = AbstractCompetition.BEFORE
        requireActivity().title = someCompetition.raceTitle()
        (someCompetition as? WaterCompetition)?.let {
            it.setupRace()
            recyclerView.adapter =
                ComboViewAdapter(it.getRaceBoats(), it.getRaceOars(), it.getRaceRowers())
            binding.buttonRaceFull.setOnClickListener { showRace() }
            binding.buttonRace.enableClick {
                while (someCompetition.phase < AbstractCompetition.FINISH) it.calculateRace()
                endRace(true)
            }
        } ?: run {
            val rowers = ArrayList(someCompetition.getRaceRowers())
            recyclerView.adapter = StandingViewAdapter(rowers, StandingViewAdapter.RESULTS)
        }
        binding.buttonRaceFull.setOnClickListener { showRace() }
    }

    private fun showRace() {
        val isWater = competition.type.isWaterCompetition()
        when {
            someCompetition.phase == AbstractCompetition.BEFORE && isWater -> hideRaceButtons()
            someCompetition.phase == AbstractCompetition.FINISH -> {
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
        if (isWater && someCompetition.phase <= AbstractCompetition.FINISH) {
            Handler(Looper.getMainLooper()).postDelayed({ showRace() }, delay) }
    }

    private fun endRace(isShort: Boolean = false) {
        val rating = someCompetition.raceCalculator!!.getStanding()
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

    private fun hideRaceButtons() = binding.apply {
        buttonRace.visibility = View.GONE
        buttonRaceFull.visibility = View.GONE
    }

    private fun RecyclerView.setViewAdapter(mode: Int) {
        val rating = someCompetition.raceCalculator!!.sortedRating()
        adapter = StandingViewAdapter(
            ArrayList(rating.map { it.first }),
            mode,
            ArrayList(rating.map { it.second }))
    }

    companion object {
        private const val delay = 650L
    }
}