package net.sarasarasa.lifeup.vo

import java.util.*

class TeamMembaerListVO {

    var isFollow: Int? = null
    var userId: Long? = null
    var nickname: String? = null
    var userAddress: String? = null
    var userHead: String? = null
    var createTime: Date? = null
    var point: Long? = null
    var rank: Int? = null

    override fun toString(): String {
        return "TeamMembaerListVO(isFollow=$isFollow, userId=$userId, nickname=$nickname, userAddress=$userAddress, userHead=$userHead, createTime=$createTime, point=$point)"
    }

}