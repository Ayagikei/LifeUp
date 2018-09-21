package net.sarasarasa.lifeup.utils

import android.content.Context
import android.widget.Toast
import java.lang.ref.WeakReference

class ToastUtils {
    companion object {

        lateinit var contextReference: WeakReference<Context>

        fun init(context: Context) {
            this.contextReference = WeakReference(context)
        }

        fun showShortToast(string: String) {
            val context = contextReference.get()

            if (context != null)
                Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
        }

        fun showLongToast(string: String) {
            val context = contextReference.get()

            if (context != null)
                Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }
    }
}