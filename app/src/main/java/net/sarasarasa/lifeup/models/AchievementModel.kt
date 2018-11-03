package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport

data class AchievementModel(var achievementId: Int,
                            var isGotReward: Boolean,
                            var title: String,
                            var desc: String,
                            var hasFinished: Boolean) : LitePalSupport() {
    var id: Long? = null

}