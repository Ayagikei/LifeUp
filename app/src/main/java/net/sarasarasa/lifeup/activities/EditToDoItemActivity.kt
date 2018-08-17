package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.text.Editable
import kotlinx.android.synthetic.main.content_add_to_do_item.*

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
            checkNotNull(til_toDoText.editText).text = Editable.Factory.getInstance().newEditable(taskModel.content)
            checkNotNull(til_remark.editText).text = Editable.Factory.getInstance().newEditable(taskModel.remark)
            when (taskModel.taskUrgencyLevel) {
                0 -> sb_urgence.left = 0
                1 -> sb_urgence.left = 33
                2 -> sb_urgence.left = 66
                3 -> sb_urgence.left = 100
                else -> sb_urgence.left = 0
            }
            when (taskModel.taskDifficultyLevel) {
                0 -> sb_difficulty.left = 0
                1 -> sb_difficulty.left = 33
                2 -> sb_difficulty.left = 66
                3 -> sb_difficulty.left = 100
                else -> sb_difficulty.left = 0
            }
        }
    }
}