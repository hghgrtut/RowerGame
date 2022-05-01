package by.profs.rowgame.view.adapters.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import by.profs.rowgame.R

class DetachDialog(private val detachFun: () -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.warning)
                .setMessage(R.string.detach)
                .setCancelable(true)
                .setPositiveButton(R.string.yes) { _, _ -> detachFun() }
                .setNegativeButton(R.string.no) { dialog, _ -> onCancel(dialog) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}