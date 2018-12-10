package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.TaskTargetModel
import org.litepal.LitePal

class TaskTargetDAO {

    fun getTaskTargetById(id: Long): TaskTargetModel? {
        return LitePal.where("id = ?", id.toString()).findLast(TaskTargetModel::class.java)
    }
}