package net.sarasarasa.lifeup.service.impl

import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.dao.UserDAO
import net.sarasarasa.lifeup.models.UserModel
import net.sarasarasa.lifeup.service.UserService
import net.sarasarasa.lifeup.vo.ProfileVO

class UserServiceImpl : UserService {


    private val userDAO = UserDAO()
    private val context by lazy {
        LifeUpApplication.getLifeUpApplication()
    }

    override fun initMine() {
        val mine = UserModel()
        mine.token = ""
        mine.nickName = context.getString(R.string.guest)
        mine.save()
    }

    override fun getMine(): UserModel {
        val res = userDAO.getMine()

        return if (res == null) {
            initMine()
            getMine()
        } else res
    }

    override fun saveToken(token: String) {
        val mine = getMine()
        mine.token = token
        mine.save()
    }

    override fun getToken(): String {
        return getMine().token
    }

    override fun clearMine() {
        val mine = getMine()
        mine.createTime = null
        mine.nickName = ""
        mine.userSex = 2
        mine.userAddress = ""
        mine.phone = ""
        mine.save()
    }

    override fun saveMine(profileVO: ProfileVO, isSaveUserHead: Boolean) {
        clearMine()

        val mine = getMine()
        mine.createTime = profileVO.createTime
        mine.nickName = profileVO.nickname

        if (isSaveUserHead)
            mine.userHead = profileVO.userHead

        mine.userSex = profileVO.userSex ?: 2
        mine.userAddress = profileVO.userAddress
        mine.phone = profileVO.phone
        mine.save()
    }

    override fun saveMine(profileVO: ProfileVO) {
        saveMine(profileVO, false)
    }

}