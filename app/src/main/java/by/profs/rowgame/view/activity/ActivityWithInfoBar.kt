package by.profs.rowgame.view.activity

import androidx.appcompat.app.AppCompatActivity

abstract class ActivityWithInfoBar : AppCompatActivity() {
    abstract val infoBar: InfoBar

    /**
     * Sets string as a subtitle of toolbar
     */
    abstract fun setSubtitle(string: String)
}