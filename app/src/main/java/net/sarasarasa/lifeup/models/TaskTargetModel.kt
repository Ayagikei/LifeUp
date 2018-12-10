package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport

data class TaskTargetModel(
        var targetTimes: Int,
        var extraExpReward: Int
) : LitePalSupport() {
    var id: Long? = null


    override fun toString(): String {
        return "TaskTargetModel(targetTimes=$targetTimes, extraExpReward=$extraExpReward, id=$id)"
    }


}