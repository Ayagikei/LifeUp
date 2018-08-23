package net.sarasarasa.lifeup.service.impl

import android.util.Log
import net.sarasarasa.lifeup.DAO.AttributeLevelDAO
import net.sarasarasa.lifeup.models.AttributeLevelModel
import net.sarasarasa.lifeup.service.AttributeLevelService

class AttributeLevelServiceImpl : AttributeLevelService {

    private val attributeLevelDAO = AttributeLevelDAO()

    override fun initAttributeLevel() {
        AttributeLevelModel(1, 0, 300).save()
        AttributeLevelModel(2, 300, 1000).save()
        AttributeLevelModel(3, 1000, 2500).save()
        AttributeLevelModel(4, 2500, 5000).save()
        AttributeLevelModel(5, 5000, 7500).save()
        AttributeLevelModel(6, 7500, 10000).save()
        AttributeLevelModel(7, 10000, 15000).save()
        AttributeLevelModel(8, 15000, 20000).save()

        var levelNum = 9
        while (levelNum <= 30) {
            AttributeLevelModel(levelNum, 20000 + 15000 * levelNum - 9, 20000 + 15000 * levelNum - 8).save()
            levelNum++
        }

        Log.i("initLevel", "initLevel")
    }

    override fun getAttributeLevel(exp: Int): AttributeLevelModel {
        return attributeLevelDAO.getOneByExp(exp)
    }


}