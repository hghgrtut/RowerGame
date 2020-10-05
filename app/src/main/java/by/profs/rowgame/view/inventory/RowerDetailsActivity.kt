package by.profs.rowgame.view.inventory

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import by.profs.rowgame.R
import by.profs.rowgame.data.PreferenceEditor
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Randomizer
import by.profs.rowgame.databinding.ActivityRowerDetailsBinding
import by.profs.rowgame.presenter.api.RetrofitApiImplementation
import by.profs.rowgame.presenter.dao.RowerDao
import by.profs.rowgame.presenter.database.RowerRoomDatabase
import by.profs.rowgame.presenter.imageloader.GlideImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader
import by.profs.rowgame.presenter.traders.Recruiter
import by.profs.rowgame.utils.NAME_ROWER
import by.profs.rowgame.utils.ROWER_SOURCE
import by.profs.rowgame.utils.USER_PREF
import by.profs.rowgame.view.utils.HelperFuns.showToast
import java.net.UnknownHostException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RowerDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRowerDetailsBinding
    private lateinit var prefEditor: PreferenceEditor
    private lateinit var dao: RowerDao
    private lateinit var recruiter: Recruiter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRowerDetailsBinding.inflate(layoutInflater)
        prefEditor = PreferenceEditor(
            applicationContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE))
        dao = RowerRoomDatabase.getDatabase(application, CoroutineScope(Dispatchers.IO)).rowerDao()
        recruiter = Recruiter(prefEditor, dao)
        MainScope().launch { showRower() }
        setContentView(binding.root)
    }

    suspend fun showRower() {
        try {
            val source = intent.extras?.getInt(ROWER_SOURCE)
            val rower: Rower = withContext(Dispatchers.IO) { when (source) {
                    FROM_EVENT -> RetrofitApiImplementation.getListOfEventRowers()!![0]
                    FROM_LIST -> dao.search(intent.extras?.getString(NAME_ROWER)!!)[0]
                    else -> Randomizer.getRandomRower()
                }
            }
            showImage(binding.rowerPic, rower)
            binding.age.text = this.getString(R.string.rower_age, rower.age)
            binding.endurance.text = this.getString(R.string.rower_endurance, rower.endurance)
            binding.height.text = this.getString(R.string.rower_height, rower.height)
            binding.name.text = rower.name
            binding.technicality.text = this.getString(R.string.rower_technicality, rower.technics)
            binding.power.text = this.getString(R.string.rower_power, rower.power)
            binding.weight.text = this.getString(R.string.rower_weight, rower.weight)
            if (rower.endpointAbout != null) {
                showExtraInfo(rower.endpointAbout)
            }
            if (withContext(Dispatchers.IO) { dao.search(rower.name) }.isEmpty()) {
                setAsNew()
                binding.button.setOnClickListener { recruit(rower) }
            } else {
                setAsExisting()
                binding.button.setOnClickListener { fire(rower) }
            }
            if (rower.cost > 0) {
                binding.cost.text = this.getString(R.string.fame_cost, rower.cost)
                binding.cost.visibility = View.VISIBLE
            }
        } catch (e: IllegalStateException) {
            setContentView(R.layout.error_network_layout)
            findViewById<TextView>(R.id.error).text = getString(R.string.error_server, e.message)
        } catch (e: UnknownHostException) {
            setContentView(R.layout.error_network_layout)
            findViewById<TextView>(R.id.error).text = getString(R.string.error_network_connection)
        }
    }

    private suspend fun showExtraInfo(infoEndpoint: String) {
        val extraInfo = withContext(Dispatchers.IO) {
            RetrofitApiImplementation.getRowerExtraInfo(infoEndpoint)
        }
        if (extraInfo?.achievements != null) {
            binding.achievementsTitle.visibility = View.VISIBLE
            binding.achievements.text = extraInfo.achievements
            binding.achievements.visibility = View.VISIBLE
        }
        if (extraInfo?.otherInfo != null) {
            binding.aboutTitle.visibility = View.VISIBLE
            binding.about.text = extraInfo.otherInfo
            binding.about.visibility = View.VISIBLE
        }
        if (extraInfo == null) {
            setContentView(R.layout.error_network_layout)
            return
        }
    }

    private fun showImage(view: ImageView, rower: Rower) {
        val imageLoader: ImageLoader = GlideImageLoader
        if (rower.photo != null) { imageLoader.loadImageFromNetwork(view, rower.photo)
        } else { view.setImageResource(
            if (rower.gender == Rower.MALE) { R.drawable.placeholder_man
            } else { R.drawable.placeholder_woman })
        }
    }

    fun setAsExisting() {
        binding.button.text = this.getString(R.string.fire_rower)
        binding.fame.visibility = View.GONE
    }

    fun setAsNew() {
        binding.button.text = this.getString(R.string.recruit)
        binding.fame.text = this.getString(R.string.fame_balance, prefEditor.getFame())
        binding.fame.visibility = View.VISIBLE
    }

    fun recruit(rower: Rower) {
        if (recruiter.buy(rower)) {
            showToast(this, R.string.recruit_success)
            setAsExisting()
        } else { showToast(this, R.string.recruit_fail) }
        binding.button.setOnClickListener { fire(rower) }
    }

    fun fire(rower: Rower) {
        recruiter.sell(rower)
        setAsNew()
        binding.button.setOnClickListener { recruit(rower) }
        showToast(this, R.string.fired)
    }

    companion object {
        const val FROM_EVENT = 1
        const val RANDOM_ROWER = 2
        const val FROM_LIST = 3
    }
}