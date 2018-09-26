package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.constants.AttributeConstants.Companion.MAX_LEVEL
import net.sarasarasa.lifeup.models.AttributeLevelModel
import org.litepal.LitePal

class AttributeLevelDAO {

    fun saveAttribute(attributeLevelModel: AttributeLevelModel) {
        attributeLevelModel.save()
    }

    fun getOneByExp(exp: Int): AttributeLevelModel {
        if (exp < 0) {

        }
        return LitePal.where("startExpValue <= ? and endExpValue > ?", exp.toString(), exp.toString())
                .findFirst(AttributeLevelModel::class.java)
    }

    fun getOneByLevel(level: Int): AttributeLevelModel {
        return LitePal.where("levelNum = ?", level.toString())
                .findFirst(AttributeLevelModel::class.java) ?: getOneByLevel(MAX_LEVEL)
    }
}