package by.profs.rowgame.presenter.imageloader

import android.widget.ImageView
import by.profs.rowgame.R

fun ImageView.loadThumb(thumb: String?) {
    thumb?.let {
        (CoilImageLoader as ImageLoader).loadImageFromNetwork(this, thumb)
    } ?: this.setImageResource(R.drawable.placeholder_man)
}

interface ImageLoader {
    fun loadImageFromNetwork(view: ImageView, url: String)
}