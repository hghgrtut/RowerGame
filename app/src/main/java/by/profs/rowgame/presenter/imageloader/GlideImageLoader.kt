package by.profs.rowgame.presenter.imageloader

import android.widget.ImageView
import by.profs.rowgame.R
import com.bumptech.glide.Glide

object GlideImageLoader : ImageLoader {
    override fun loadImageFromNetwork(view: ImageView, url: String) {
        Glide.with(view)
            .load(url)
            .error(R.drawable.image_failed_download)
            .placeholder(R.drawable.placeholder_universal)
            .into(view)
    }
}