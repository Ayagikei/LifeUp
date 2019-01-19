package net.sarasarasa.lifeup.utils

import java.util.*

class CalendarUtil {
    companion object {
        fun setToTheFirstSecondOfTheDay(cal: Calendar) {
            with(cal) {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }

        fun setToTheLastSecondOfTheDay(cal: Calendar) {
            with(cal) {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }
        }

        fun getTimeInMillisTheFirstSecondOfTheDay(cal: Calendar): Long {
            setToTheFirstSecondOfTheDay(cal)
            return cal.timeInMillis
        }

        fun getTimeInMillisTheLastSecondOfTheDay(cal: Calendar): Long {
            setToTheLastSecondOfTheDay(cal)
            return cal.timeInMillis
        }

        fun getTimeInMillisNow(): Long {
            return Calendar.getInstance().timeInMillis
        }
    }
}