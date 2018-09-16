package net.sarasarasa.lifeup.vo


class PageVO<T> {
    //当前页码，从1开始，查询必传
    var currentPage: Long? = null

    // 当前页展示的数据量，大于0，查询必传
    var size: Long? = null

    //总页码，由后端返回
    var totalPage: Long? = null

    //查询结果，有后端返回
    var list: List<T>? = null

    override fun toString(): String {
        return "PageVO(currentPage=$currentPage, size=$size, totalPage=$totalPage, list=$list)"
    }


}