package by.profs.rowgame.view.fragments.extensions

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.profs.rowgame.app.ServiceLocator

fun RecyclerView.setup(): RecyclerView {
    val context: Context = ServiceLocator.locate()
    setHasFixedSize(true)
    layoutManager = LinearLayoutManager(context)
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    return this
}