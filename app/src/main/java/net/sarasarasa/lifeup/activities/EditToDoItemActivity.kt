package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.dao.TaskTargetDAO
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*

class EditToDoItemActivity : AddToDoItemActivity() {

    var id: Long = 0
    private val taskTargetDAO = TaskTargetDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        id = intent.getLongExtra("id", -1)

        if (id != -1L) {
            initStatus(id)
        }

    }

    private fun initStatus(id: Long) {

        val taskModel = todoService.getATodoItem(id)
        if (taskModel != null) {
            //还原基础信息
            checkNotNull(til_toDoText.editText).text = Editable.Factory.getInstance().newEditable(taskModel.content)
            checkNotNull(til_remark.editText).text = Editable.Factory.getInstance().newEditable(taskModel.remark)

            if (taskModel.taskExpireTime != null) {
                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val simpleDateTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

                if (!taskModel.isUseSpecificExpireTime)
                    checkNotNull(til_deadLine.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormat.format(taskModel.taskExpireTime))
                else checkNotNull(til_deadLine.editText).text = Editable.Factory.getInstance().newEditable(simpleDateTimeFormat.format(taskModel.taskExpireTime))
/*                //显示重复
                val set = ConstraintSet()
                set.clone(layout_extra)
                set.connect(switch1.id, ConstraintSet.TOP, til_repeat.id, ConstraintSet.BOTTOM, DensityUtil.dp2px(this, 8f))
                set.applyTo(layout_extra)*/

                btn_ddl_reset.visibility = View.VISIBLE
                btn_ddl_set_spec_time.visibility = View.VISIBLE
            }

            if (taskModel.taskRemindTime != null) {
                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                checkNotNull(til_remindDate.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormat.format(taskModel.taskRemindTime))

                btn_remind_reset.visibility = View.VISIBLE
            }

            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            checkNotNull(til_startTime.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormat.format(taskModel.startTime))
            btn_start_time_reset.visibility = View.VISIBLE

            //还原奖励设置
            when (taskModel.taskUrgencyDegree) {
                1 -> sb_urgence.setValue(0f)
                2 -> sb_urgence.setValue(33f)
                3 -> sb_urgence.setValue(66f)
                4 -> sb_urgence.setValue(99f)
                else -> sb_urgence.setValue(0.0f)
            }

            when (taskModel.taskDifficultyDegree) {
                1 -> sb_difficulty.setValue(0.0f)
                2 -> sb_difficulty.setValue(33f)
                3 -> sb_difficulty.setValue(66f)
                4 -> sb_difficulty.setValue(99f)
                else -> sb_difficulty.setValue(0.0f)
            }

            iFrequency = taskModel.taskFrequency
            if (taskModel.isIgnoreDayOfWeek.isNotEmpty()) {
                arrIgnoreDayOfWeek = taskModel.isIgnoreDayOfWeek.toIntArray()
            }

            if (iFrequency == 1 && taskModel.isIgnoreDayOfWeek.contains(1)) {
                et_repeat.setText(TodoItemConverter.iFrequencyWithIgnoreToNormalString(arrIgnoreDayOfWeek))
            } else et_repeat.setText(TodoItemConverter.iFrequencyToNormalString(iFrequency))

            if (iFrequency == 1)
                btn_repeat_set_ignore_day_of_week.visibility = View.VISIBLE

            //还原3个属性的选择
            restoreAbbrSelection(taskModel.relatedAttribute1)
            restoreAbbrSelection(taskModel.relatedAttribute2)
            restoreAbbrSelection(taskModel.relatedAttribute3)

            if (taskModel.taskTargetId != null) {
                val taskTarget = taskTargetDAO.getTaskTargetById(taskModel.taskTargetId!!)
                if (taskTarget != null) {
                    til_target.editText?.setText(taskTarget.targetTimes.toString())
                } else {
                    til_target.editText?.setText("0")
                }
            }
            til_target.isEnabled = false

            // 还原奖励
            til_complete_reward.editText?.setText(taskModel.completeReward)
        }
    }

    private fun restoreAbbrSelection(abbr: String?) {

        val imageView: ImageView = when (abbr) {
            "strength" -> findViewById(R.id.iv_strength)
            "learning" -> findViewById(R.id.iv_learning)
            "charm" -> findViewById(R.id.iv_charm)
            "endurance" -> findViewById(R.id.iv_endurance)
            "vitality" -> findViewById(R.id.iv_vitality)
            "creative" -> findViewById(R.id.iv_creative)
            else -> return
        }

        //选中的[imageView]恢复彩色
        imageView.colorFilter = null

        arrAbbrBtn[ToDoItemConstants.SELECTED_CNT]++
        arrAbbrBtn[TodoItemConverter.viewToIndex(imageView)] = ToDoItemConstants.SELECTED
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                if (check()) {
                    updateItem(getItem(newItem = false))
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateItem(taskModel: TaskModel) {
        //设置提醒
        if (taskModel.taskRemindTime != null && id != null) {
            todoService.setOrUpdateAlarm(taskModel.taskRemindTime!!.time, id, applicationContext)
            ToastUtils.showShortToast(getString(R.string.edit_to_do_remind_reset_success))
        }
        todoService.updateTodoItem(id, taskModel)
        WidgetUtils.updateWidgets(applicationContext)
        finish()
    }
}