package by.profs.rowgame.presenter.imageloader

import android.widget.ImageView
import by.profs.rowgame.R
import coil.load

object CoilImageLoader : ImageLoader {
    override fun loadImageFromNetwork(view: ImageView, url: String) {
        view.load(url) {
            crossfade(true)
            error(R.drawable.placeholder_man)
            placeholder(R.drawable.placeholder_universal)
        }
    }
}