package net.sarasarasa.lifeup.service

interface StepService {

    fun updateAndGetTodayStepCount(step: Float): Long

    fun getRewardByStep(): Long

    fun getTodayStepCount(): Long

    fun isTodayGotReward(): Boolean

}