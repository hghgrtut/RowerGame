package by.profs.rowgame.view.extensions

import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.getIntOrZero(): Int = this.editText?.text.toString().toIntOrNull() ?: 0

fun TextInputLayout.hasText(): Boolean = this.editText?.text.toString() != ""

fun TextInputLayout.setError(@StringRes errorId: Int) {
    isErrorEnabled = true
    error = context.getString(errorId)
}

fun TextInputLayout.clearError() {
    isErrorEnabled = false
    error = ""
}