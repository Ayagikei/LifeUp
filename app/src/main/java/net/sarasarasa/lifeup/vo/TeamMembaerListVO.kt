package net.sarasarasa.lifeup.vo

import java.util.*

class TeamMembaerListVO {

    var isFollow: Int? = null
    var userId: Long? = null
    var nickname: String? = null
    var userAddress: String? = null
    var userHead: String? = null
    var createTime: Date? = null

    override fun toString(): String {
        return "TeamMembaerListVO(isFollow=$isFollow, userId=$userId, nickName=$nickname, userAddress=$userAddress, userHead=$userHead, createTime=$createTime)"
    }

}