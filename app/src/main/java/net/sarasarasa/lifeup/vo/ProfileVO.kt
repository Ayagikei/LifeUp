package net.sarasarasa.lifeup.vo


class ProfileVO {
    var authTypes: List<String>? = null

    var createTime: String? = null

    var nickName: String? = null

    var phone: String? = null

    var userAddress: String? = null

    var userHead: String? = null

    var userId: Long? = null

    var userSex: Int? = null

    var userStatus: Int? = null


    override fun toString(): String {
        return "ProfileVO(authTypes=$authTypes, createTime=$createTime, nickName=$nickName, phone=$phone, userAddress=$userAddress, userHead=$userHead, userId=$userId, userSex=$userSex, userStatus=$userStatus)"
    }


}