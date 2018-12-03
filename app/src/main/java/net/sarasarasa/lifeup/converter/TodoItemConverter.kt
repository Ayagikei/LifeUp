package net.sarasarasa.lifeup.converter

import android.view.View
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants

class TodoItemConverter {
    companion object {
        fun strAbbrToDrawableId(string: String?): Int {
            return when (string) {
                "strength" -> R.drawable.ic_abbr_strength
                "learning" -> R.drawable.ic_abbr_learning
                "charm" -> R.drawable.ic_abbr_charm
                "endurance" -> R.drawable.ic_abbr_endurance
                "vitality" -> R.drawable.ic_abbr_vitality
                "creative" -> R.drawable.ic_abbr_creative
                else -> R.drawable.ic_empty
            }
        }

        fun strAbbrToStrTitle(string: String?): String {
            return when (string) {
                "strength" -> "力量"
                "learning" -> "学识"
                "charm" -> "魅力"
                "endurance" -> "耐力"
                "vitality" -> "活力"
                "creative" -> "创造"
                else -> ""
            }
        }

        fun indexToString(index: Int): String {
            return when (index) {
                ToDoItemConstants.STRENGTH_INDEX -> "strength"
                ToDoItemConstants.LEARNING_INDEX -> "learning"
                ToDoItemConstants.CHARM_INDEX -> "charm"
                ToDoItemConstants.ENDURANCE_INDEX -> "endurance"
                ToDoItemConstants.VITALITY_INDEX -> "vitality"
                ToDoItemConstants.CREATIVE_INDEX -> "creative"
                else -> ""
            }
        }

        fun viewToIndex(view: View): Int {
            return when (view.id) {
                R.id.iv_strength -> ToDoItemConstants.STRENGTH_INDEX
                R.id.iv_learning -> ToDoItemConstants.LEARNING_INDEX
                R.id.iv_charm -> ToDoItemConstants.CHARM_INDEX
                R.id.iv_endurance -> ToDoItemConstants.ENDURANCE_INDEX
                R.id.iv_vitality -> ToDoItemConstants.VITALITY_INDEX
                R.id.iv_creative -> ToDoItemConstants.CREATIVE_INDEX
                else -> -1
            }
        }


        fun iFrequencyToTitleString(taskFrequency: Int): String {
            return iFrequencyToTitleString(false, taskFrequency)
        }

        fun iFrequencyToTitleString(isTeamTask: Boolean, taskFrequency: Int): String {
            if (isTeamTask) {
                return when (taskFrequency) {
                    0 -> "团队任务-单次"
                    1 -> "团队任务-每日"
                    2 -> "团队任务-每两日"
                    7 -> "团队任务-每周"
                    14 -> "团队任务-每两周"
                    30 -> "团队任务-每月"
                    else -> "团队任务-每${taskFrequency}天"
                }
            } else {
                return when (taskFrequency) {
                    0 -> "单次任务"
                    -1 -> "多次任务"
                    1 -> "周期任务-每日"
                    2 -> "周期任务-每两日"
                    7 -> "周期任务-每周"
                    14 -> "周期任务-每两周"
                    30 -> "周期任务-每月"
                    else -> "周期任务-每${taskFrequency}天"
                }
            }
        }

        fun iFrequencyToNormalString(taskFrequency: Int?): String {
            return when (taskFrequency) {
                0 -> "单次"
                -1 -> "多次"
                1 -> "每日"
                2 -> "每两日"
                7 -> "每周"
                14 -> "每两周"
                30 -> "每月"
                else -> "自定义-${taskFrequency}天"
            }
        }


        fun strStatusToDrawableId(status: Int): Int {
            return when (status) {
                ToDoItemConstants.COMPLETED -> R.drawable.ic_finish
                ToDoItemConstants.OUT_OF_DATE -> R.drawable.ic_out_of_date
                ToDoItemConstants.GIVE_UP -> R.drawable.ic_give_up
                else -> R.drawable.ic_finish
            }
        }

        fun strFrequencyToColorId(taskFrequency: Int): Int {
            return when (taskFrequency) {
                -1 -> R.color.color_to_do_item_fre0
                0 -> R.color.color_to_do_item_fre0
                1 -> R.color.color_to_do_item_fre1
                2 -> R.color.color_to_do_item_fre1
                7 -> R.color.color_to_do_item_fre7
                14 -> R.color.color_to_do_item_fre7
                30 -> R.color.color_to_do_item_fre30
                else -> R.color.color_to_do_item_custom
            }
        }


    }
}