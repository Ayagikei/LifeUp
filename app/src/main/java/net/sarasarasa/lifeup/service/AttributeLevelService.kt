package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.AttributeLevelModel

interface AttributeLevelService {
    fun initAttributeLevel()

    fun getAttributeLevelByExp(exp: Int): AttributeLevelModel

    fun getAttributeLevelByLevel(level: Int): AttributeLevelModel

}