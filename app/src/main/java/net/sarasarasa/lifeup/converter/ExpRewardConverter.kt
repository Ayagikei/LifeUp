package net.sarasarasa.lifeup.converter

class ExpRewardConverter {
    companion object {
        fun getExpReward(num: Int, taskUrgencyLevel: Int, taskDifficultyLevel: Int): Int {
            if (num == 0) return 0
            return taskUrgencyLevel * 60 + taskDifficultyLevel * 60 / num + (taskUrgencyLevel + taskDifficultyLevel) * 10 / 2
        }
    }
}