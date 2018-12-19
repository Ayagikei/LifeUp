package net.sarasarasa.lifeup.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import dmax.dialog.SpotsDialog
import java.lang.ref.WeakReference

class LoadingDialogUtils {
    companion object {

        var dialog: AlertDialog? = null
        private lateinit var contextReference: WeakReference<Context>

        fun show(context: Context) {

            this.contextReference = WeakReference(context)

            if (dialog != null) {
                dialog?.dismiss()
            }

            val mContext = contextReference.get()
            if (mContext != null) {
                dialog = SpotsDialog.Builder().setMessage("加载中...").setCancelable(true).setContext(context).build()

                try {
                    val mActivity = mContext as Activity

                    dialog?.let {
                        if (!it.isShowing && !mActivity.isDestroyed)
                            it.show()
                    }
                } catch (e: Exception) {
                    ToastUtils.showShortToast(e.toString())
                }
            }

        }

        fun dismiss() {
            dialog?.dismiss()
        }
    }
}