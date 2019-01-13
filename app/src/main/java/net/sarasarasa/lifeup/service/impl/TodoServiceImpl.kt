package net.sarasarasa.lifeup.service.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.OUT_OF_DATE
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.UNCOMPLETED
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.dao.TodoDAO
import net.sarasarasa.lifeup.instance.RetrofitInstance
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.network.TeamNetwork
import net.sarasarasa.lifeup.receiver.AlarmReceiver
import net.sarasarasa.lifeup.service.TodoService
import net.sarasarasa.lifeup.utils.DateUtil
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import net.sarasarasa.lifeup.vo.ResultVO
import net.sarasarasa.lifeup.vo.TeamTaskVO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TodoServiceImpl : TodoService {

    private val todoDAO = TodoDAO()
    private val taskTargetDAO = TaskTargetDAO()
    private val attributeService = AttributeServiceImpl()
    private val userService = UserServiceImpl()


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

            // 更新UpdatedTime
            updatedTime = Calendar.getInstance().timeInMillis
            // 开始时间也支持修改
            startTime = taskModel.startTime
        }


        todoDAO.saveTodoItem(existTodoItem)
        return true
    }

    override fun deleteTodoItem(id: Long?): Boolean {
        if (id == null) return false

        val ans = todoDAO.deleteTodoItemById(id) ?: return false
        return ans > 0
    }

    override fun getUncompletedTodoList(isShowToast: Boolean): List<TaskModel> {
        if (checkAndUpdateOverdueTask()) {
            if (isShowToast) ToastUtils.showLongToast("你有代办事项逾期了！请前往[历史]查看。")
        }

        val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)

        val classBy = optionSharedPreferences.getString("classBy", "all")

        return when (classBy) {
            "all" -> todoDAO.findAllUncompletedTodoItem()
            "today" -> todoDAO.findAllUncompletedTodoItemWhichHaveBegun()
            else -> todoDAO.findUncompletedTodoItemAfterDays(7)
        }

    }


    override fun getUncompletedTodoListWhichHaveBegun(isShowToast: Boolean): List<TaskModel> {
        if (checkAndUpdateOverdueTask()) {
            if (isShowToast) ToastUtils.showLongToast("你有代办事项逾期了！请前往[历史]查看。")
        }

        return todoDAO.findAllUncompletedTodoItemWhichHaveBegun()
    }

    override fun getCompletedTodoList(limit: Int, offset: Int): List<TaskModel> {
        return todoDAO.findAllCompletedTodoItem(limit, offset)
    }

    override fun countCompletedTodoList(): Int {
        return todoDAO.countAllCompletedTodoItem()
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

            val attrs = ArrayList<String>(Arrays.asList(this.relatedAttribute1, this.relatedAttribute2, this.relatedAttribute3))
            attributeService.increaseMultiExp(attrs, this.expReward, "完成事项「${this.content}」")
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

            //撤销经验
            val attrs = ArrayList<String>(Arrays.asList(this.relatedAttribute1, this.relatedAttribute2, this.relatedAttribute3))
            attributeService.decreaseMultiExp(attrs, this.expReward, "撤销完成事项「${this.content}」")

            if (this.nextTaskId != null && this.nextTaskId is Long && this.nextTaskId != 0L && this.nextTaskId != -1L) {
                val nextTask = todoDAO.findATodoItem(this.nextTaskId!!)
                nextTask?.delete()
            }
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

            //放弃任务损失经验值
            val attrs = ArrayList<String>(Arrays.asList(this.relatedAttribute1, this.relatedAttribute2, this.relatedAttribute3))
            attributeService.decreaseMultiExp(attrs, this.expReward / 5, "放弃完成事项「${this.content}」")
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

        with(cal) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val lastSecOfThisDay = cal.timeInMillis

        //总数量为未完成的+今天已经完成的-今天未开始的任务
        return todoDAO.getUnFinishTaskCount(millisTime) + getTodayFinishCount() - todoDAO.getUnStartedTaskCount(lastSecOfThisDay)
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

    override fun getFinishCount(): Int {
        return todoDAO.getFinishCount()
    }

    override fun getFinishTeamTaskCount(): Int {
        return todoDAO.getFinishTeamTaskCount()
    }

    override fun getGiveUpCount(): Int {
        return todoDAO.getGiveUpCount()
    }

    override fun getOverdueCount(): Int {
        return todoDAO.getOverdueCount()
    }

    override fun setOrUpdateAlarm(time: Long, id: Long, context: Context): Boolean {
        val taskModel = getATodoItem(id)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, AlarmReceiver::class.java)
        notificationIntent.putExtra("id", taskModel?.id)
        notificationIntent.putExtra("content", taskModel?.content)
        notificationIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        val broadcast = PendingIntent.getBroadcast(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, broadcast)

        return true
    }

    override fun repeatTask(id: Long?): Boolean {
        if (id == null) return false
        val origin = todoDAO.findATodoItem(id) ?: return false

        if (origin.taskFrequency == 0) return false

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
        taskModel.priority = origin.priority
        taskModel.currentTimes = origin.currentTimes + 1
        taskModel.taskTargetId = origin.taskTargetId

        //最后一次事项增加奖励
        if (taskModel.taskTargetId != null && taskModel.taskTargetId is Long) {
            val taskTarget = taskTargetDAO.getTaskTargetById(taskModel.taskTargetId!!)
            if (taskTarget?.targetTimes == taskModel.currentTimes) {
                taskModel.expReward += taskTarget.extraExpReward
            }
        }

        if (origin.taskFrequency != -1) {
            val newExpireTime = Calendar.getInstance()
            newExpireTime.time = origin.taskExpireTime
            newExpireTime.add(Calendar.DATE, origin.taskFrequency)
            taskModel.taskExpireTime = newExpireTime.time

            val newStartTime = Calendar.getInstance()
            newStartTime.time = origin.startTime
            newStartTime.set(Calendar.HOUR_OF_DAY, 0)
            newStartTime.set(Calendar.MINUTE, 0)
            newStartTime.set(Calendar.SECOND, 0)
            newStartTime.add(Calendar.DATE, origin.taskFrequency)
            taskModel.startTime = newStartTime.time
        }

        if (origin.taskRemindTime != null) {
            val newRemindTime = Calendar.getInstance()
            newRemindTime.time = origin.taskRemindTime
            newRemindTime.add(Calendar.DATE, origin.taskFrequency)
            taskModel.taskRemindTime = newRemindTime.time
        }


        taskModel.save()

        //保存下一次的id，以便撤销
        origin.nextTaskId = taskModel.id
        origin.save()

        return true
    }

    override fun checkAndUpdateOverdueTask(): Boolean {
        //期限当天不算逾期，第二天才算，此处做处理
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)

        val list = todoDAO.getOverdueItems(calendar.timeInMillis)

        return if (list.isEmpty()) false
        else {
            for (e in list) {
                e.taskStatus = ToDoItemConstants.OUT_OF_DATE
                e.endDate = Calendar.getInstance().time
                e.updatedTime = Calendar.getInstance().timeInMillis
                e.save()

                //经验值惩罚
                val attrs = ArrayList<String>(Arrays.asList(e.relatedAttribute1, e.relatedAttribute2, e.relatedAttribute3))
                attributeService.decreaseMultiExp(attrs, e.expReward / 5, "逾期事项「${e.content}」")

                val isDefaultRemake = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE).getBoolean("isDefaultRemake", true)
                if (isDefaultRemake && e.taskFrequency != 0 && e.taskFrequency != -1)
                    if (e.teamId == -1L) {
                        e.id?.let { restartTask(it) }
                    } else {
                        val retrofit = RetrofitInstance.getInstance()
                        val network: TeamNetwork = retrofit.create(TeamNetwork::class.java)
                        val call = network.getNextTeamTask(userService.getToken(), e.teamId)

                        call.enqueue(object : Callback<ResultVO<TeamTaskVO>> {
                            override fun onFailure(call: Call<ResultVO<TeamTaskVO>>?, t: Throwable?) {
                            }

                            override fun onResponse(call: Call<ResultVO<TeamTaskVO>>, response: Response<ResultVO<TeamTaskVO>>) {
                                val responseBody = response.body()

                                if (responseBody?.msg.equals("success")) {
                                    val teamTaskVO = responseBody?.data
                                    if (teamTaskVO != null) {
                                        addOrUpdateTeamTask(teamTaskVO)
                                    }
                                }
                            }
                        })

                    }

            }
            true
        }


    }


    /** 加入或创建团队的时候调用 **/
    override fun addOrUpdateTeamTask(teamTaskVO: TeamTaskVO): Boolean {
        val teamId: Long = teamTaskVO.teamId ?: -1L

        if (teamId == -1L)
            return false

        //查看是否能找到
        val taskModel = todoDAO.getOneTeamTaskById(teamId, teamTaskVO.teamRecordId)
        Log.i("findTaskModel", taskModel.toString())

        val expireTime = teamTaskVO.nextEndTime
        val cal = Calendar.getInstance()
        cal.time = expireTime
        cal.add(Calendar.DATE, 0)

        //不存在时，新增
        if (taskModel == null) {
            val newTaskModel = TaskModel(
                    teamTaskVO.teamTitle ?: "",
                    "",
                    cal.time,
                    null,
                    teamTaskVO.rewardAttrs.getOrNull(0),
                    teamTaskVO.rewardAttrs.getOrNull(1),
                    teamTaskVO.rewardAttrs.getOrNull(2),
                    0,
                    0,
                    teamTaskVO.teamFreq ?: 0,
                    -1,
                    false,
                    null
            )

            with(newTaskModel) {
                newTaskModel.teamId = teamTaskVO.teamId ?: -1
                startTime = teamTaskVO.nextStartTime ?: Date()
                endTime = teamTaskVO.nextEndTime ?: Date()
                expReward = teamTaskVO.rewardExp ?: 0
                teamRecordId = teamTaskVO.teamRecordId
            }

            newTaskModel.save()
            WidgetUtils.updateWidgets(LifeUpApplication.getLifeUpApplication())

            return true
        }
        // 如果事项是逾期状态，恢复为未完成
        if (taskModel.taskStatus == OUT_OF_DATE) {
            taskModel.taskStatus = UNCOMPLETED
            taskModel.save()
        }

        return false
    }

    override fun deleteTeamTask() {
        val teamTaskList = todoDAO.findAllTeamTodoItem()
        for (teamTask in teamTaskList) {
            teamTask.delete()
        }
    }

    override fun deleteTeamTaskByTeamId(teamId: Long) {
        val teamTask = todoDAO.findTeamTodoItem(teamId)

        teamTask?.delete()
    }

    override fun resetAllRemind(context: Context) {
        val cal = Calendar.getInstance()
        val teamTaskList = todoDAO.findAllUncompletedAndNeedRemindTodoItem(cal.timeInMillis)
        for (teamTask in teamTaskList) {
            teamTask.id?.let { setOrUpdateAlarm(teamTask.taskRemindTime!!.time, it, context) }
        }
    }

    override fun changePriority(id: Long): Int {
        with(todoDAO.findATodoItem(id) ?: return -1)
        {
            priority = if (priority != 1) {
                1
            } else 0

            updatedTime = Calendar.getInstance().timeInMillis
            save()

            return priority as Int
        }
    }

    override fun restartTask(id: Long): Boolean {
        if (id == null) return false

        val origin = todoDAO.findATodoItem(id) ?: return false
        if (origin.teamId != -1L) return false

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
        taskModel.priority = origin.priority
        taskModel.currentTimes = origin.currentTimes
        taskModel.taskTargetId = origin.taskTargetId
        taskModel.completeReward = origin.completeReward


        //重设的时候，把单次和多次事项当做每日事项处理
        if (origin.taskFrequency == 0 || origin.taskFrequency == -1) {
            origin.taskFrequency = 1
        }

        val newExpireTime = Calendar.getInstance()
        newExpireTime.time = origin.taskExpireTime
        val iExpireTimes = DateUtil.getDiscrepantDays(newExpireTime.time, Calendar.getInstance().time) / origin.taskFrequency
        if (iExpireTimes == 0) newExpireTime.add(Calendar.DATE, origin.taskFrequency * 1)
        else newExpireTime.add(Calendar.DATE, origin.taskFrequency * iExpireTimes)
        taskModel.taskExpireTime = newExpireTime.time

        val newStartTime = Calendar.getInstance()
        newStartTime.time = origin.startTime
        newStartTime.set(Calendar.HOUR_OF_DAY, 0)
        newStartTime.set(Calendar.MINUTE, 0)
        newStartTime.set(Calendar.SECOND, 0)
        val iStartTimes = DateUtil.getDiscrepantDays(newStartTime.time, Calendar.getInstance().time) / origin.taskFrequency
        if (iExpireTimes == 0) newStartTime.add(Calendar.DATE, origin.taskFrequency * 1)
        else newStartTime.add(Calendar.DATE, origin.taskFrequency * iStartTimes)
        taskModel.startTime = newStartTime.time


        if (origin.taskRemindTime != null) {
            val newRemindTime = Calendar.getInstance()
            newRemindTime.time = origin.taskRemindTime
            newRemindTime.add(Calendar.DATE, origin.taskFrequency)
            taskModel.taskRemindTime = newRemindTime.time
        }

        taskModel.save()

        origin.nextTaskId = taskModel.id
        origin.save()

        return true
    }

    override fun addGuideTask(): Boolean {
        val task = TaskModel(
                "开始使用人升",
                "点击小圆圈完成待办事项。\n长按可以进行置顶、编辑、放弃、删除等操作。",
                null,
                null,
                "endurance",
                null,
                null,
                1,
                1,
                0,
                0,
                false,
                null
        )

        task.expReward = 130
        return task.save()
    }

    override fun hideHistoryItem(id: Long): Int {
        with(todoDAO.findATodoItem(id) ?: return -1)
        {
            isDeleteRecord = 1

            updatedTime = Calendar.getInstance().timeInMillis
            save()

            return 1
        }
    }

    override fun getFinishTaskCountByDate(cal: Calendar): Int {
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val firstSecOfThisDay = cal.timeInMillis

        with(cal) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val lastSecOfThisDay = cal.timeInMillis

        return todoDAO.getFinishCountByStartTimeAndEndTime(firstSecOfThisDay, lastSecOfThisDay)
    }

    override fun listFinishTaskCountPastDays(days: Int): ArrayList<Int> {
        val cal = Calendar.getInstance()
        val countList = ArrayList<Int>()

        for (i in 1..days) {
            countList.add(getFinishTaskCountByDate(cal))
            cal.add(Calendar.DATE, -1)
        }
        countList.reverse()
        return countList
    }

}