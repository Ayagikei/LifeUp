package net.sarasarasa.lifeup.service.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import net.sarasarasa.lifeup.DAO.TodoDAO
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.receiver.AlarmReceiver
import net.sarasarasa.lifeup.service.TodoService
import java.util.*

class TodoServiceImpl : TodoService {

    private val todoDAO = TodoDAO()
    private val attributeService = AttributeServiceImpl()

    override fun addTodoItem(taskModel: TaskModel): Long? {
        taskModel.createdTime = Calendar.getInstance().timeInMillis
        return todoDAO.saveTodoItem(taskModel)
    }

    override fun updateTodoItem(id: Long, taskModel: TaskModel): Boolean {
        val existTodoItem = todoDAO.findATodoItem(id) ?: return false

        with(existTodoItem) {
            //复制所有输入信息
            content = taskModel.content
            remark = taskModel.remark
            taskExpireTime = taskModel.taskExpireTime
            taskRemindTime = taskModel.taskRemindTime
            relatedAttribute1 = taskModel.relatedAttribute1
            relatedAttribute2 = taskModel.relatedAttribute2
            relatedAttribute3 = taskModel.relatedAttribute3
            taskUrgencyDegree = taskModel.taskUrgencyDegree
            taskDifficultyDegree = taskModel.taskDifficultyDegree
            taskFrequency = taskModel.taskFrequency
            userId = taskModel.userId
            isShared = taskModel.isShared
            taskType = taskModel.taskType
            createdTime = taskModel.createdTime
            expReward = taskModel.expReward
            endDate = taskModel.endDate
            taskStatus = taskModel.taskStatus

            //更新UpdatedTime
            updatedTime = Calendar.getInstance().timeInMillis
        }

        todoDAO.saveTodoItem(existTodoItem)
        return true
    }

    override fun deleteTodoItem(id: Long?): Boolean {
        if (id == null) return false

        val ans = todoDAO.deleteTodoItemById(id) ?: return false
        return ans > 0
    }

    override fun getUncompletedTodoList(): List<TaskModel> {
        return todoDAO.findAllUncompletedTodoItem()
    }

    override fun getCompletedTodoList(): List<TaskModel> {
        return todoDAO.findAllCompletedTodoItem()
    }

    override fun getATodoItem(id: Long): TaskModel? {
        return todoDAO.findATodoItem(id)
    }

    override fun finishTodoItem(id: Long?): Boolean {
        if (id == null) return false

        with(todoDAO.findATodoItem(id) ?: return false)
        {
            taskStatus = ToDoItemConstants.COMPLETED
            updatedTime = Calendar.getInstance().timeInMillis
            endDate = Date()
            save()

            attributeService.increaseExp(this.relatedAttribute1 ?: "", this.expReward)
            attributeService.increaseExp(this.relatedAttribute2 ?: "", this.expReward)
            attributeService.increaseExp(this.relatedAttribute3 ?: "", this.expReward)
        }



        return true
    }

    override fun undoFinishTodoItem(id: Long?): Boolean {
        if (id == null) return false

        with(todoDAO.findATodoItem(id) ?: return false)
        {
            taskStatus = ToDoItemConstants.UNCOMPLETED
            updatedTime = Calendar.getInstance().timeInMillis
            endDate = Date()
            save()
        }

        return true
    }

    override fun giveUpTodoItem(id: Long?): Boolean {
        if (id == null) return false

        with(todoDAO.findATodoItem(id) ?: return false)
        {
            taskStatus = ToDoItemConstants.GIVE_UP
            updatedTime = Calendar.getInstance().timeInMillis
            endDate = Date()
            save()
        }

        return true
    }

    override fun getTodayTaskCount(): Int {
        val cal = Calendar.getInstance()
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val millisTime = cal.timeInMillis
        //总数量为未完成的+今天已经完成的
        return todoDAO.getUnFinishTaskCount(millisTime) + getTodayFinishCount()
    }


    override fun getTodayFinishCount(): Int {
        val cal = Calendar.getInstance()
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val millisTime = cal.timeInMillis

        return todoDAO.getTodayFinishCount(millisTime)
    }

    override fun setOrUpdateAlarm(time: Long, id: Long, context: Context): Boolean {
        val taskModel = getATodoItem(id)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmReceiver::class.java)
        notificationIntent.putExtra("id", taskModel?.id)
        notificationIntent.putExtra("content", taskModel?.content)
        val broadcast = PendingIntent.getBroadcast(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, broadcast)

        return true
    }

    override fun repeatTask(id: Long): Boolean {
        val origin = todoDAO.findATodoItem(id) ?: return false

        val taskModel = TaskModel(
                origin.content,
                origin.remark,
                origin.taskExpireTime,
                origin.taskRemindTime,
                origin.relatedAttribute1,
                origin.relatedAttribute2,
                origin.relatedAttribute3,
                origin.taskUrgencyDegree,
                origin.taskDifficultyDegree,
                origin.taskFrequency,
                origin.userId,
                origin.isShared,
                origin.taskType
        )

        taskModel.taskId = origin.taskId
        taskModel.createdTime = Calendar.getInstance().timeInMillis
        taskModel.updatedTime = Calendar.getInstance().timeInMillis
        taskModel.expReward = origin.expReward

        val newExpireTime = Calendar.getInstance()
        newExpireTime.time = origin.taskExpireTime
        newExpireTime.add(Calendar.DATE, origin.taskFrequency)
        taskModel.taskExpireTime = newExpireTime.time

        val newRemindTime = Calendar.getInstance()
        newRemindTime.time = origin.taskRemindTime
        newRemindTime.add(Calendar.DATE, origin.taskFrequency)
        taskModel.taskRemindTime = newRemindTime.time

        return true
    }
}