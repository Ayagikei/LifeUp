package net.sarasarasa.lifeup.utils

import android.content.Context
import android.widget.Toast
import java.lang.ref.WeakReference

class ToastUtils {
    companion object {

        private lateinit var contextReference: WeakReference<Context>
        private var mToast: Toast? = null

        fun init(context: Context) {
            this.contextReference = WeakReference(context)
        }

        fun showShortToast(string: String) {
            val context = contextReference.get()

            try {
                if (context != null) {
                    if (mToast == null) mToast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
                    else mToast!!.setText(string)
                    mToast?.duration = Toast.LENGTH_SHORT
                    mToast?.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showShortToast(string: String, passedContext: Context) {
            try {
                Toast.makeText(passedContext, string, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showLongToast(string: String) {
            val context = contextReference.get()

            try {
                if (context != null) {
                    if (mToast == null) mToast = Toast.makeText(context, string, Toast.LENGTH_LONG)
                    else mToast!!.setText(string)
                    mToast?.duration = Toast.LENGTH_LONG
                    mToast?.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showLongToast(string: String, passedContext: Context) {
            try {
                Toast.makeText(passedContext, string, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}