package com.cgcel.gbiacarddoge.screen

class Util {
    /*
    * 姓名打码
    * */
    fun formatUserName(userName: String): String {
        val formattedName = when {
            userName.length == 2 -> userName.replaceRange(1, 2, "*")
            userName.length > 2 -> userName.first() + "*".repeat(userName.length - 2) + userName.last()
            else -> userName // 不到两个字不做处理
        }
        return formattedName
    }
}