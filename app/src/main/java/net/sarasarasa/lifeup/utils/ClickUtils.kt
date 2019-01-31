package net.sarasarasa.lifeup.utils

class ClickUtils {
    companion object {
        private val MIN_CLICK_DELAY_TIME = 1000
        private var lastClickTime: Long = 0

        fun isNotFastClick(): Boolean {
            var flag = false
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
                flag = true
            }
            lastClickTime = curClickTime
            return flag
        }

        fun isNotFastClick(ms: Int): Boolean {
            var flag = false
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime >= ms) {
                flag = true
            }
            lastClickTime = curClickTime
            return flag
        }
    }
}