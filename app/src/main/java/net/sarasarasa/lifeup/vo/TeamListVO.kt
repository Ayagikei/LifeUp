package net.sarasarasa.lifeup.vo

import java.util.*

class TeamListVO {

    var teamFreq: Int? = null
    var teamId: Long? = null
    var teamTitle: String? = null
    var owner: ProfileVO? = null
    var teamHead: String? = null
    var teamDesc: String? = null
    var startDate: Date? = null


    override fun toString(): String {
        return "TeamListVO(teamFreq=$teamFreq, teamId=$teamId, teamTitle=$teamTitle, owner=$owner, teamHead=$teamHead)"
    }


}