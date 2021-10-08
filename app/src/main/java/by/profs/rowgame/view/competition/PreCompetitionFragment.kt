package by.profs.rowgame.view.competition

import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.competition.CompetitionLevel.Companion.isRegional
import by.profs.rowgame.databinding.FragmentPreCompetitionBinding
import by.profs.rowgame.presenter.competition.type.AbstractCompetition
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.view.activity.infobar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreCompetitionFragment : Fragment(R.layout.fragment_pre_competition) {
    private var _binding: FragmentPreCompetitionBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreCompetitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val competitionDao: CompetitionDao = ServiceLocator.locate()
        CoroutineScope(Dispatchers.IO).launch {
            val competition = competitionDao.search(requireActivity().infobar().getDay())
            MainScope().launch {
                binding.run {
                    imageCompetition.setImageResource(when (competition.type) {
                        AbstractCompetition.CONCEPT -> R.drawable.competition_concept
                        AbstractCompetition.OFP -> R.drawable.competition_ofp
                        else -> R.drawable.competition_water
                    })
                    nameCompetition.text = competition.toString()
                    if (!competition.level.isRegional()) {
                        aboutParticipants.visibility = View.VISIBLE
                        MainScope().launch { participants.text = withContext(Dispatchers.IO) {
                            competitionDao.getParticipantsNames(competition.level, competition.age)
                                .joinToString(",\n") }
                        }
                    }
                }
                Handler(getMainLooper()).postDelayed(
                    {
                        PreCompetitionFragmentDirections
                            .actionPreCompetitionFragmentToCompetitionFragment()
                            .also { findNavController().navigate(it) }
                    },
                    DELAY
                )
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object { private const val DELAY = 3L * SECOND_IN_MILLIS }
}