package net.sarasarasa.lifeup.vo

import java.util.*

class TeamTaskVO {
    var nextEndTime: Date? = null
    var nextStartTime: Date? = null
    var teamId: Long? = null
    var teamTitle: String? = null


    override fun toString(): String {
        return "TeamTaskVO(nextEndTime=$nextEndTime, nextStartTime=$nextStartTime, teamId=$teamId, teamTitle=$teamTitle)"
    }

}