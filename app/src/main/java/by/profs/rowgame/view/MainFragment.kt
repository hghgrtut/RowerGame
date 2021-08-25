package by.profs.rowgame.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.profs.rowgame.R
import by.profs.rowgame.data.preferences.Calendar
import by.profs.rowgame.databinding.MainFragmentBinding
import by.profs.rowgame.presenter.navigation.INTENT_BOATS
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.presenter.navigation.INTENT_ROWERS
import by.profs.rowgame.reminder.ReminderReceiver
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.extensions.showToast
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

            daily.setOnClickListener { getDailyReward() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Warning: can be hacked by scrolling date on phone
    private fun getDailyReward() {
        val context = requireContext().applicationContext
        val calendar = Calendar(context)
        val currentDate = calendar.getToday()

        if (currentDate <= calendar.getLastDailyDay()) {
            context.showToast(R.string.daily_already_collected)
            return
        }

        calendar.setLastDailyDay(currentDate)
        ReminderReceiver.setNotification(context)
        val infobar = (requireActivity() as ActivityWithInfoBar).infoBar
        infobar.changeFame(bonusFame)
        infobar.changeMoney(bonusMoney)
        context.showToast(R.string.daily_collected, bonusMoney, bonusFame)
    }

    companion object {
        private const val bonusMoney = 600
        private const val bonusFame = 1
    }
}