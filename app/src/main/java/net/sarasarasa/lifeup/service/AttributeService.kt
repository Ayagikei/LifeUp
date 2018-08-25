package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.AttributeModel
import net.sarasarasa.lifeup.vo.AttributionVO

interface AttributeService {
    fun initAttribute()

    fun getAttribute(): AttributeModel

    fun getAttributeExpByString(attribute: String): Int

    fun increaseExp(abbr: String, exp: Int): Boolean

    fun decreaseExp(abbr: String, exp: Int): Boolean

    fun getAttributeVO(): AttributionVO
}