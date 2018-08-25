package net.sarasarasa.lifeup.vo

class AttributionVO {
    var attributeCharm: Int? = null

    var attributeCreativity: Int? = null

    var attributeEndurance: Int? = null

    var attributeEnergy: Int? = null

    var attributeId: Long? = null

    var attributeKnowledge: Int? = null

    var attributeStrength: Int? = null

    var userExp: Int? = null

    var userGrade: Int? = null

    var userId: Long? = null

    override fun toString(): String {
        return "AttributionVO(attributeCharm=$attributeCharm, attributeCreativity=$attributeCreativity, attributeEndurance=$attributeEndurance, attributeEnergy=$attributeEnergy, attributeId=$attributeId, attributeKnowledge=$attributeKnowledge, attributeStrength=$attributeStrength, userExp=$userExp, userGrade=$userGrade, userId=$userId)"
    }
}