package net.sarasarasa.lifeup.converter

import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication

class UserConverter {
    companion object {
        fun iSexTostrSex(sex: Int): String? {
            return when (sex) {
                0 -> LifeUpApplication.getLifeUpApplication().getString(R.string.female)
                1 -> LifeUpApplication.getLifeUpApplication().getString(R.string.male)
                2 -> LifeUpApplication.getLifeUpApplication().getString(R.string.secret)
                else -> null
            }
        }
    }
}