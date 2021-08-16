package by.profs.rowgame.presenter.imageloader

import android.widget.ImageView
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Rower

fun ImageView.loadThumb(rower: Rower) {
    val imageLoader: ImageLoader = CoilImageLoader
    if (rower.thumb != null) {
        imageLoader.loadImageFromNetwork(this, rower.thumb)
    } else { this.setImageResource(
        if (rower.gender == Rower.MALE) { R.drawable.placeholder_man
        } else { R.drawable.placeholder_woman })
    }
}

interface ImageLoader {
    fun loadImageFromNetwork(view: ImageView, url: String)
}