package by.profs.rowgame.view.fragments.extensions

import android.view.View
import android.widget.ImageView
import by.profs.rowgame.R
import by.profs.rowgame.presenter.imageloader.CoilImageLoader

fun View.makeVisible() = setVisibility(View.VISIBLE)
fun View.makeInvisible() = setVisibility(View.GONE)

fun ImageView.loadThumb(thumb: String?) = thumb?.let {
    CoilImageLoader.loadImageFromNetwork(this, thumb)
} ?: setImageResource(R.drawable.placeholder_man)