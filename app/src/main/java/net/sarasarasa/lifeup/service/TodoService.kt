package net.sarasarasa.lifeup.service

import android.content.Context
import android.os.Handler
import net.sarasarasa.lifeup.models.CategoryModel
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.vo.TeamTaskVO
import java.util.*

interface TodoService {

    /** 新增一个待办事项，返回[TaskModel.id] **/
    fun addTodoItem(taskModel: TaskModel): Long?

    /** 更新指定[id]的待办事项，成功返回[Boolean]类型的true **/
    fun updateTodoItem(id: Long, taskModel: TaskModel): Boolean

    /** 删除指定[id]的待办事项，返回[Boolean]，成功返回true **/
    fun deleteTodoItem(id: Long?): Boolean

    /** 获取所有未完成事项的[List<TaskModel>] **/
    fun getUncompletedTodoList(isShowToast: Boolean): List<TaskModel>

    /** 获取所有非未完成事项（含逾期、放弃等）的[List<TaskModel>] **/
    fun getCompletedTodoList(limit: Int, offset: Int): List<TaskModel>

    /** 获取所有非未完成事项（含逾期、放弃等）的计数 **/
    fun countCompletedTodoList(): Int

    /** 根据[id: Long]获取该待办事项 **/
    fun getATodoItem(id: Long): TaskModel?

    /** 根据[id: Long]完成该待办事项，
     * 并且实现经验值增长 **/
    fun finishTodoItem(id: Long?): Boolean

    /** 根据[id: Long]撤销完成该待办事项，
     * 并且实现经验值减少 **/
    fun undoFinishTodoItem(id: Long?): Boolean

    /** 根据[id: Long]放弃该待办事项，
     * 并且实现经验值减少 **/
    fun giveUpTodoItem(id: Long?): Boolean

    /** 获取今天完成的事项和未完成事项的[Int]计数 **/
    fun getTodayTaskCount(): Int

    /** 获取今天完成的事项的[Int]计数 **/
    fun getTodayFinishCount(): Int

    /** 获取所有完成的事项的[Int]计数 **/
    fun getFinishCount(): Int

    /** 在[time:Long]时间设置内容为[id: Long]的事项的内容的提醒 **/
    fun setOrUpdateAlarm(time: Long, id: Long, context: Context): Boolean

    /** 根据[id: Long]重复该待办事项,
     * 返回[Boolean]代表操作是否成功 **/
    fun repeatTask(id: Long?): TaskModel?

    /** 检查并更新逾期情况，
     * 返回[Boolean]代表是否有逾期的待办事项 **/
    fun checkAndUpdateOverdueTask(uiHandler: Handler.Callback?): Boolean

    fun getGiveUpCount(): Int

    fun getOverdueCount(): Int

    fun addOrUpdateTeamTask(teamTaskVO: TeamTaskVO, passCategoryId: Long): Boolean

    fun getFinishTeamTaskCount(): Int

    fun deleteTeamTask()

    fun resetAllRemind(context: Context)

    fun deleteTeamTaskByTeamId(teamId: Long)

    fun changePriority(id: Long): Int

    fun restartTask(id: Long): Boolean

    fun getUncompletedTodoListWhichHaveBegun(isShowToast: Boolean): List<TaskModel>

    fun addGuideTask(): Boolean

    fun hideHistoryItem(id: Long): Int

    fun getFinishTaskCountByDate(cal: Calendar): Int

    fun listFinishTaskCountPastDays(days: Int): ArrayList<Int>

    fun addCategory(category: CategoryModel): Long?

    fun listCategory(): List<CategoryModel>

    fun getCategoryNameById(categoryId: Long): String

    fun moveToCategoryById(categoryId: Long, toDoItem: TaskModel): Boolean

    fun renameCategory(categoryId: Long, newName: String): Boolean

    fun deleteCategory(categoryId: Long): Boolean

    fun remakeTaskWhichIsRemakeFailed()

    fun setOverdueItemToFinish(id: Long?): Boolean
    fun getAllUncompletedTodoList(isShowToast: Boolean): List<TaskModel>
    fun getAllUncompletedTodoListWhichHaveBegun(isShowToast: Boolean): List<TaskModel>
}