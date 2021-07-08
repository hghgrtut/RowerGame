package by.profs.rowgame.view.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentRowerDetailsBinding
import by.profs.rowgame.presenter.api.RetrofitApiImplementation
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.imageloader.CoilImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader
import by.profs.rowgame.presenter.traders.Recruiter
import by.profs.rowgame.view.utils.HelperFuns.showToast
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RowerDetailsFragment : Fragment(R.layout.fragment_rower_details) {
    private val args by navArgs<RowerDetailsFragmentArgs>()
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: FragmentRowerDetailsBinding? = null
    private lateinit var prefEditor: PreferenceEditor
    private lateinit var dao: RowerDao
    private lateinit var recruiter: Recruiter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRowerDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        prefEditor = PreferenceEditor(context)
        dao = MyRoomDatabase.getDatabase(context).rowerDao()
        recruiter = Recruiter(prefEditor, dao)
        MainScope().launch { showRower() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private suspend fun showRower() {
        try {
            val rower: Rower = withContext(Dispatchers.IO) { when (args.source) {
                FROM_EVENT -> RetrofitApiImplementation.getListOfEventRowers()!![0]
                FROM_LIST -> dao.search(args.rowerId)!!
                else -> Randomizer.getRandomRower()
            } }
            if (args.source != FROM_LIST) {
                binding?.buttonNewLegend?.visibility = View.VISIBLE
                binding?.buttonNewLegend?.setOnClickListener {
                    navController.navigate(R.id.action_rowerDetailsFragment_to_newLegendFragment)
                }
            }

            showImage(binding!!.rowerPic, rower)
            binding?.age?.text = this.getString(R.string.rower_age, rower.age)
            binding?.endurance?.text = this.getString(R.string.rower_endurance, rower.endurance)
            binding?.height?.text = this.getString(R.string.rower_height, rower.height)
            binding?.name?.text = rower.name
            binding?.technicalit?.text = this.getString(R.string.rower_technicality, rower.technics)
            binding?.power?.text = this.getString(R.string.rower_power, rower.power)
            binding?.weight?.text = this.getString(R.string.rower_weight, rower.weight)

            if (rower.about != null) { showExtraInfo(rower.about) }
            if (withContext(Dispatchers.IO) { dao.searchByName(rower.name) } == null) {
                setAsNew(rower)
            } else {
                setAsExisting(rower)
            }
            if (rower.cost > 0) {
                binding?.cost?.text = this.getString(R.string.fame_cost, rower.cost)
                binding?.cost?.visibility = View.VISIBLE
            }
        } catch (e: UnknownHostException) {
            navController.navigate(R.id.action_rowerDetailsFragment_to_netErrorFragment)
        }
    }

    private suspend fun showExtraInfo(infoEndpoint: String) {
        val extraInfo = withContext(Dispatchers.IO) {
            RetrofitApiImplementation.getRowerExtraInfo(infoEndpoint)
        }
        if (extraInfo?.achievements != null) {
            binding?.achievementsTitle?.visibility = View.VISIBLE
            binding?.achievements?.text = extraInfo.achievements
            binding?.achievements?.visibility = View.VISIBLE
        }
        if (extraInfo?.otherInfo != null) {
            binding?.aboutTitle?.visibility = View.VISIBLE
            binding?.about?.text = extraInfo.otherInfo
            binding?.about?.visibility = View.VISIBLE
        }
    }

    private fun showImage(view: ImageView, rower: Rower) {
        val imageLoader: ImageLoader = CoilImageLoader
        if (rower.thumb != null) { imageLoader.loadImageFromNetwork(view, rower.thumb)
        } else { view.setImageResource(
            if (rower.gender == Rower.MALE) { R.drawable.placeholder_man
            } else { R.drawable.placeholder_woman })
        }
    }

    private fun setAsExisting(rower: Rower) {
        showFame()
        binding?.button?.text = this.getString(R.string.fire_rower)
        binding?.button?.setOnClickListener { fire(rower) }
    }

    private fun setAsNew(rower: Rower) {
        showFame()
        binding?.button?.text = this.getString(R.string.recruit)
        binding?.button?.setOnClickListener { recruit(rower) }
    }

    private fun recruit(rower: Rower) {
        if (recruiter.buy(rower)) {
            showToast(requireContext(), R.string.recruit_success)
            setAsExisting(rower)
        } else {
            showToast(requireContext(), R.string.recruit_fail)
        }
    }

    private fun fire(rower: Rower) {
        recruiter.sell(rower)
        setAsNew(rower)
        showToast(requireContext(), R.string.fired)
    }

    private fun showFame() {
        binding?.fame?.text = this.getString(R.string.fame_balance, prefEditor.getFame()) }

    companion object {
        const val FROM_EVENT = 1
        const val RANDOM_ROWER = 2
        const val FROM_LIST = 3
    }
}