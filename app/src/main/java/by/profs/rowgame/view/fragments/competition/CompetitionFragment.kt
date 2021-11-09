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
import by.profs.rowgame.data.preferences.LevelEditor
import by.profs.rowgame.databinding.FragmentCompetitionBinding
import by.profs.rowgame.presenter.competition.type.AbstractCompetition
import by.profs.rowgame.presenter.competition.type.AbstractCompetition.Companion.isOFPCompetition
import by.profs.rowgame.presenter.competition.type.AbstractCompetition.Companion.isWaterCompetition
import by.profs.rowgame.presenter.competition.type.ConceptCompetition
import by.profs.rowgame.presenter.competition.type.OFPCompetition
import by.profs.rowgame.presenter.competition.type.WaterCompetition
import by.profs.rowgame.presenter.competition.type.WaterCompetition.Companion.FINAL_A
import by.profs.rowgame.presenter.competition.type.WaterCompetition.Companion.FINAL_B
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.mappers.ComboItemWrapper
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.infobar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.adapters.StandingViewAdapter
import by.profs.rowgame.view.fragments.extensions.enableClick
import by.profs.rowgame.view.fragments.extensions.makeInvisible
import by.profs.rowgame.view.fragments.extensions.setTitle
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
        setTitle(R.string.results)
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
        }
    }

    private fun beforeRace() = MainScope().launch {
        withContext(Dispatchers.IO) { someCompetition.setupRace() }
        val raceRowers = someCompetition.getRaceRowers()
        val f = someCompetition.changeStrategy
        (someCompetition as? WaterCompetition)?.let {
            val viewItems = ComboItemWrapper.map(it.getRaceBoats(), it.getRaceOars(), raceRowers)
            recyclerView.adapter = ComboViewAdapter(viewItems, myRowers, changeStrategyFun = f)
            binding.buttonRaceFull.setOnClickListener { showRace() }
            binding.buttonRace.enableClick {
                while (someCompetition.getRaceCalculator().phase < AbstractCompetition.FINISH) {
                    it.calculateRace() }
                endRace(true)
            }
        } ?: recyclerView.setAdapter(StandingViewAdapter(
            raceRowers, StandingViewAdapter.BEFORE, changeStrategyFun = f, myRowerIds = myRowers))
        binding.buttonRaceFull.setOnClickListener { showRace() }
        setCompetitionTitle()
    }

    private fun showRace() {
        _binding ?: return
        val isWater = competition.type.isWaterCompetition()
        when (someCompetition.getRaceCalculator().phase) {
            AbstractCompetition.BEFORE -> if (isWater) hideRaceButtons()
            AbstractCompetition.FINISH -> {
                endRace()
                return
            }
        }
        someCompetition.calculateRace()

        val rating = getRating()
        recyclerView.adapter = StandingViewAdapter(
            rating.map { it.first },
            if (competition.type.isOFPCompetition()) StandingViewAdapter.SCORE
            else StandingViewAdapter.RACE,
            ArrayList(rating.map { it.second })
        )
        setCompetitionTitle()
        if (isWater && someCompetition.getRaceCalculator().phase <= AbstractCompetition.FINISH)
            Handler(Looper.getMainLooper()).postDelayed({ showRace() }, delay)
    }

    private fun endRace(isShort: Boolean = false) {
        val rating = ArrayList(getRating().map { it.first })
        someCompetition.deleteRaceCalculator()
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
            if (isShort) beforeRace() else binding.buttonRaceFull.enableClick { beforeRace() }
        } ?: run {
            finalists = rating
            showResults()
        }
    }

    private fun hideRaceButtons() =
        listOf(binding.buttonRace, binding.buttonRaceFull).forEach { it.makeInvisible() }

    private fun getRating() = someCompetition.getRaceCalculator().sortedRating()

    private fun setCompetitionTitle() = setTitle(someCompetition.raceTitle())

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

    inner class Rewarder(
        private val standing: ArrayList<Rower>,
        private val competition: CompetitionInfo
    ) {
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
                for (position in 0 until quota) {
                    if (isMine(position)) {
                        val rowerId = standing[position].id!!
                        competitionDao.addLicenses(
                            listOf(
                                License(null, rowerId, competition.level + 1, competition.age),
                                License(null, rowerId, competition.level, competition.age + 1)
                            )
                        )
                        totalPrize += rewardForPlace
                        if (position <= THIRD) LevelEditor.trySet(competition.getCompetitionLevel())
                    }
                    rewardForPlace /= 2
                }
            }
            return totalPrize
        }

        private fun isMine(position: Int) = myRowers.contains(standing[position].id)
        private fun giveRandomOar() =
            ServiceLocator.get(OarDao::class).insert(Randomizer.getRandomOar())
    }
}