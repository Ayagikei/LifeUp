package net.sarasarasa.lifeup.vo

import java.util.*

class UserDetailVO {
    var createTime: Date? = null
    var followingAmount: Int? = null
    var followerAmount: Int? = null
    var nickname: String? = null
    var phone: String? = null
    var teamAmount: Int? = null
    var userAddress: String? = null
    var userHead: String? = null
    var userId: Long? = null
    var userSex: Int? = null
    var userStatus: Int? = null
    var isFollow: Int? = null


    override fun toString(): String {
        return "UserDetailVO(createTime=$createTime, followingAmount=$followingAmount, followerAmount=$followerAmount, nickname=$nickname, phone=$phone, teamAmount=$teamAmount, userAddress=$userAddress, userHead=$userHead, userId=$userId, userSex=$userSex, userStatus=$userStatus, isFollow=$isFollow)"
    }

}