package net.lifeupapp.lifeup.http.utils

import android.os.Build
import android.text.Html
import android.widget.TextView

fun TextView.setHtmlText(html: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        this.text = Html.fromHtml(html)
    }
}

fun TextView.setHtmlText(stringRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(context.getString(stringRes), Html.FROM_HTML_MODE_LEGACY)
    } else {
        this.text = Html.fromHtml(context.getString(stringRes))
    }
}