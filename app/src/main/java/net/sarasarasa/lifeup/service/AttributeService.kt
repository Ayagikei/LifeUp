package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.AttributeModel
import net.sarasarasa.lifeup.models.ExpModel
import net.sarasarasa.lifeup.vo.AttributionVO
import java.util.*

interface AttributeService {
    fun initAttribute()

    fun getAttribute(): AttributeModel

    fun getAttributeExpByString(attribute: String): Int

    fun increaseExp(attr: String?, exp: Int): Boolean

    fun decreaseExp(attr: String?, exp: Int): Boolean

    fun getAttributeVO(): AttributionVO

    fun getTotalAttrExp(): Int

    fun decreaseMultiExp(attrs: ArrayList<String>, exp: Int, content: String): Boolean

    fun increaseMultiExp(attrs: ArrayList<String>, exp: Int, content: String): Boolean

    fun getDailyTotalExpByDate(cal: Calendar): Int

    fun listDailyTotalExpPastDays(days: Int): ArrayList<Int>

    /** 获取所有经验值收支情况[List<TaskModel>] **/
    fun listExpDetail(limit: Int, offset: Int): List<ExpModel>

    /** 获取所有经验值收支情况的计数 **/
    fun countExpDetail(): Int
}