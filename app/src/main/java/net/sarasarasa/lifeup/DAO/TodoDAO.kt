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

    fun findAllUncompletedTodoItem(): List<TaskModel> {
        return LitePal.where("taskStatus = ?", "0").find(TaskModel::class.java)
    }

    fun findAllCompletedTodoItem(): List<TaskModel> {
        return LitePal.where("taskStatus != ?", "0").order("endDate desc").find(TaskModel::class.java)
    }

    fun findATodoItem(id: Long): TaskModel? {
        return LitePal.find(TaskModel::class.java, id)
    }
}
