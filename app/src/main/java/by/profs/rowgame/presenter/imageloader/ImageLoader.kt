package by.profs.rowgame.presenter.imageloader

import android.widget.ImageView

interface ImageLoader { fun loadImageFromNetwork(view: ImageView, url: String) }