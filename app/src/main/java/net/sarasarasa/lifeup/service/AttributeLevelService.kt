package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.AttributeLevelModel

interface AttributeLevelService {
    fun initAttributeLevel()

    fun getAttributeLevel(exp: Int): AttributeLevelModel

}