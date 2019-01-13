package net.sarasarasa.lifeup.service.impl

import net.sarasarasa.lifeup.dao.AttributeDAO
import net.sarasarasa.lifeup.models.AttributeModel
import net.sarasarasa.lifeup.models.ExpModel
import net.sarasarasa.lifeup.service.AttributeService
import net.sarasarasa.lifeup.vo.AttributionVO
import java.util.*

class AttributeServiceImpl : AttributeService {

    private val attributeDAO = AttributeDAO()


    override fun initAttribute() {
        if (attributeDAO.getFirstAttribute() == null) {
            val attributeModel = AttributeModel(0, 0, 0, 0, 0, 0, 0)
            attributeDAO.saveAttribute(attributeModel)
        }
    }

    override fun getAttribute(): AttributeModel {
        val ret = attributeDAO.getFirstAttribute()
        if (ret == null) {
            initAttribute()
            return getAttribute()
        }
        return ret
    }

    override fun getAttributeExpByString(attribute: String): Int {
        return when (attribute) {
            "strength" -> getAttribute().strengthAttribute
            "learning" -> getAttribute().knowledgeAttribute
            "charm" -> getAttribute().charmAttribute
            "endurance" -> getAttribute().enduranceAttribute
            "vitality" -> getAttribute().energyAttribute
            "creative" -> getAttribute().creativity
            else -> return -1
        }
    }

    override fun increaseExp(attr: String?, exp: Int): Boolean {
        if (attr.isNullOrEmpty()) return false
        val attributeModel = getAttribute()

        when (attr) {
            "strength" -> attributeModel.strengthAttribute += exp
            "learning" -> attributeModel.knowledgeAttribute += exp
            "charm" -> attributeModel.charmAttribute += exp
            "endurance" -> attributeModel.enduranceAttribute += exp
            "vitality" -> attributeModel.energyAttribute += exp
            "creative" -> attributeModel.creativity += exp
            else -> return false
        }

        attributeModel.gradeAttribute += exp / 5
        return attributeModel.save()
    }

    override fun increaseMultiExp(attrs: ArrayList<String>, exp: Int, content: String): Boolean {
        if (attrs.isEmpty()) return false

        val arrayListTrueDataSize = when {
            attrs.getOrNull(1).isNullOrBlank() -> 1
            attrs.getOrNull(2).isNullOrBlank() -> 2
            else -> 3
        }
        val expModel = ExpModel(exp, content, Date(), false, exp * arrayListTrueDataSize, arrayListTrueDataSize)
        expModel.relatedAttribute = attrs
        increaseExp(attrs.getOrNull(0), exp)
        increaseExp(attrs.getOrNull(1), exp)
        increaseExp(attrs.getOrNull(2), exp)
        return expModel.save()
    }

    override fun decreaseExp(attr: String?, exp: Int): Boolean {
        if (attr.isNullOrEmpty()) return false

        val attributeModel = getAttribute()
        when (attr) {
            "strength" -> {
                attributeModel.strengthAttribute -= exp
                if (attributeModel.strengthAttribute < 0)
                    attributeModel.strengthAttribute = 0
            }
            "learning" -> {
                attributeModel.knowledgeAttribute -= exp
                if (attributeModel.knowledgeAttribute < 0)
                    attributeModel.knowledgeAttribute = 0
            }
            "charm" -> {
                attributeModel.charmAttribute -= exp
                if (attributeModel.charmAttribute < 0)
                    attributeModel.charmAttribute = 0
            }
            "endurance" -> {
                attributeModel.enduranceAttribute -= exp
                if (attributeModel.enduranceAttribute < 0)
                    attributeModel.enduranceAttribute = 0
            }
            "vitality" -> {
                attributeModel.energyAttribute -= exp
                if (attributeModel.energyAttribute < 0)
                    attributeModel.energyAttribute = 0
            }
            "creative" -> {
                attributeModel.creativity -= exp
                if (attributeModel.creativity < 0)
                    attributeModel.creativity = 0
            }
            else -> return false
        }

        attributeModel.gradeAttribute -= exp / 5
        if (attributeModel.gradeAttribute < 0)
            attributeModel.gradeAttribute = 0

        return attributeModel.save()
    }

    override fun decreaseMultiExp(attrs: ArrayList<String>, exp: Int, content: String): Boolean {
        if (attrs.isEmpty()) return false

        val arrayListTrueDataSize = when {
            attrs.getOrNull(1).isNullOrBlank() -> 1
            attrs.getOrNull(2).isNullOrBlank() -> 2
            else -> 3
        }

        val expModel = ExpModel(exp, content, Date(), true, exp * arrayListTrueDataSize, arrayListTrueDataSize)
        expModel.relatedAttribute = attrs
        decreaseExp(attrs.getOrNull(0), exp)
        decreaseExp(attrs.getOrNull(1), exp)
        decreaseExp(attrs.getOrNull(2), exp)
        return expModel.save()
    }

    override fun getAttributeVO(): AttributionVO {
        val attributeModel = getAttribute()
        val attributionVO = AttributionVO()
        with(attributionVO) {
            attributionVO.userExp = attributeModel.gradeAttribute
            attributionVO.attributeStrength = attributeModel.strengthAttribute
            attributionVO.attributeKnowledge = attributeModel.knowledgeAttribute
            attributionVO.attributeCharm = attributeModel.charmAttribute
            attributionVO.attributeEndurance = attributeModel.enduranceAttribute
            attributionVO.attributeEnergy = attributeModel.energyAttribute
            attributionVO.attributeCreativity = attributeModel.creativity
        }

        return attributionVO
    }

    override fun getTotalAttrExp(): Int {
        val attributeModel = getAttribute()
        return attributeModel.strengthAttribute +
                attributeModel.charmAttribute +
                attributeModel.knowledgeAttribute +
                attributeModel.energyAttribute +
                attributeModel.enduranceAttribute +
                attributeModel.creativity
    }

    override fun getDailyTotalExpByDate(cal: Calendar): Int {
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val firstSecOfThisDay = cal.timeInMillis

        with(cal) {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        val lastSecOfThisDay = cal.timeInMillis

        return attributeDAO.sumDailyTotalExpByDate(firstSecOfThisDay, lastSecOfThisDay)
    }

    override fun listDailyTotalExpPastDays(days: Int): ArrayList<Int> {
        val cal = Calendar.getInstance()
        val countList = ArrayList<Int>()

        for (i in 1..days) {
            countList.add(getDailyTotalExpByDate(cal))
            cal.add(Calendar.DATE, -1)
        }
        countList.reverse()
        return countList
    }

    override fun listExpDetail(limit: Int, offset: Int): List<ExpModel> {
        return attributeDAO.listExpDetail(limit, offset)
    }

    override fun countExpDetail(): Int {
        return attributeDAO.countExpDetail()
    }
}