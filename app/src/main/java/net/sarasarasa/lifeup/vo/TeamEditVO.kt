package net.sarasarasa.lifeup.vo

class TeamEditVO {

    var teamDesc: String? = null

    var teamId: Long? = null

    var teamTitle: String? = null

    var teamHead: String? = null

    override fun toString(): String {
        return "TeamEditVO(teamDesc=$teamDesc, teamId=$teamId, teamTitle=$teamTitle, teamHead=$teamHead)"
    }

}