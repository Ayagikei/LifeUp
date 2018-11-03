package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.AchievementModel
import org.litepal.LitePal

class AchievementDAO {

    fun getAchievementById(id: Int): AchievementModel? {
        return LitePal.where("achievementId = ?", id.toString()).findFirst(AchievementModel::class.java)
    }
}