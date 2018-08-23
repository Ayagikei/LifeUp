package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport

class AttributeLevelModel(var levelNum: Int,
                          var startExpValue: Int,
                          var endExpValue: Int) : LitePalSupport() {
    var id: Long? = null
}