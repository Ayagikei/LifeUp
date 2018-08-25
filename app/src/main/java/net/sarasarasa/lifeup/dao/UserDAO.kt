package net.sarasarasa.lifeup.dao

import net.sarasarasa.lifeup.models.UserModel
import org.litepal.LitePal

class UserDAO {

    fun getMine(): UserModel? {
        return LitePal.findFirst(UserModel::class.java)
    }
}