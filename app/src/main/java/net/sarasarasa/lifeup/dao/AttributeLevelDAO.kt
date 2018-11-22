package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.AttributeLevelModel
import org.litepal.LitePal

class AttributeLevelDAO {

    fun saveAttribute(attributeLevelModel: AttributeLevelModel) {
        attributeLevelModel.save()
    }

    fun getOneByExp(exp: Int): AttributeLevelModel {
        if (exp < 0) {
            return AttributeLevelModel(0, 0, 0)
        }

        val lastLevel = LitePal.findLast(AttributeLevelModel::class.java)

        return LitePal.where("startExpValue <= ? and endExpValue > ?", exp.toString(), exp.toString())
                .findFirst(AttributeLevelModel::class.java)
                ?: AttributeLevelModel(100, lastLevel.endExpValue, lastLevel.endExpValue + 9999999)


    }

    fun getOneByLevel(level: Int): AttributeLevelModel {
        return LitePal.where("levelNum = ?", level.toString())
                .findFirst(AttributeLevelModel::class.java)
                ?: LitePal.findLast(AttributeLevelModel::class.java)
    }
}