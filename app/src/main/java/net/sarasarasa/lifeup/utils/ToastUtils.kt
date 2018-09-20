package net.sarasarasa.lifeup.utils

import android.content.Context
import android.widget.Toast

class ToastUtils {
    companion object {

        lateinit var context: Context

        fun init(context: Context) {
            this.context = context
        }

        fun showShortToast(string: String) {
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
        }

        fun showLongToast(string: String) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }
    }
}