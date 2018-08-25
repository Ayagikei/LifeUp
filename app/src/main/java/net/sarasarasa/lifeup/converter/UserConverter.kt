package net.sarasarasa.lifeup.converter

class UserConverter() {
    companion object {
        fun iSexTostrSex(sex: Int): String? {
            return when (sex) {
                0 -> "女"
                1 -> "男"
                2 -> "保密"
                else -> null
            }
        }
    }
}