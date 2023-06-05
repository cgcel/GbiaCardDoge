package com.cgcel.gbiacarddoge

import com.alibaba.fastjson2.JSON
import com.cgcel.gbiacarddoge.datastore.SingleHelpingData
import org.junit.Test

import org.junit.Assert.*
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale

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

    @Test
    fun test_helping_data_json(){
        val userObj = SingleHelpingData("token", "sessionId", "phyCardId", "username", "userId", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        val jsonStr = JSON.toJSONString(userObj)
        val userStr = "[{\"createTime\":\"2023-06-03 18:45:31\",\"user_phyCardId\":\"phyCardId\",\"user_sessionId\":\"sessionId\",\"user_token\":\"token\",\"user_userId\":\"userId\",\"user_username\":\"username\"},{\"createTime\":\"2023-06-03 18:45:31\",\"user_phyCardId\":\"phyCardId2\",\"user_sessionId\":\"sessionId2\",\"user_token\":\"token2\",\"user_userId\":\"userId2\",\"user_username\":\"username2\"}]"
        val jsonObj = JSON.parseArray(userStr, SingleHelpingData::class.javaObjectType)
        println(jsonStr)
        println(jsonObj[1].user_token)
        jsonObj.remove(jsonObj[0])
        val newJsonStr = JSON.toJSONString(jsonObj)
        println(newJsonStr)
    }
}