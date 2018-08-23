package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport

data class AttributeModel(var gradeAttribute: Int,
                          var strengthAttribute: Int,
                          var knowledgeAttribute: Int,
                          var charmAttribute: Int,
                          var enduranceAttribute: Int,
                          var energyAttribute: Int,
                          var creativity: Int) : LitePalSupport() {
    var id: Long? = null
}