package net.sarasarasa.lifeup.utils

import android.content.Context
import android.widget.Toast

class ToastUtils {
    companion object {
        fun showShortToast(context: Context, string: String) {
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
        }

        fun showLongToast(context: Context, string: String) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }
    }
}