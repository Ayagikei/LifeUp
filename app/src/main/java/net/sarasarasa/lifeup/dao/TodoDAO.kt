package net.sarasarasa.lifeup.dao

import android.content.Context
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.models.TaskModel
import org.litepal.FluentQuery
import org.litepal.LitePal
import java.util.*

class TodoDAO {
    fun saveTodoItem(taskModel: TaskModel): Long? {
        taskModel.save()
        return LitePal.findLast(TaskModel::class.java).id
    }

    fun deleteTodoItemById(id: Long): Int? {
        return LitePal.find(TaskModel::class.java, id)?.delete()
    }

    fun findAllUncompletedTodoItem(): List<TaskModel> {
        val litePalWhere = LitePal.where("taskStatus = ?", "0")

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    private fun getLitePalOrder(litePalWhere: FluentQuery): FluentQuery {
        val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)
        val classBy = optionSharedPreferences.getString("sortBy", "startTime")
        val isAsc = optionSharedPreferences.getBoolean("isAsc", true)

        return if (isAsc)
            when (classBy) {
                "startTime" -> litePalWhere.order("priority desc,startTime asc")
                "deadline" -> litePalWhere.order("priority desc,taskExpireTime asc")
                "createTime" -> litePalWhere.order("priority desc,id asc")
                "exp" -> litePalWhere.order("priority desc,expReward asc")
                else -> litePalWhere.order("priority desc,startTime asc")
            }
        else when (classBy) {
            "startTime" -> litePalWhere.order("priority desc,startTime desc")
            "deadline" -> litePalWhere.order("priority desc,taskExpireTime desc")
            "createTime" -> litePalWhere.order("priority desc,id desc")
            "exp" -> litePalWhere.order("priority desc,expReward desc")
            else -> litePalWhere.order("priority desc,startTime desc")
        }

    }

    fun findUncompletedTodoItemAfterDays(days: Int): List<TaskModel> {
        val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)

        val classBy = optionSharedPreferences.getString("sortBy", "startTime")
        val isAsc = optionSharedPreferences.getBoolean("isAsc", true)

        val cal = Calendar.getInstance()
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            add(Calendar.DATE, days - 1)
        }
        val lastSecOfThisDay = cal.timeInMillis
        val litePalWhere = LitePal.where("taskStatus = ? and startTime < ?", "0", lastSecOfThisDay.toString())
        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findAllUncompletedTodoItemWhichHaveBegun(): List<TaskModel> {
        val cal = Calendar.getInstance()
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val lastSecOfThisDay = cal.timeInMillis
        val litePalWhere = LitePal.where("taskStatus = ? and startTime <= ?", "0", lastSecOfThisDay.toString())

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findAllUncompletedAndNeedRemindTodoItem(time: Long): List<TaskModel> {
        return LitePal.where("taskStatus = ? and taskRemindTime > ?", "0", time.toString()).find(TaskModel::class.java)
    }

    fun findAllCompletedTodoItem(): List<TaskModel> {
        return LitePal.where("taskStatus != ? and (isDeleteRecord != ? or isDeleteRecord is null)", "0", "1").order("endDate desc").find(TaskModel::class.java)
    }

    fun findAllTeamTodoItem(): List<TaskModel> {
        return LitePal.where("teamId != ? and taskStatus = ?", "-1", "0").find(TaskModel::class.java)
    }

    fun findTeamTodoItem(teamId: Long): TaskModel? {
        return LitePal.where("teamId = ? and taskStatus = ?", teamId.toString(), "0").findLast(TaskModel::class.java)
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

    fun getFinishCountByStartTimeAndEndTime(startTime: Long, endTime: Long): Int {
        return LitePal.where("endDate > ? and endDate < ? and taskStatus = ?", startTime.toString(), endTime.toString(), "1").count(TaskModel::class.java)
    }

    fun getOverdueItems(time: Long): List<TaskModel> {
        val listTotalItem = LitePal.where("taskExpireTime <= ? and taskStatus = ? and teamId = ?", time.toString(), "0", "-1").find(TaskModel::class.java)
        val cal = Calendar.getInstance()
        val listNetworkItem = LitePal.where("endTime <= ? and taskStatus = ? and teamId != ?", cal.timeInMillis.toString(), "0", "-1").find(TaskModel::class.java)
        listTotalItem.addAll(listNetworkItem)
        return listTotalItem
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

    fun getFinishTeamTaskCount(): Int {
        return LitePal.where("taskStatus = ? and teamId != ?", "1", "-1").count(TaskModel::class.java)
    }

}
