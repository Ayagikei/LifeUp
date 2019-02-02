package net.sarasarasa.lifeup.utils

import java.util.*

class CalendarUtil {
    companion object {
        fun setToTheFirstSecondOfTheDay(cal: Calendar) {
            with(cal) {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        fun setToTheLastSecondOfTheDay(cal: Calendar) {
            with(cal) {
                set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE))
                set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND))
                set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND))
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

        fun getTimeAfterSeveralMinutesTime(date: Date, minutes: Int): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.MINUTE, minutes)
            return cal.time
        }
    }
}