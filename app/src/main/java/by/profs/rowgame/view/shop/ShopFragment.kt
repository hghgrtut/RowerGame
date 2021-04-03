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
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentShopBinding
import by.profs.rowgame.presenter.database.BoatRoomDatabase
import by.profs.rowgame.presenter.database.OarRoomDatabase
import by.profs.rowgame.presenter.navigation.INTENT_OARS
import by.profs.rowgame.view.adapters.BoatViewAdapter
import by.profs.rowgame.view.adapters.OarViewAdapter
import by.profs.rowgame.view.adapters.SHOP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ShopFragment : Fragment(R.layout.fragment_shop) {
    private val args by navArgs<ShopFragmentArgs>()
    private var binding: FragmentShopBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var prefEditor: PreferenceEditor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefEditor = PreferenceEditor(requireContext())
        recyclerView = binding!!.list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        if (args.itemType == INTENT_OARS) {
            val dao = OarRoomDatabase
                .getDatabase(requireContext(), CoroutineScope(Dispatchers.IO)).oarDao()
            val viewAdapter = OarViewAdapter(SHOP, prefEditor, dao)
            recyclerView = binding!!.list.apply { adapter = viewAdapter }
            requireActivity().setTitle(R.string.oar_shop)
        } else {
            val dao = BoatRoomDatabase
                .getDatabase(requireContext(), CoroutineScope(Dispatchers.IO)).boatDao()
            val viewAdapter = BoatViewAdapter(SHOP, prefEditor, dao)
            recyclerView = binding!!.list.apply { adapter = viewAdapter }
            requireActivity().setTitle(R.string.boat_shop)
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.money?.text = this.getString(R.string.money_balance, prefEditor.getBalance())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}