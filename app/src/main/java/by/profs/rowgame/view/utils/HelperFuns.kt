package by.profs.rowgame.view.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.presenter.imageloader.GlideImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader

object HelperFuns {
    fun changeTheme(resources: Resources) {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Configuration.UI_MODE_NIGHT_NO ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun showToast(context: Context, resId: Int) =
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()

    fun showToast(context: Context, str: String) =
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()

    fun loadThumb(rower: Rower, view: ImageView) {
        val imageLoader: ImageLoader = GlideImageLoader
        if (rower.thumb != null) {
            imageLoader.loadImageFromNetwork(view, rower.thumb)
        } else { view.setImageResource(
            if (rower.gender == Rower.MALE) { R.drawable.placeholder_man
            } else { R.drawable.placeholder_woman })
        }
    }
}