package net.sarasarasa.lifeup.service

import java.util.*

interface StepService {

    fun updateAndGetTodayStepCount(step: Float): Long

    fun getRewardByStep(): Long

    fun getTodayStepCount(): Long

    fun isTodayGotReward(): Boolean

    fun userInputTodayStepData(step: Long): Boolean

    fun getDailyStepByDate(cal: Calendar): Long

    fun listFinishTaskCountPastDays(days: Int): ArrayList<Long>


}