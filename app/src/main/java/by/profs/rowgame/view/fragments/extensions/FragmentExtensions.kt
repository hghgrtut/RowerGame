package by.profs.rowgame.view.fragments.extensions

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.setTitle(@StringRes titleId: Int) =
    (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(titleId)

fun Fragment.setTitle(title: String) =
    (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(title)