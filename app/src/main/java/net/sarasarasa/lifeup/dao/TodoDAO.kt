package net.sarasarasa.lifeup.dao

import android.content.Context
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.utils.CalendarUtil
import org.litepal.FluentQuery
import org.litepal.LitePal
import java.util.*

class TodoDAO {
    private val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)

    fun saveTodoItem(taskModel: TaskModel): Long? {
        taskModel.save()
        return LitePal.findLast(TaskModel::class.java).id
    }

    fun deleteTodoItemById(id: Long): Int? {
        return LitePal.find(TaskModel::class.java, id)?.delete()
    }

    fun findAllUncompletedTodoItem(): List<TaskModel> {
        val categoryId = optionSharedPreferences.getLong("categoryId", 0L)
        if (categoryId == -1L) return findAllUncompletedTodoItemIgnoreCategory()

        val litePalWhere = if (categoryId == 0L)
            LitePal.where("taskStatus = ? and (categoryId = ? or categoryId is null)", "0", categoryId.toString())
        else LitePal.where("taskStatus = ? and categoryId = ?", "0", categoryId.toString())

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findAllUncompletedTodoItemIgnoreCategory(): List<TaskModel> {
        val litePalWhere = LitePal.where("taskStatus = ?", "0")

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    private fun getLitePalOrder(litePalWhere: FluentQuery): FluentQuery {
        val sortBy = optionSharedPreferences.getString("sortBy", "startTime")
        val isAsc = optionSharedPreferences.getBoolean("isAsc", true)

        return if (isAsc)
            when (sortBy) {
                "alpha" -> litePalWhere.order("priority desc,content COLLATE LOCALIZED asc")
                "startTime" -> litePalWhere.order("priority desc,startTime asc")
                "deadline" -> litePalWhere.order("priority desc,taskExpireTime asc")
                "createTime" -> litePalWhere.order("priority desc,id asc")
                "exp" -> litePalWhere.order("priority desc,expReward asc")
                else -> litePalWhere.order("priority desc,startTime asc")
            }
        else when (sortBy) {
            "alpha" -> litePalWhere.order("priority desc,content COLLATE LOCALIZED desc")
            "startTime" -> litePalWhere.order("priority desc,startTime desc")
            "deadline" -> litePalWhere.order("priority desc,taskExpireTime desc")
            "createTime" -> litePalWhere.order("priority desc,id desc")
            "exp" -> litePalWhere.order("priority desc,expReward desc")
            else -> litePalWhere.order("priority desc,startTime desc")
        }
    }


    fun findUncompletedTodoItemAfterDays(days: Int): List<TaskModel> {
        val cal = Calendar.getInstance()
        CalendarUtil.setToTheLastSecondOfTheDay(cal)
        cal.add(Calendar.DATE, days - 1)
        val lastSecOfThisDay = cal.timeInMillis
        val categoryId = optionSharedPreferences.getLong("categoryId", 0L)
        if (categoryId == -1L) return findUncompletedTodoItemAfterDaysIgnoreCategory(days)

        val litePalWhere = if (categoryId == 0L)
            LitePal.where("taskStatus = ? and startTime <= ? and (categoryId = ? or categoryId is null)", "0", lastSecOfThisDay.toString(), categoryId.toString())
        else LitePal.where("taskStatus = ? and startTime <= ? and categoryId = ?", "0", lastSecOfThisDay.toString(), categoryId.toString())

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findUncompletedTodoItemAfterDaysIgnoreCategory(days: Int): List<TaskModel> {
        val cal = Calendar.getInstance()
        CalendarUtil.setToTheLastSecondOfTheDay(cal)
        cal.add(Calendar.DATE, days - 1)
        val lastSecOfThisDay = cal.timeInMillis

        val litePalWhere = LitePal.where("taskStatus = ? and startTime <= ?", "0", lastSecOfThisDay.toString())

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findAllUncompletedTodoItemWhichHaveBegun(): List<TaskModel> {
        val cal = Calendar.getInstance()
        CalendarUtil.setToTheLastSecondOfTheDay(cal)
        val lastSecOfThisDay = cal.timeInMillis
        val categoryId = optionSharedPreferences.getLong("categoryId", 0L)
        if (categoryId == -1L) return findAllUncompletedTodoItemWhichHaveBegunIgnoreCategory()

        val litePalWhere = if (categoryId == 0L)
            LitePal.where("taskStatus = ? and startTime <= ? and (categoryId = ? or categoryId is null)", "0", lastSecOfThisDay.toString(), categoryId.toString())
        else LitePal.where("taskStatus = ? and startTime <= ? and categoryId = ?", "0", lastSecOfThisDay.toString(), categoryId.toString())

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findAllUncompletedTodoItemWhichHaveBegunIgnoreCategory(): List<TaskModel> {
        val cal = Calendar.getInstance()
        CalendarUtil.setToTheLastSecondOfTheDay(cal)
        val lastSecOfThisDay = cal.timeInMillis

        val litePalWhere = LitePal.where("taskStatus = ? and startTime <= ?", "0", lastSecOfThisDay.toString())

        return getLitePalOrder(litePalWhere).find(TaskModel::class.java)
    }

    fun findAllUncompletedAndNeedRemindTodoItem(time: Long): List<TaskModel> {
        return LitePal.where("taskStatus = ? and taskRemindTime >= ?", "0", time.toString()).find(TaskModel::class.java)
    }

    fun findAllCompletedTodoItem(limit: Int, offset: Int): List<TaskModel> {
        return LitePal.where("taskStatus != ? and (isDeleteRecord != ? or isDeleteRecord is null)", "0", "1")
                .order("endDate desc")
                .limit(limit)
                .offset(offset)
                .find(TaskModel::class.java)
    }

    fun countAllCompletedTodoItem(): Int {
        return LitePal.where("taskStatus != ? and (isDeleteRecord != ? or isDeleteRecord is null)", "0", "1")
                .order("endDate desc")
                .find(TaskModel::class.java)
                .count()
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
        return LitePal.where("taskStatus = ? and startTime > ?", "0", time.toString()).count(TaskModel::class.java)
    }

    fun getTodayFinishCount(time: Long): Int {
        return LitePal.where("endDate > ? and taskStatus = ?", time.toString(), "1").count(TaskModel::class.java)
    }

    fun getFinishCountByStartTimeAndEndTime(startTime: Long, endTime: Long): Int {
        return LitePal.where("endDate >= ? and endDate <= ? and taskStatus = ?", startTime.toString(), endTime.toString(), "1").count(TaskModel::class.java)
    }

    fun getOverdueItems(time: Long): List<TaskModel> {
        val cal = Calendar.getInstance()
        return LitePal.where("(taskExpireTime <= ? and taskStatus = ? and teamId = ? and (isUseSpecificExpireTime is null or isUseSpecificExpireTime = ?)) " +
                "or (endTime <= ? and taskStatus = ? and teamId != ?)" +
                "or (taskExpireTime <= ? and taskStatus = ? and teamId = ? and isUseSpecificExpireTime = ?)"
                , time.toString(), "0", "-1", "0", cal.timeInMillis.toString(), "0", "-1", cal.timeInMillis.toString(), "0", "-1", "1").find(TaskModel::class.java)
    }

    fun getNeedToRemakeItems(): List<TaskModel> {
        return LitePal.where("isNeedToRemake = ?", "true").find(TaskModel::class.java)
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

    fun countCategoryTask(categoryId: Long): Int {
        return LitePal.where("taskStatus = ? and categoryId = ?", "0", categoryId.toString()).count(TaskModel::class.java)
    }

}
