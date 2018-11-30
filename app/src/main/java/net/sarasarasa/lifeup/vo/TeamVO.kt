package net.sarasarasa.lifeup.vo

import java.util.*
import kotlin.collections.ArrayList

class TeamVO {
    var firstEndTime: Date? = null

    var firstStartTime: Date? = null

    var rewardAttrs: ArrayList<String> = ArrayList()

    var rewardExp: Int = 0

    var startDate: Date? = null

    var teamDesc: String? = null

    var teamFreq: Int = 0

    var teamId: Int? = null

    var teamTitle: String? = null

    var teamHead: String? = null

    override fun toString(): String {
        return "TeamVO(firstEndTime=$firstEndTime, firstStartTime=$firstStartTime, rewardAttrs=$rewardAttrs, rewardExp=$rewardExp, startDate=$startDate, teamDesc=$teamDesc, teamFreq=$teamFreq, teamId=$teamId, teamTitle=$teamTitle)"
    }

}