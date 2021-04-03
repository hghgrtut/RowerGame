package by.profs.rowgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.MainFragmentBinding
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.presenter.utils.Resetter
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(R.layout.main_fragment) {
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: MainFragmentBinding? = null
    private lateinit var calendar: Calendar
    private lateinit var prefEditor: PreferenceEditor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar(requireContext())
        prefEditor = PreferenceEditor(requireContext())

        binding?.goToBoats?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToInventoryFragment(itemType = INTENT_BOATS)
                .also { navController.navigate(it) } }
        binding?.goToOars?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToInventoryFragment(itemType = INTENT_OARS)
                .also { navController.navigate(it) } }
        binding?.goToRowers?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToInventoryFragment(itemType = INTENT_ROWERS)
                .also { navController.navigate(it) } }
        binding?.goToLegends?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToRowerDetailsFragment()
                .also { navController.navigate(it) } }
        binding?.goToNewPair?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToPairingFragment()
                .also { navController.navigate(it) } }
        binding?.goToExistingPairs?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToTrainingFragment()
                .also { navController.navigate(it) } }
        binding?.goToCompetitions?.setOnClickListener {
            MainFragmentDirections.actionMainFragmentToCompetitionFragment()
                .also { navController.navigate(it) } }

        binding?.giveMoney?.setOnClickListener { MainScope().launch { resetMoney() } }
    }

    override fun onResume() {
        super.onResume()
        val balance = prefEditor.getBalance()
        binding?.money?.text = this.getString(R.string.money_balance, balance)
        showDay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showDay() {
        binding?.day?.text = this.getString(R.string.day_with_instruction, calendar.getDayOfYear())
    }

    private suspend fun resetMoney() {
        val context = requireContext()
        val flag = withContext(Dispatchers.IO) { Resetter.giveInitMoney(prefEditor, context) }
        showToast(context, if (flag) R.string.money_reseted else R.string.money_not_reseted)
        onResume()
    }
}