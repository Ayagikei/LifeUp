package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport
import java.util.*

data class TaskModel(
        var content: String,
        var remark: String,
        var taskExpireTime: Date?,
        var taskRemindTime: Date?,
        var relatedAttribute1: String?,
        var relatedAttribute2: String?,
        var relatedAttribute3: String?,
        var taskUrgencyDegree: Int,
        var taskDifficultyDegree: Int,
        var taskFrequency: Int,
        var userId: Int?,
        var isShared: Boolean,
        var taskType: Int?
) : LitePalSupport() {

    var id: Long? = null
    var taskId: Long? = null
    var createdTime: Long = 0
    var updatedTime: Long = 0
    var expReward: Int = 0
    var startTime: Date = Date()
    var endDate: Date? = null
    var taskStatus: Int = 0

}