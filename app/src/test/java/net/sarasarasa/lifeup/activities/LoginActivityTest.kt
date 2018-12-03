package net.sarasarasa.lifeup.activities

import net.sarasarasa.lifeup.utils.MD5Util
import org.junit.Test
import java.util.*

class LoginActivityTest {
    @Test
    fun getQQDefaultNicknameTest() {
        System.out.print(MD5Util.encryption(Calendar.getInstance().toString()).substring(0, 8))
    }
}