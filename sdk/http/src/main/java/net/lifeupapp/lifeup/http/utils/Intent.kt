package net.lifeupapp.lifeup.http.utils

import android.app.PendingIntent
import android.os.Build

fun compactAndFlagImmutable(flag: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flag or PendingIntent.FLAG_IMMUTABLE else flag
}