package net.sarasarasa.lifeup.service

import com.cdev.achievementview.AchievementView
import net.sarasarasa.lifeup.models.AchievementModel

interface AchievementService {

    fun initAchievement()

    fun getAchievementById(id: Int): AchievementModel

    fun finishAchievement(id: Int)

    fun checkAchievement(achievementView: AchievementView)

}