package by.profs.rowgame.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import by.profs.rowgame.R
import by.profs.rowgame.databinding.MainFragmentBinding
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.view.activity.ActivityWithInfoBar

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

        (requireActivity() as ActivityWithInfoBar).setSubtitle("")
        binding?.run {
            goToBoats.setOnClickListener { navigateToInventory(INTENT_BOATS) }
            goToOars.setOnClickListener { navigateToInventory(INTENT_OARS) }
            goToRowers.setOnClickListener { navigateToInventory(INTENT_ROWERS) }
            goToNewPair.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToPairingFragment().navigate()
            }
            goToExistingPairs.setOnClickListener {
                MainFragmentDirections.actionMainFragmentToTrainingFragment().navigate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun navigateToInventory(type: Int) =
        MainFragmentDirections.actionMainFragmentToInventoryFragment(type).navigate()

    private fun NavDirections.navigate() = also { navController.navigate(it) }
}