package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.TaskModel

interface TodoService {

    fun addTodoItem(taskModel: TaskModel)

    fun updateTodoItem(id: Long, taskModel: TaskModel): Boolean

    fun deleteTodoItem(id: Long?): Boolean

    fun getTodoList(): List<TaskModel>

    fun finishTodoItem(id: Long?): Boolean


}