package net.sarasarasa.lifeup.service

import android.app.Activity
import com.cdev.achievementview.AchievementView
import net.sarasarasa.lifeup.models.AchievementModel
import java.lang.ref.WeakReference

interface AchievementService {

    fun initAchievement()

    fun getAchievementById(id: Int): AchievementModel

    fun finishAchievement(id: Int)

    fun checkAchievement(achievementView: AchievementView, weakReference: WeakReference<Activity>)
}