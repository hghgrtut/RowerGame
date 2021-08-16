package by.profs.rowgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.profs.rowgame.R
import by.profs.rowgame.databinding.MainFragmentBinding
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.utils.HelperFuns.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(R.layout.main_fragment) {
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: MainFragmentBinding? = null

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

        binding?.run {
            goToBoats.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToInventoryFragment(INTENT_BOATS)
                    .also { navController.navigate(it) }
            }
            goToOars.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToInventoryFragment(INTENT_OARS)
                    .also { navController.navigate(it) }
            }
            goToRowers.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToInventoryFragment(INTENT_ROWERS)
                    .also { navController.navigate(it) }
            }
            goToLegends.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToRowerDetailsFragment()
                    .also { navController.navigate(it) }
            }
            goToNewPair.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToPairingFragment()
                    .also { navController.navigate(it) }
            }
            goToExistingPairs.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToTrainingFragment()
                    .also { navController.navigate(it) }
            }

            giveMoney.setOnClickListener { MainScope().launch { resetMoney() } }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private suspend fun resetMoney() {
        val context = requireContext()
        val flag = withContext(Dispatchers.IO) { true }
        showToast(context, if (flag) R.string.money_reseted else R.string.money_not_reseted)
    }
}