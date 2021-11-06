package by.profs.rowgame.view.fragments.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Boat
import by.profs.rowgame.data.items.Oar
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.databinding.FragmentTrainingBinding
import by.profs.rowgame.presenter.database.dao.BoatDao
import by.profs.rowgame.presenter.database.dao.ComboDao
import by.profs.rowgame.presenter.database.dao.CompetitionDao
import by.profs.rowgame.presenter.database.dao.OarDao
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.presenter.mappers.ComboItemWrapper
import by.profs.rowgame.presenter.trainer.Trainer
import by.profs.rowgame.utils.TRAIN_ENDURANCE
import by.profs.rowgame.utils.TRAIN_POWER
import by.profs.rowgame.utils.TRAIN_TECHNICALITY
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.ComboViewAdapter
import by.profs.rowgame.view.fragments.extensions.setup
import by.profs.rowgame.view.fragments.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private var binding: FragmentTrainingBinding? = null
    private lateinit var competitionDays: IntArray
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)
    private lateinit var recyclerView: RecyclerView
    private val comboDao: ComboDao = ServiceLocator.locate()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val deleteComboFun: (Int?) -> Unit = {
        MainScope().launch { this@TrainingFragment.context?.showToast(R.string.something_broke) }
        scope.launch {
            comboDao.deleteComboWithRower(it!!)
            refreshView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingBinding.inflate(inflater, container, false)
        _infoBar = (requireActivity() as ActivityWithInfoBar).infoBar
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoBar.showDay()

        recyclerView = binding!!.list.setup()
        scope.launch {
            competitionDays = ServiceLocator.get(CompetitionDao::class).getCompetitionDays()
            refreshView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun nextDay() = MainScope().launch {
        infoBar.nextAndShowDay()
        infoBar.showDay()
        val day = infoBar.getDay()
        if (competitionDays.contains(day)) {
            if (competitionDays.last() == day) {
                scope.launch { ServiceLocator.get(CompetitionDao::class).deleteLicences() } }
            requireContext().showToast(R.string.time_to_race)
            TrainingFragmentDirections.actionTrainingFragmentToPreCompetitionFragment().also {
                findNavController().navigate(it) }
        }
    }

    private fun refreshView() {
        val boatDao: BoatDao = ServiceLocator.locate()
        val oarDao: OarDao = ServiceLocator.locate()
        val rowerDao: RowerDao = ServiceLocator.locate()

        val combos = comboDao.getAllCombos().toMutableList()
        val boats = ArrayList<Boat>()
        val oars = ArrayList<Oar>()
        val rowers = ArrayList<Rower>()

        combos.forEach {
            val boat = boatDao.search(it.boatId)
            val oar = oarDao.search(it.oarId)
            val rower = rowerDao.search(it.rowerId)
            if (boat == null || oar == null || rower == null) { deleteComboFun(it.combinationId)
            } else {
                boats.add(boatDao.search(it.boatId)!!)
                oars.add(oarDao.search(it.oarId)!!)
                rowers.add(rowerDao.search(it.rowerId)!!)
            }
        }

        MainScope().launch {
            recyclerView.adapter = ComboViewAdapter(ComboItemWrapper.map(boats, oars, rowers))
        }

        val trainer = Trainer(deleteComboFun)

        fun Button.setupTrain(mode: Int) = setOnClickListener {
            scope.launch { trainer.startTraining(mode, combos) }
            nextDay()
        }

        binding?.run {
            buttonTrainEndurance.setupTrain(TRAIN_ENDURANCE)
            buttonTrainPower.setupTrain(TRAIN_POWER)
            buttonTrainTechnical.setupTrain(TRAIN_TECHNICALITY)
        }
    }
}