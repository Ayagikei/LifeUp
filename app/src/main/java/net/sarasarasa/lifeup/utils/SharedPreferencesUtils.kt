package net.sarasarasa.lifeup.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtils {
    companion object {

        private var optionsSharedPreferences: SharedPreferences? = null

        fun init(context: Context) {
            optionsSharedPreferences = context.getSharedPreferences("options", Context.MODE_PRIVATE)
        }

        fun getOptionsPreferencesInstance(): SharedPreferences? {
            return optionsSharedPreferences
        }

    }
}