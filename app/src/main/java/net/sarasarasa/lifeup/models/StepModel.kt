package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport
import java.util.*

data class StepModel(var dailyStepCount: Long,
                     var totalStepCount: Long,
                     var isGotReward: Boolean,
                     var date: Date) : LitePalSupport() {
    var id: Long? = null
    var userId: Long? = null
    var isUserInput: Boolean = false

}