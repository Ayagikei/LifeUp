package net.lifeupapp.lifeup.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import net.lifeupapp.lifeup.api.Val.LIFEUP_PACKAGE_NAME
import net.lifeupapp.lifeup.api.utils.isAppInstalled


@SuppressLint("StaticFieldLeak")
object LifeUpApi {

    private lateinit var appCtx: Context

    fun init(context: Context) {
        appCtx = context.applicationContext ?: context
    }

    fun isLifeUpInstalled(): Boolean {
        return isAppInstalled(appCtx, LIFEUP_PACKAGE_NAME)
    }

    fun call(context: Context?, url: String) {
        val action = parseUriIntent(url)
        action.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        (context ?: appCtx).startActivity(action)
    }

    @Throws
    private fun parseUriIntent(uriString: String): Intent {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent.parseUri(uriString, Intent.URI_ALLOW_UNSAFE)
        } else {
            Intent.parseUri(uriString, 0)
        }
        // forbid launching activities without BROWSABLE category
        intent.addCategory("android.intent.category.BROWSABLE")
        // forbid explicit call
        intent.component = null
        // forbid intent with selector intent
        intent.selector = null
        return intent
    }


}