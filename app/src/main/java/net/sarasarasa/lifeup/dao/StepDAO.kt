package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.StepModel
import org.litepal.LitePal
import java.util.*

class StepDAO {

    fun getTheLastStepRecord(): StepModel? {
        return LitePal.findLast(StepModel::class.java)
    }

    fun getTheLastStepRecordBeforeDate(date: Date): StepModel? {
        return LitePal.where("date < ?", date.time.toString()).findLast(StepModel::class.java)
    }

    fun getStepByStartTimeAndEndTime(startTime: Long, endTime: Long): StepModel? {
        return LitePal.where("date < ? and date > ?", endTime.toString(), startTime.toString()).find(StepModel::class.java).getOrNull(0)
    }
}