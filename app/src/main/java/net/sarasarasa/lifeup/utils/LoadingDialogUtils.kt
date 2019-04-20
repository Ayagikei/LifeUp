package net.sarasarasa.lifeup.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import dmax.dialog.SpotsDialog
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import java.lang.ref.WeakReference

class LoadingDialogUtils {
    companion object {

        var dialog: AlertDialog? = null
        private var contextReference: WeakReference<Context>? = null

        fun show(weakRefContext: WeakReference<Context>) {

            this.contextReference = weakRefContext

            if (dialog != null) {
                dialog?.dismiss()
            }

            val mContext = contextReference?.get()
            if (mContext != null) {
                dialog = SpotsDialog.Builder()
                        .setMessage(LifeUpApplication.getLifeUpApplication().getString(R.string.loading))
                        .setCancelable(true)
                        .setContext(mContext)
                        .build()

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
            if (dialog?.isShowing == true)
                dialog?.dismiss()
        }

        fun dismissAndClearReference() {
            if (dialog?.isShowing == true)
                dialog?.dismiss()

            contextReference = null
        }
    }
}