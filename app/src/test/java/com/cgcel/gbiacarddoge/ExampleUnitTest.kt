package com.cgcel.gbiacarddoge

import com.alibaba.fastjson2.JSON
import org.junit.Test

import org.junit.Assert.*
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_jwtDecode(){
        val completeToken = "xxx"
        val payloadData = completeToken.split(".")[1]
        val decodedBytes = Base64.getUrlDecoder().decode(payloadData)
        val decodedString = String(decodedBytes, StandardCharsets.UTF_8)

        val jsonData = JSON.parseObject(decodedString)
        val phyCardId = jsonData?.getString("physicalCardId")
        val userName = jsonData?.getString("userName")

        println(phyCardId + userName)
    }
}