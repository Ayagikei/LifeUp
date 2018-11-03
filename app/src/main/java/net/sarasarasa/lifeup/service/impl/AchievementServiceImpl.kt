package net.sarasarasa.lifeup.service.impl

import com.cdev.achievementview.AchievementView
import net.sarasarasa.lifeup.dao.AchievementDAO
import net.sarasarasa.lifeup.models.AchievementModel
import net.sarasarasa.lifeup.service.AchievementService


class AchievementServiceImpl : AchievementService {

    private val achievementDAO = AchievementDAO()
    private val attributeService = AttributeServiceImpl()
    private val attributeLevelService = AttributeLevelServiceImpl()
    private val todoService = TodoServiceImpl()
    private val stepService = StepServiceImpl()

    override fun initAchievement() {
        AchievementModel(0, false, "新的开始", "完成你的第一个事项。", false).save()
        AchievementModel(1, false, "大忙人", "完成了两百个待办事项。", false).save()
        AchievementModel(2, false, "日行千里", "单日行走超过20000步。", false).save()
        AchievementModel(3, false, "头号玩家", "完成了两千个待办事项。", false).save()
        AchievementModel(4, false, "新成员", "第一次创建或者加入一个团队。", false).save()
        AchievementModel(5, false, "团队支柱", "完成了两百个团队待办事项。", false).save()
        AchievementModel(6, false, "运动达人", "力量达到等级15。", false).save()
    }


    override fun getAchievementById(id: Int): AchievementModel {
        var achievement = achievementDAO.getAchievementById(id)
        if (achievement == null) {
            initAchievement()
            achievement = achievementDAO.getAchievementById(id)
        }

        return achievement!!
    }

    override fun finishAchievement(id: Int) {
        val item = getAchievementById(id)
        item.isGotReward = true
        item.save()
    }

    override fun checkAchievement(achievementView: AchievementView) {
        for (i in 0..6)
            checkAchievementById(i, achievementView)
    }

    private fun checkAchievementById(id: Int, achievementView: AchievementView) {
        val item = getAchievementById(id)
        if (!item.hasFinished) {
            when (id) {
                0 -> {
                    if (todoService.getFinishCount() >= 1) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「新的开始」", item.desc)
                    }
                }
                1 -> {
                    if (todoService.getFinishCount() >= 200) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「大忙人」", item.desc)
                    }
                }
                2 -> {
                    if (stepService.getTodayStepCount() >= 20000) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「日行千里」", item.desc)
                    }
                }
                3 -> {
                    if (todoService.getFinishCount() >= 2000) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「大忙人」", item.desc)
                    }
                }
                4 -> {
                    if (todoService.getFinishTeamTaskCount() >= 1) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「新成员」", item.desc)
                    }
                }
                5 -> {
                    if (todoService.getFinishTeamTaskCount() >= 200) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「团队支柱」", item.desc)
                    }
                }
                6 -> {
                    if (attributeLevelService.getAttributeLevelByExp(attributeService.getAttributeExpByString("strength")).levelNum >= 15) {
                        item.hasFinished = true
                        item.save()
                        achievementView.show("你完成了成就「运动达人」", item.desc)
                    }
                }
            }

        }
    }


}