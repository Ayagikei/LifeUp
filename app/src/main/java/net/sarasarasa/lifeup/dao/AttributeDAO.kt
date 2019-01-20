package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.AttributeModel
import net.sarasarasa.lifeup.models.ExpModel
import org.litepal.LitePal

class AttributeDAO {

    fun saveAttribute(attributeModel: AttributeModel) {
        attributeModel.save()
    }

    fun getFirstAttribute(): AttributeModel? {
        return LitePal.findFirst(AttributeModel::class.java)
    }

    fun sumDailyTotalExpByDate(startTime: Long, endTime: Long): Int {
        return LitePal.where("createTime >= ? and createTime <= ? and isDecrease = 0", startTime.toString(), endTime.toString())
                .sum(ExpModel::class.java, "totalValue", Int::class.java) -
                LitePal.where("createTime >= ? and createTime <= ? and isDecrease = 1", startTime.toString(), endTime.toString())
                        .sum(ExpModel::class.java, "totalValue", Int::class.java)
    }

    fun listExpDetail(limit: Int, offset: Int): List<ExpModel> {
        return LitePal.order("createTime desc")
                .limit(limit)
                .offset(offset)
                .find(ExpModel::class.java)
    }

    fun countExpDetail(): Int {
        return LitePal.count(ExpModel::class.java)
    }
}