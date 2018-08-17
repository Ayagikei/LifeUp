package net.sarasarasa.lifeup.service.impl

import net.sarasarasa.lifeup.DAO.TodoDAO
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
            taskDeadline = taskModel.taskDeadline
            relatedAttribute1 = taskModel.relatedAttribute1
            relatedAttribute2 = taskModel.relatedAttribute2
            relatedAttribute3 = taskModel.relatedAttribute3
            taskUrgencyLevel = taskModel.taskUrgencyLevel
            taskDifficultyLevel = taskModel.taskDifficultyLevel
            taskShared = taskModel.taskShared
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

    override fun getTodoList(): List<TaskModel> {
        return todoDAO.findAllTodoItem()
    }

    override fun getATodoItem(id: Long): TaskModel? {
        return todoDAO.findATodoItem(id)
    }

    override fun finishTodoItem(id: Long?): Boolean {
        if (id == null) return false

        with(todoDAO.findATodoItem(id) ?: return false)
        {
            isFinished = true
            save()
        }

        return true
    }

}