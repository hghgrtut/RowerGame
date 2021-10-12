package by.profs.rowgame.view.fragments.extensions

import android.widget.Button

fun Button.enableClick(onClick: () -> Unit) {
    makeVisible()
    setOnClickListener { onClick() }
}