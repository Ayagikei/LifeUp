package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.AttributeModel

interface AttributeService {
    fun initAttribute()

    fun getAttribute(): AttributeModel

    fun increaseExp(abbr: String, exp: Int): Boolean
}