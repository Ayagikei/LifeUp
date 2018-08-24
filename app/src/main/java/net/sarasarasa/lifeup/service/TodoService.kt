package net.sarasarasa.lifeup.service

import android.content.Context
import net.sarasarasa.lifeup.models.TaskModel

interface TodoService {

    fun addTodoItem(taskModel: TaskModel): Long?

    fun updateTodoItem(id: Long, taskModel: TaskModel): Boolean

    fun deleteTodoItem(id: Long?): Boolean

    fun getUncompletedTodoList(): List<TaskModel>

    fun getCompletedTodoList(): List<TaskModel>

    fun getATodoItem(id: Long): TaskModel?

    fun finishTodoItem(id: Long?): Boolean

    fun undoFinishTodoItem(id: Long?): Boolean

    fun giveUpTodoItem(id: Long?): Boolean

    fun getTodayTaskCount(): Int

    fun getTodayFinishCount(): Int

    fun setOrUpdateAlarm(time: Long, id: Long, context: Context): Boolean

    fun repeatTask(id: Long?): Boolean

}