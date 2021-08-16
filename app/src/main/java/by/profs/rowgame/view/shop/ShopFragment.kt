package by.profs.rowgame.view.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.databinding.FragmentShopBinding
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.utils.SHOP_SIZE
import by.profs.rowgame.view.activity.ActivityWithInfoBar
import by.profs.rowgame.view.activity.InfoBar
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.SHOP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopFragment : Fragment(R.layout.fragment_shop) {
    private val args by navArgs<ShopFragmentArgs>()
    private var binding: FragmentShopBinding? = null
    private lateinit var database: MyRoomDatabase
    private lateinit var recyclerView: RecyclerView
    private var _infoBar: InfoBar? = null
    private val infoBar: InfoBar get() = requireNotNull(_infoBar)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        database = MyRoomDatabase.getDatabase(requireContext())
        MainScope().launch { if (args.itemType == INTENT_OARS) showOars() else showBoats() }
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
            ArrayList(List(SHOP_SIZE) { Randomizer.getRandomBoat() }) }
        val viewAdapter = BoatViewAdapter(randomBoats, SHOP, infoBar, database.boatDao())
        recyclerView = binding!!.list.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.boat_shop)
    }

    private suspend fun showOars() {
        val oars = withContext(Dispatchers.IO) { List(SHOP_SIZE) { Randomizer.getRandomOar() } }
        val viewAdapter = OarViewAdapter(oars, SHOP, infoBar, database)
        recyclerView = binding!!.list.apply { adapter = viewAdapter }
        requireActivity().setTitle(R.string.oar_shop)
    }
}