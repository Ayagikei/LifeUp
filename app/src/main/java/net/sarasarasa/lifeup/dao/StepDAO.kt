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
}