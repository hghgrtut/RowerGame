package by.profs.rowgame.view.fragments.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.data.preferences.LevelEditor
import by.profs.rowgame.databinding.FragmentShopBinding
import by.profs.rowgame.utils.SHOP_SIZE
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.SHOP
import by.profs.rowgame.view.fragments.extensions.setup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopFragment : Fragment(R.layout.fragment_shop) {
    private var binding: FragmentShopBinding? = null
    private lateinit var recyclerView: RecyclerView
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)
    private val maxItemLevel = LevelEditor.get() + 2
    private lateinit var boatViewAdapter: BoatViewAdapter
    private lateinit var oarViewAdapter: OarViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding!!.list.setup()
        MainScope().launch {
            val boats = withContext(Dispatchers.IO) {
                ArrayList(List(SHOP_SIZE) { Randomizer.getRandomBoat(maxItemLevel) }) }
            val oars = withContext(Dispatchers.IO) {
                List(SHOP_SIZE) { Randomizer.getRandomOar(maxItemLevel) } }
            boatViewAdapter = BoatViewAdapter(boats, SHOP, infoBar)
            oarViewAdapter = OarViewAdapter(oars, SHOP, infoBar)
            recyclerView.adapter = listOf(boatViewAdapter, oarViewAdapter).random()
            binding?.let { it ->
                it.oars.setOnClickListener { recyclerView.adapter = oarViewAdapter }
                it.boats.setOnClickListener { recyclerView.adapter = boatViewAdapter }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        _infoBar = (requireActivity() as ActivityWithInfoBar).infoBar
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private suspend fun showBoats() {
        val randomBoats = withContext(Dispatchers.IO) {
            ArrayList(List(SHOP_SIZE) { Randomizer.getRandomBoat(maxItemLevel) }) }
        val viewAdapter = BoatViewAdapter(randomBoats, SHOP, infoBar)
        recyclerView.adapter = viewAdapter
    }

    private suspend fun showOars() {
        val oars = withContext(Dispatchers.IO) {
            List(SHOP_SIZE) { Randomizer.getRandomOar(maxItemLevel) } }
        val viewAdapter = OarViewAdapter(oars, SHOP, infoBar)
        recyclerView.adapter = viewAdapter
    }
}