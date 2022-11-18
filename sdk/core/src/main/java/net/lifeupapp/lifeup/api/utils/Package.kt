package net.lifeupapp.lifeup.api.utils

import android.content.Context
import net.lifeupapp.lifeup.api.LifeUpApi

internal fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: Exception) {
        false
    }
}