package net.sarasarasa.lifeup.service.impl

import android.text.format.DateUtils
import net.sarasarasa.lifeup.dao.StepDAO
import net.sarasarasa.lifeup.models.StepModel
import net.sarasarasa.lifeup.service.StepService
import java.util.*

class StepServiceImpl : StepService {

    private val stepDAO = StepDAO()
    val attributeService = AttributeServiceImpl()

    override fun updateAndGetTodayStepCount(step: Float): Long {
        val theLastStepRec = stepDAO.getTheLastStepRecord()

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        // 第一步：先判断有没有任何一条记录
        if (theLastStepRec != null) {
            // 第二步：如果有记录，判断是不是今天的记录
            if (DateUtils.isToday(theLastStepRec.date.time)) {
                // 第三步：判断有没有手动输入
                if (theLastStepRec.isUserInput) {
                    // 手动输入的话，直接返回手动输入的数据
                    return theLastStepRec.dailyStepCount
                } else {
                    // 第四步：以总步数判断是否重启过
                    if (step.toLong() < theLastStepRec.totalStepCount) {
                        // 重启过，将所有步数加入当天步数，并且重置总步数统计
                        theLastStepRec.dailyStepCount += step.toLong()
                    } else {
                        // 没有重启过，将差值加入当天步数
                        theLastStepRec.dailyStepCount += step.toLong() - theLastStepRec.totalStepCount
                    }
                    // 第五步：保存并且返回
                    theLastStepRec.totalStepCount = step.toLong()
                    theLastStepRec.save()
                    return theLastStepRec.dailyStepCount
                }
            } else {
                // 如果不是今天的记录，那么判断总步数
                if (step.toLong() < theLastStepRec.totalStepCount) {
                    // 重启过，将当前步数当做当天步数（除非步数过多）
                    val newStepRecord = StepModel(step.toLong(), step.toLong(), false, cal.time)
                    if (step.toLong() > 50000L) {
                        newStepRecord.dailyStepCount = 0L
                    }

                    newStepRecord.save()
                    return newStepRecord.dailyStepCount
                } else {
                    // 没有重启过
                    val newStepRecord =
                            StepModel(step.toLong() - theLastStepRec.totalStepCount,
                                    step.toLong(),
                                    false,
                                    cal.time)

                    if (newStepRecord.dailyStepCount > 50000L) {
                        newStepRecord.dailyStepCount = 0L
                    }

                    newStepRecord.save()
                    return newStepRecord.dailyStepCount
                }
            }
        } else {
            // 如果一条记录都没有，直接插入新纪录
            val newStepRecord = StepModel(0, step.toLong(), false, cal.time)
            newStepRecord.save()
            return newStepRecord.dailyStepCount
        }
    }

    override fun getRewardByStep(): Long {
        val theLastStepRec = stepDAO.getTheLastStepRecord()

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        // 第一步：先判断有没有任何一条记录
        if (theLastStepRec != null) {
            // 第二步：如果有记录，判断是不是今天的记录
            if (DateUtils.isToday(theLastStepRec.date.time)) {
                if (!theLastStepRec.isGotReward) {
                    var exp = 0
                    when {
                        theLastStepRec.dailyStepCount in 2500..5000 -> exp = 150
                        theLastStepRec.dailyStepCount in 5000..10000 -> exp = 400
                        theLastStepRec.dailyStepCount in 10000..20000 -> exp = 950
                        theLastStepRec.dailyStepCount > 20000 -> exp = 2000
                    }

                    val attrs = ArrayList<String>(Arrays.asList("strength"))
                    attributeService.increaseMultiExp(attrs, exp, "步数兑换力量经验值")

                    theLastStepRec.isGotReward = true
                    theLastStepRec.save()
                    return exp.toLong()
                }
            }
        }

        return 0
    }

    override fun getTodayStepCount(): Long {
        val theLastStepRec = stepDAO.getTheLastStepRecord()

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        return if (theLastStepRec != null
                && DateUtils.isToday(theLastStepRec.date.time))
            theLastStepRec.dailyStepCount
        else 0
    }

    override fun isTodayGotReward(): Boolean {
        val theLastStepRec = stepDAO.getTheLastStepRecord()

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        return theLastStepRec != null
                && DateUtils.isToday(theLastStepRec.date.time)
                && theLastStepRec.isGotReward
    }

    override fun userInputTodayStepData(step: Long): Boolean {
        if (step < 0) return false
        val theLastStepRec = stepDAO.getTheLastStepRecord()

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        if (theLastStepRec != null) {
            return if (DateUtils.isToday(theLastStepRec.date.time)) {
                theLastStepRec.dailyStepCount = step
                theLastStepRec.isUserInput = true
                theLastStepRec.save()
            } else {
                val newStepRecord = StepModel(step, theLastStepRec.totalStepCount, false, cal.time)
                newStepRecord.isUserInput = true
                newStepRecord.save()
            }
        } else {
            val newStepRecord = StepModel(step, step, false, cal.time)
            newStepRecord.isUserInput = true
            return newStepRecord.save()
        }
    }

    override fun getDailyStepByDate(cal: Calendar): Long {
        with(cal) {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }
        val firstSecOfThisDay = cal.timeInMillis

        with(cal) {
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
        }
        val lastSecOfThisDay = cal.timeInMillis

        return stepDAO.getStepByStartTimeAndEndTime(firstSecOfThisDay, lastSecOfThisDay)?.dailyStepCount
                ?: 0L
    }

    override fun listFinishTaskCountPastDays(days: Int): ArrayList<Long> {
        val cal = Calendar.getInstance()
        val stepList = ArrayList<Long>()

        for (i in 1..days) {
            stepList.add(getDailyStepByDate(cal))
            cal.add(Calendar.DATE, -1)
        }
        stepList.reverse()
        return stepList
    }
}