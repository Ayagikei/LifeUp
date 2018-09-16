package net.sarasarasa.lifeup.vo

import java.util.*

class TeamDetailVO {
    var completeTime: Date? = null
    var createTime: Date? = null
    var rewardAttrs: ArrayList<String> = ArrayList()
    var rewardExp: Int? = null
    var startDate: Date? = null
    var nextStartTime: Date? = null
    var nextEndTime: Date? = null
    var teamDesc: String? = null
    var teamFreq: Int? = null
    var teamId: Long? = null
    var teamStatus: Int? = null
    var teamTitle: String? = null
    var userId: Int? = null
    var owner: ProfileVO? = null
    var teamHead: String? = null
    var memberAmount: Int? = null


    override fun toString(): String {
        return "TeamDetailVO(completeTime=$completeTime, createTime=$createTime, rewardAttrs=$rewardAttrs, rewardExp=$rewardExp, startDate=$startDate, nextStartTime=$nextStartTime, nextEndTime=$nextEndTime, teamDesc=$teamDesc, teamFreq=$teamFreq, teamId=$teamId, teamStatus=$teamStatus, teamTitle=$teamTitle, userId=$userId, owner=$owner, teamHead=$teamHead, memberAmount=$memberAmount)"
    }


/*    inner class Time{
        var hour: Int? = null
        var minute: Int? = null
        var nano: Int? = null
        var second: Int? = null
    }*/

}