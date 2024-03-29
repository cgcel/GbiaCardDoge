package com.cgcel.gbiacarddoge.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.alibaba.fastjson2.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class HttpHelper {

    /*
    * 获取验证码
    * */
    fun getVerifyCode(phoneNumber: String) {
        val url = "http://ykt.baiyunairport.com:80/ykt/message/sendSms?phoneNum=$phoneNumber"
        val request = Request.Builder()
            .url(url)
            .build()

        val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
            .build()
        val client = OkHttpClient.Builder()
            .connectionSpecs(listOf(spec))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                println("Response: $responseBody")
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Error: ${e.message}")
            }
        })
    }

    /*
    * 登录函数
    * */
    suspend fun login(phoneNumber: String, verificationCode: String): String? {
        val url = "http://ykt.baiyunairport.com/ykt/appletlogin/phoneLogin"

        val mediaType = "application/json;charset=UTF-8".toMediaTypeOrNull()
        val body = "{\"phone\":$phoneNumber,\"captcha\":$verificationCode}"
            .toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .method("POST", body)
            .addHeader("Host", "ykt.baiyunairport.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("Content-Length", "42")
            .addHeader("Accept", "application/json, text/plain, */*")
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
            )
            .addHeader("Content-Type", " application/json;charset=UTF-8 ")
            .addHeader("Origin", "http://ykt.baiyunairport.com")
            .addHeader("X-Requested-With", "com.tencent.mm")
            .addHeader("Referer", "http://ykt.baiyunairport.com/login")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .addHeader("Cookie", "JSESSIONID=")
            .build()

        val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
            .build()
        val client = OkHttpClient.Builder()
            .connectionSpecs(listOf(spec))
            .build()
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                ""
            } else {
                val responseBody = response.body?.string()
                responseBody
            }
        }
    }

    /*
    * 请求二维码bitmap
    * */
    suspend fun getQrCodeBitmap(
        savedToken: String,
        savedSessionID: String,
        savedPhyCardId: String
    ): Bitmap? {
        val url =
            "http://ykt.baiyunairport.com/ykt/homepage/qrcode?codeContent=${savedPhyCardId}"
        val request = Request.Builder().url(url)
            .addHeader("Host", "ykt.baiyunairport.com")
            .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "application/json, text/plain, */*")
            .addHeader("Accept", "application/octet-stream")
            .addHeader(
                "Applet-Token", savedToken
            )
            .addHeader("Session-ID", savedSessionID)
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
            )
            .addHeader("X-Requested-With", "com.tencent.mm")
            .addHeader("Referer", "http://ykt.baiyunairport.com/payment")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader(
                "Accept-Language",
                "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7"
            )
            .addHeader("Cookie", "JSESSIONID=${savedSessionID}")
            .build()

        val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
        val client =
            OkHttpClient.Builder().connectionSpecs(listOf(spec)).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                null
            } else {
                val responseBody = response.body?.byteStream()
                BitmapFactory.decodeStream(responseBody)
            }
        }
    }

    /*
    * 获取验证码
    * */
    suspend fun getWalletDetails(
        savedToken: String,
        savedSessionID: String,
        savedUserId: String
    ): String? {
        val url =
            "http://ykt.baiyunairport.com:80/ykt/homepage/getwalletDetaileds?cardAccountId=$savedUserId"
        val request = Request.Builder().url(url)
            .addHeader("Host", "ykt.baiyunairport.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("Accept", "application/json, text/plain, */*")
            .addHeader(
                "Applet-Token", savedToken
            )
            .addHeader("Session-ID", savedSessionID)
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
            )
            .addHeader("X-Requested-With", "com.tencent.mm")
            .addHeader("Referer", "http://ykt.baiyunairport.com/home")
            .addHeader("Accept-Encoding", "gzip, deflate")
            .addHeader(
                "Accept-Language",
                "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7"
            )
            .addHeader("Cookie", "JSESSIONID=${savedSessionID}")
            .build()

        val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
            .build()
        val client = OkHttpClient.Builder()
            .connectionSpecs(listOf(spec))
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                null
            } else {
                response.body?.string()
            }
        }
    }

    /*
    * 检查token是否有效
    * */
    suspend fun checkIsLogin(savedToken: String, savedSessionID: String) :Boolean {
        val url = "http://ykt.baiyunairport.com/ykt/orderFood/showOrderFood"
        val request =
            Request.Builder().url(url).addHeader("Host", "ykt.baiyunairport.com")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader(
                    "Applet-Token", savedToken
                )
                .addHeader("Session-ID", savedSessionID)
                .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
                )
                .addHeader("X-Requested-With", "com.tencent.mm")
                .addHeader("Referer", "http://ykt.baiyunairport.com/payment")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Cookie", "JSESSIONID=${savedSessionID}")
                .build()

        val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
        val client = OkHttpClient.Builder().connectionSpecs(listOf(spec)).build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                false
            } else {
                val responseBody = response.body?.string()
//                println(responseBody)
                val jsonData = JSON.parseObject(responseBody) // JSONObject
                val code = jsonData.getIntValue("code")
                code == 200
            }
        }
    }
}