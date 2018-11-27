package net.sarasarasa.lifeup.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import dmax.dialog.SpotsDialog

class LoadingDialogUtils {
    companion object {

        var dialog: AlertDialog? = null

        fun show(context: Context) {

            if (dialog != null) {
                dialog?.dismiss()
            }

            dialog = SpotsDialog.Builder().setMessage("加载中...").setCancelable(true).setContext(context).build()

            dialog?.let {
                if (!it.isShowing)
                    it.show()
            }

        }

        fun dismiss() {

            Log.i("LoadingDialog", "dismiss()")
            dialog?.let {
                it.dismiss()
            }
        }
    }
}