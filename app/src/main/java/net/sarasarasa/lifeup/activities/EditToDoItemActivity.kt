package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.utils.DensityUtil
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*

class EditToDoItemActivity : AddToDoItemActivity() {

    var id: Long = 0

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
                checkNotNull(til_deadLine.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormat.format(taskModel.taskExpireTime))

                //显示重复
                val set = ConstraintSet()
                set.clone(layout_extra)
                set.connect(switch1.id, ConstraintSet.TOP, til_repeat.id, ConstraintSet.BOTTOM, DensityUtil.dp2px(this, 8f))
                set.applyTo(layout_extra)

                btn_ddl_reset.visibility = View.VISIBLE
                til_repeat.visibility = View.VISIBLE
            }

            if (taskModel.taskRemindTime != null) {
                val simpleDateFormatFirst = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val simpleDateFormatSec = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                checkNotNull(til_remindDate.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormatFirst.format(taskModel.taskRemindTime))
                checkNotNull(til_remindTime.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormatSec.format(taskModel.taskRemindTime))

                btn_remind_reset.visibility = View.VISIBLE
            }

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


            //还原频次的选择
/*            checkNotNull(til_repeat.editText).text = when (taskModel.taskFrequency) {
                0 -> Editable.Factory.getInstance().newEditable("单次")
                -1 -> Editable.Factory.getInstance().newEditable("多次")
                1 -> Editable.Factory.getInstance().newEditable("每日")
                2 -> Editable.Factory.getInstance().newEditable("每两日")
                7 -> Editable.Factory.getInstance().newEditable("每周")
                14 -> Editable.Factory.getInstance().newEditable("每两周")
                30 -> Editable.Factory.getInstance().newEditable("每月")
                else -> Editable.Factory.getInstance().newEditable("不重复")
            }*/

            iFrequency = taskModel.taskFrequency
            til_repeat.editText?.setText(TodoItemConverter.iFrequencyToNormalString(iFrequency))

            //还原3个属性的选择
            restoreAbbrSelection(taskModel.relatedAttribute1)
            restoreAbbrSelection(taskModel.relatedAttribute2)
            restoreAbbrSelection(taskModel.relatedAttribute3)

            switch1.isChecked = taskModel.isShared

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
                    updateItem(getItem())
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
            ToastUtils.showShortToast("提醒重设成功！")
        }
        todoService.updateTodoItem(id, taskModel)
        WidgetUtils.updateWidgets(applicationContext)
        finish()
    }
}