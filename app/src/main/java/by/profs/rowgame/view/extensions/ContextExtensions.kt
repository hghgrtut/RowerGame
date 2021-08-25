package by.profs.rowgame.view.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
fun Context.showToast(resId: Int, vararg args: Any) =
    Toast.makeText(this, this.getString(resId, *args), Toast.LENGTH_SHORT).show()
fun Context.showToast(str: String) = Toast.makeText(this, str, Toast.LENGTH_SHORT).show()