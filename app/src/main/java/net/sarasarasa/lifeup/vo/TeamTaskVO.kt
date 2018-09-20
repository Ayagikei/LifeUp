package net.sarasarasa.lifeup.vo

import java.util.*

class TeamTaskVO {
    var nextEndTime: Date? = null
    var nextStartTime: Date? = null
    var teamId: Long? = null
    var teamRecordId: Long = -1
    var teamTitle: String? = null
    var rewardAttrs: ArrayList<String> = ArrayList()
    var rewardExp: Int? = null
    var teamFreq: Int? = null




    override fun toString(): String {
        return "TeamTaskVO(nextEndTime=$nextEndTime, nextStartTime=$nextStartTime, teamId=$teamId, teamTitle=$teamTitle)"
    }

}