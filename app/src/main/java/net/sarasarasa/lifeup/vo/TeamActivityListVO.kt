package net.sarasarasa.lifeup.vo

import java.util.*

class TeamActivityListVO {

    var activityIcon: Int? = null
    var memberRecordId: Long? = null
    var teamId: Long? = null
    var teamRecordId: Long? = null
    var userActivity: String? = null
    var userId: Long? = null
    var nickname: String? = null
    var userHead: String? = null
    var createTime: Date? = null
    var teamTitle: String? = null


    override fun toString(): String {
        return "TeamActivityListVO(activityIcon=$activityIcon, memberRecordId=$memberRecordId, teamId=$teamId, teamRecordId=$teamRecordId, userActivity=$userActivity, userId=$userId, nickname=$nickname, userHead=$userHead, createTime=$createTime, teamTitle=$teamTitle)"
    }


}