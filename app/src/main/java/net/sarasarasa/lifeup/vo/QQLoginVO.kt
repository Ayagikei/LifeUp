package net.sarasarasa.lifeup.vo


class QQLoginVO {
    var ret: Int = 0

    var openid: String? = null

    var access_token: String? = null

    var pay_token: String? = null

    var expires_in: Int = 0

    var pf: String? = null

    var pfkey: String? = null

    var msg: String? = null

    var login_cost: Int = 0

    var query_authority_cost: Int = 0

    var authority_cost: Int = 0

    var expires_time: Long = 0

    override fun toString(): String {
        return "QQLoginVO(ret=$ret, openid=$openid, access_token=$access_token, pay_token=$pay_token, expires_in=$expires_in, pf=$pf, pfkey=$pfkey, msg=$msg, login_cost=$login_cost, query_authority_cost=$query_authority_cost, authority_cost=$authority_cost, expires_time=$expires_time)"
    }


}