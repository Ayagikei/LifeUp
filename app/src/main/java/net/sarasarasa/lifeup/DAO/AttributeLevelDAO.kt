package net.sarasarasa.lifeup.DAO

import net.sarasarasa.lifeup.models.AttributeLevelModel
import org.litepal.LitePal

class AttributeLevelDAO {

    fun saveAttribute(attributeLevelModel: AttributeLevelModel) {
        attributeLevelModel.save()
    }

    fun getOneByExp(exp: Int): AttributeLevelModel {
        return LitePal.where("startExpValue <= ? and endExpValue > ?", exp.toString(), exp.toString())
                .findFirst(AttributeLevelModel::class.java)
    }
}