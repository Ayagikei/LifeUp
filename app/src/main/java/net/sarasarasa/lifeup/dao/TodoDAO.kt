package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.TaskModel
import org.litepal.LitePal

class TodoDAO {
    fun saveTodoItem(taskModel: TaskModel): Long? {
        taskModel.save()
        return LitePal.findLast(TaskModel::class.java).id
    }

    fun deleteTodoItemById(id: Long): Int? {
        return LitePal.find(TaskModel::class.java, id)?.delete()
    }

    fun findAllUncompletedTodoItem(): List<TaskModel> {
        return LitePal.where("taskStatus = ?", "0").order("startTime asc").find(TaskModel::class.java)
    }

    fun findAllCompletedTodoItem(): List<TaskModel> {
        return LitePal.where("taskStatus != ?", "0").order("endDate desc").find(TaskModel::class.java)
    }

    fun findATodoItem(id: Long): TaskModel? {
        return LitePal.find(TaskModel::class.java, id)
    }

    fun getUnFinishTaskCount(time: Long): Int {
        return LitePal.where("taskStatus = ?", "0").count(TaskModel::class.java)
    }

    /** time应为当天最后一秒 **/
    fun getUnStartedTaskCount(time: Long): Int {
        return LitePal.where("taskStatus = ? and startTime>?", "0", time.toString()).count(TaskModel::class.java)
    }

    fun getTodayFinishCount(time: Long): Int {
        return LitePal.where("endDate > ? and taskStatus = ?", time.toString(), "1").count(TaskModel::class.java)
    }

    fun getOverdueItems(time: Long): List<TaskModel> {
        return LitePal.where("taskExpireTime <= ? and taskStatus = ?", time.toString(), "0").find(TaskModel::class.java)
    }

    fun getFinishCount(): Int {
        return LitePal.where("taskStatus = ?", "1").count(TaskModel::class.java)
    }

    fun getGiveUpCount(): Int {
        return LitePal.where("taskStatus = ?", "3").count(TaskModel::class.java)
    }

    fun getOverdueCount(): Int {
        return LitePal.where("taskStatus = ?", "2").count(TaskModel::class.java)
    }

    fun getOneTeamTaskById(teamId: Long, teamRecordId: Long): TaskModel? {
        if (teamId == -1L || teamRecordId == -1L)
            return null

        return LitePal.where("teamId = ? and teamRecordId = ?", teamId.toString(), teamRecordId.toString()).findFirst(TaskModel::class.java)
    }

}
