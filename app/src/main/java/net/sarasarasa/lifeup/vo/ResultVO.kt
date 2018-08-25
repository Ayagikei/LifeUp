package net.sarasarasa.lifeup.vo

class ResultVO<T>(var code: Int, var data: T, var msg: String) {
    override fun toString(): String {
        return "ResultVO(code=$code, data=$data, msg='$msg')"
    }
}