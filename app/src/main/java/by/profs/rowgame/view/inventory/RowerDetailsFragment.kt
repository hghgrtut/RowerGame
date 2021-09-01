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
import by.profs.rowgame.app.ServiceLocator
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer.getRandomRower
import by.profs.rowgame.databinding.FragmentRowerDetailsBinding
import by.profs.rowgame.presenter.database.dao.RowerDao
import by.profs.rowgame.presenter.imageloader.CoilImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader
import by.profs.rowgame.presenter.traders.Recruiter
import by.profs.rowgame.view.activity.infobar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RowerDetailsFragment : Fragment(R.layout.fragment_rower_details) {
    private val args by navArgs<RowerDetailsFragmentArgs>()
    private val navController by lazy(LazyThreadSafetyMode.NONE) { findNavController() }
    private var binding: FragmentRowerDetailsBinding? = null
    private val dao: RowerDao = ServiceLocator.locate()
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
        recruiter = Recruiter(requireActivity().infobar(), dao)
        MainScope().launch { showRower() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private suspend fun showRower() {
        val rower: Rower
        if (args.source != FROM_LIST) {
            rower = withContext(Dispatchers.IO) { getRandomRower() }
            binding?.buttonNewLegend?.visibility = View.VISIBLE
            binding?.buttonNewLegend?.setOnClickListener {
                navController.navigate(R.id.action_rowerDetailsFragment_to_newLegendFragment)
            }
            setAsNew(rower)
        } else {
            rower = withContext(Dispatchers.IO) { dao.search(args.rowerId)!! }
            setAsExisting(rower)
        }

        showImage(binding!!.rowerPic, rower)
        binding?.age?.text = this.getString(R.string.rower_age, rower.age)
        binding?.endurance?.text = this.getString(R.string.rower_endurance, rower.endurance)
        binding?.height?.text = this.getString(R.string.rower_height, rower.height)
        binding?.name?.text = rower.name
        binding?.technicalit?.text = this.getString(R.string.rower_technicality, rower.technics)
        binding?.power?.text = this.getString(R.string.rower_power, rower.power)
        binding?.weight?.text = this.getString(R.string.rower_weight, rower.weight)
        if (rower.about != null) {
            binding?.aboutTitle?.visibility = View.VISIBLE
            binding?.about?.text = rower.about
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
        binding?.button?.text = this.getString(R.string.fire_rower)
        binding?.button?.setOnClickListener {
            recruiter.sell(rower)
            setAsNew(rower)
        }
    }

    private fun setAsNew(rower: Rower) {
        binding?.button?.text = this.getString(R.string.recruit)
        binding?.button?.setOnClickListener { if (recruiter.buy(rower)) setAsExisting(rower) }
    }

    companion object {
        const val FROM_EVENT = 1
        const val RANDOM_ROWER = 2
        const val FROM_LIST = 3
    }
}