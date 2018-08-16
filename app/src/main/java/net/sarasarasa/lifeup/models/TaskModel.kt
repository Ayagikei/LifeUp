package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport
import java.util.*

data class TaskModel(
        var content: String,
        var remark: String,
        var taskDeadline: Date?,
        var relatedAttribute1: String?,
        var relatedAttribute2: String?,
        var relatedAttribute3: String?,
        var taskUrgencyLevel: Int,
        var taskDifficultyLevel: Int,
        var sponsorId: Int?,
        var taskShared: Boolean,
        var taskType: Int?
) : LitePalSupport() {

    var id: Long? = null
    var taskId: Long? = null
    var createdTime: Long = 0
    var updatedTime: Long = 0
    var isFinished: Boolean = false

}