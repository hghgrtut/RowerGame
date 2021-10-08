package by.profs.rowgame.view.extensions

import android.view.View
import android.widget.Button

fun Button.enableClick(onClick: () -> Unit) {
    visibility = View.VISIBLE
    setOnClickListener { onClick() }
}