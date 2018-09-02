package net.sarasarasa.lifeup.service

import net.sarasarasa.lifeup.models.UserModel
import net.sarasarasa.lifeup.vo.ProfileVO

interface UserService {

    /** 获得包含自己个人信息的[UserModel] **/
    fun getMine(): UserModel

    /** 初始化自己个人信息的[UserModel] **/
    fun initMine()

    /** 在自己的[UserModel]中保存[token] **/
    fun saveToken(token: String)

    /** 获得自己的[token:String] **/
    fun getToken(): String

    /** 将[ProfileVO]里的个人信息保存到本地 **/
    fun saveMine(profileVO: ProfileVO)

    fun clearMine()
}