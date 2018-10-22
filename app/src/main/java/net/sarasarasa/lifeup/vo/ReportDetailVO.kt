package net.sarasarasa.lifeup.vo


class ReportDetailVO {

    var criminalUserId: Long? = null

    var itemId : Long? = null

    var reportId: Long? = null

    var reportItem: String? = null

    var reportTypeId: Long? = null

    var reportUserId: Long? = null


    override fun toString(): String {
        return "ReportDetailVO(criminalUserId=$criminalUserId, itemId=$itemId, reportId=$reportId, reportItem=$reportItem, reportTypeId=$reportTypeId, reportUserId=$reportUserId)"
    }

}