package net.sarasarasa.lifeup.DAO

import net.sarasarasa.lifeup.models.TaskModel
import org.litepal.LitePal

class TodoDAO {
    fun saveTodoItem(taskModel: TaskModel) {
        taskModel.save()
    }

    fun deleteTodoItemById(id: Long): Int? {
        return LitePal.find(TaskModel::class.java, id)?.delete()
    }

    fun findAllTodoItem(): List<TaskModel> {
        return LitePal.findAll(TaskModel::class.java)
    }

    fun findATodoItem(id: Long): TaskModel? {
        return LitePal.find(TaskModel::class.java, id)
    }
}
