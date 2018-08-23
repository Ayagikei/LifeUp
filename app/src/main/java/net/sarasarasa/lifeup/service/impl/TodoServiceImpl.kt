package net.sarasarasa.lifeup.service.impl

import net.sarasarasa.lifeup.DAO.TodoDAO
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.TodoService
import java.util.*

class TodoServiceImpl : TodoService {

    private val todoDAO = TodoDAO()

    override fun addTodoItem(taskModel: TaskModel) {
        taskModel.createdTime = Calendar.getInstance().timeInMillis
        todoDAO.saveTodoItem(taskModel)
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
            isShared = taskModel.isShared
            taskType = taskModel.taskType

            //更新UpdatedTime
            updatedTime = Calendar.getInstance().timeInMillis
        }

        todoDAO.saveTodoItem(taskModel)
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
        return todoDAO.getTodayTaskCount(millisTime)
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



}