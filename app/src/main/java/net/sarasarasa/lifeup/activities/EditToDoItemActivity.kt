package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.text.Editable
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import java.text.SimpleDateFormat
import java.util.*

class EditToDoItemActivity : AddToDoItemActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val id = intent.getLongExtra("id", -1)

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
            }

            if (taskModel.taskRemindTime != null) {
                val simpleDateFormatFirst = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val simpleDateFormatSec = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                checkNotNull(til_remindDate.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormatFirst.format(taskModel.taskRemindTime))
                checkNotNull(til_remindTime.editText).text = Editable.Factory.getInstance().newEditable(simpleDateFormatSec.format(taskModel.taskRemindTime))
            }

            //还原奖励设置
            when (taskModel.taskUrgencyDegree) {
                0 -> sb_urgence.setValue(0f)
                1 -> sb_urgence.setValue(33f)
                2 -> sb_urgence.setValue(66f)
                3 -> sb_urgence.setValue(99f)
                else -> sb_urgence.setValue(0.0f)
            }

            when (taskModel.taskDifficultyDegree) {
                0 -> sb_difficulty.setValue(0.0f)
                1 -> sb_difficulty.setValue(33f)
                2 -> sb_difficulty.setValue(66f)
                3 -> sb_difficulty.setValue(99f)
                else -> sb_difficulty.setValue(0.0f)
            }

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
}