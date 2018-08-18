package net.sarasarasa.lifeup.converter

import android.view.View
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AddToDoItemConstants

class TodoItemConverter {
    companion object {
        fun stringToDrawableId(string: String?): Int {
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

        fun indexToString(index: Int): String {
            return when (index) {
                AddToDoItemConstants.STRENGTH_INDEX -> "strength"
                AddToDoItemConstants.LEARNING_INDEX -> "learning"
                AddToDoItemConstants.CHARM_INDEX -> "charm"
                AddToDoItemConstants.ENDURANCE_INDEX -> "endurance"
                AddToDoItemConstants.VITALITY_INDEX -> "vitality"
                AddToDoItemConstants.CREATIVE_INDEX -> "creative"
                else -> ""
            }
        }

        fun viewToIndex(view: View): Int {
            return when (view.id) {
                R.id.iv_strength -> AddToDoItemConstants.STRENGTH_INDEX
                R.id.iv_learning -> AddToDoItemConstants.LEARNING_INDEX
                R.id.iv_charm -> AddToDoItemConstants.CHARM_INDEX
                R.id.iv_endurance -> AddToDoItemConstants.ENDURANCE_INDEX
                R.id.iv_vitality -> AddToDoItemConstants.VITALITY_INDEX
                R.id.iv_creative -> AddToDoItemConstants.CREATIVE_INDEX
                else -> -1
            }
        }


    }
}