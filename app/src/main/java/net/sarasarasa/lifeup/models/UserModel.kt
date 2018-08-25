package net.sarasarasa.lifeup.models

import org.litepal.crud.LitePalSupport


class UserModel : LitePalSupport() {
    var token: String = ""

    var createTime: String? = null

    var nickName: String? = null

    var phone: String? = null

    var userAddress: String? = null

    var userHead: String? = null

    var userId: Int = 0

    var userSex: Int = 0

    var userStatus: Int = 0

}