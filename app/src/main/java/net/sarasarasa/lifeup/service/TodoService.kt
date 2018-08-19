package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.TaskModel

interface TodoService {

    fun addTodoItem(taskModel: TaskModel)

    fun updateTodoItem(id: Long, taskModel: TaskModel): Boolean

    fun deleteTodoItem(id: Long?): Boolean

    fun getUncompletedTodoList(): List<TaskModel>

    fun getCompletedTodoList(): List<TaskModel>

    fun getATodoItem(id: Long): TaskModel?

    fun finishTodoItem(id: Long?): Boolean

    fun giveUpTodoItem(id: Long?): Boolean


}