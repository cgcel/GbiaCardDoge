package com.cgcel.gbiacarddoge.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alibaba.fastjson2.JSON
import com.cgcel.gbiacarddoge.R
import com.cgcel.gbiacarddoge.datastore.UserStore
import com.cgcel.gbiacarddoge.ui.theme.BaiyunCardTheme
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.math.BigDecimal


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodePage(navController: NavHostController) {
    val context = LocalContext.current
    val datastore = UserStore(context)

    val savedToken = datastore.getUserToken.collectAsState(initial = "")
    val savedSessionID = datastore.getUserSessionID.collectAsState(initial = "")
    val savedPhyCardId = datastore.getUserPhyCardId.collectAsState(initial = "")
    val savedUserName = datastore.getUserName.collectAsState(initial = "")
    val savedUserId = datastore.getUserId.collectAsState(initial = "")

    var allValuesAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        allValuesAvailable = savedToken.value.isNotBlank() &&
                savedSessionID.value.isNotBlank() &&
                savedPhyCardId.value.isNotBlank() &&
                savedUserName.value.isNotBlank() &&
                savedUserId.value.isNotBlank()
    }

    // 先这样解决, 否则页面跳转有问题
    SideEffect {
        allValuesAvailable = savedToken.value.isNotBlank() &&
                savedSessionID.value.isNotBlank() &&
                savedPhyCardId.value.isNotBlank() &&
                savedUserName.value.isNotBlank() &&
                savedUserId.value.isNotBlank()
    }

    if (allValuesAvailable) {
        // 所有状态值都可用，进行渲染
        // 可以在这里引用上述四个参数进行下一步操作
        ShowQrCodePage(
            navController = navController,
            savedToken = savedToken.value,
            savedSessionID = savedSessionID.value,
            savedPhyCardId = savedPhyCardId.value,
            savedUserName = savedUserName.value,
            savedUserId = savedUserId.value
        )
    } else {
        // 状态值还未全部获取到，可以显示一个加载动画或其他占位符
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowQrCodePage(
    navController: NavHostController,
    savedToken: String,
    savedSessionID: String,
    savedPhyCardId: String,
    savedUserName: String,
    savedUserId: String
) {
    val context = LocalContext.current
    val httpHelper = HttpHelper()

    var isLogin by remember { mutableStateOf(true) }

    val formatUserName = Util().formatUserName(savedUserName)

    var bitmap by remember {
        mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    }

    var showFavDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }

    // 记录返回的钱包信息
    var walletDetails by remember { mutableStateOf("") }
    var scope  = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        LocalContext.current.getString(R.string.app_name) + " - " + formatUserName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        /* doSomething() */
                        showInfoDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Show the App Info"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        /* doSomething() */
                        showFavDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Show the Fav Dialog"
                        )
                    }
                }
            )
        },

        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = {
                        /* doSomething() */
                        scope.launch {
                            val resp = httpHelper.getWalletDetails(savedToken, savedSessionID, savedUserId)
                            if (resp != null) {
                                walletDetails = resp
                                showWalletDialog = true
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Person, contentDescription = "Get wallet details")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if (savedToken.isNotEmpty() && savedSessionID.isNotEmpty()) {
                                val url =
                                    "http://ykt.baiyunairport.com/ykt/homepage/qrcode?codeContent=${savedPhyCardId}"
                                val request = Request.Builder().url(url)
                                    .addHeader("Host", "ykt.baiyunairport.com")
                                    .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "application/json, text/plain, */*")
                                    .addHeader("Accept", "application/octet-stream").addHeader(
                                        "Applet-Token", savedToken
                                    )
                                    .addHeader("Session-ID", savedSessionID).addHeader(
                                        "User-Agent",
                                        "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
                                    ).addHeader("X-Requested-With", "com.tencent.mm")
                                    .addHeader("Referer", "http://ykt.baiyunairport.com/payment")
                                    .addHeader("Accept-Encoding", "gzip, deflate")
                                    .addHeader(
                                        "Accept-Language",
                                        "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7"
                                    )
                                    .addHeader("Cookie", "JSESSIONID=${savedSessionID}").build()

                                val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
                                val client =
                                    OkHttpClient.Builder().connectionSpecs(listOf(spec)).build()
                                client.newCall(request).enqueue(object : Callback {
                                    override fun onResponse(call: Call, response: Response) {
                                        val responseBody = response.body?.byteStream()
                                        bitmap = BitmapFactory.decodeStream(responseBody)
                                    }

                                    override fun onFailure(call: Call, e: IOException) {
                                        println("Error: ${e.message}")
                                    }
                                })
                            }

                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Refresh, "Localized description")
                    }
                }
            )
        }
    ) {

    }

    LaunchedEffect(Unit) {
        if (savedToken.isNotEmpty() && savedSessionID.isNotEmpty()) {
            val url = "http://ykt.baiyunairport.com/ykt/orderFood/showOrderFood"
            val request =
                Request.Builder().url(url).addHeader("Host", "ykt.baiyunairport.com")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept", "application/json, text/plain, */*").addHeader(
                        "Applet-Token", savedToken
                    ).addHeader("Session-ID", savedSessionID).addHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
                    ).addHeader("X-Requested-With", "com.tencent.mm")
                    .addHeader("Referer", "http://ykt.baiyunairport.com/payment")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                    .addHeader("Cookie", "JSESSIONID=${savedSessionID}").build()

            val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
            val client = OkHttpClient.Builder().connectionSpecs(listOf(spec)).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val jsonData = JSON.parseObject(responseBody) // JSONObject
                    val code = jsonData.getIntValue("code")
                    println("responseBody: $responseBody")
                    if (code != 200) {
                        isLogin = false
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    println("Error: ${e.message}")
                }
            })
        } else {
            isLogin = false
        }
    }


    LaunchedEffect(isLogin) {
        if (!isLogin) {
            navController.navigate("login")
        } else {
            if (savedToken.isNotEmpty() && savedSessionID.isNotEmpty()) {
                val url =
                    "http://ykt.baiyunairport.com/ykt/homepage/qrcode?codeContent=${savedPhyCardId}"
                val request =
                    Request.Builder().url(url).addHeader("Host", "ykt.baiyunairport.com")
                        .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "application/json, text/plain, */*")
                        .addHeader("Accept", "application/octet-stream").addHeader(
                            "Applet-Token", savedToken
                        ).addHeader("Session-ID", savedSessionID).addHeader(
                            "User-Agent",
                            "Mozilla/5.0 (Linux; Android 13; XT2301-5 Build/T1TR33.4-30-36; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/107.0.5304.141 Mobile Safari/537.36 XWEB/5023 MMWEBSDK/20230202 MMWEBID/3522 MicroMessenger/8.0.33.2305(0x28002143) WeChat/arm64 Weixin GPVersion/1 NetType/WIFI Language/zh_CN ABI/arm64"
                        ).addHeader("X-Requested-With", "com.tencent.mm")
                        .addHeader("Referer", "http://ykt.baiyunairport.com/payment")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
                        .addHeader("Cookie", "JSESSIONID=${savedSessionID}").build()

                val spec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build()
                val client = OkHttpClient.Builder().connectionSpecs(listOf(spec)).build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.byteStream()
                        bitmap = BitmapFactory.decodeStream(responseBody)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        println("Error: ${e.message}")
                    }
                })
            }
        }
    }



    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .aspectRatio(1f)
        ) {
            Box(Modifier.fillMaxSize()) {
                if (bitmap != null) {
                    val imageBitmap: ImageBitmap = bitmap.asImageBitmap()
                    Image(
                        bitmap = imageBitmap, contentDescription = "My Bitmap",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .scale(1.5f) // 放大
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }

    if (showFavDialog) {
        AlertDialog(
            onDismissRequest = { showFavDialog = false },
            title = { Text("喜欢这个APP吗?") },
            text = { Text("给我个Star") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_VIEW).setData(Uri.parse(context.getString(R.string.project_url)))
                        context.startActivity(intent)
                        showFavDialog = false
                    }
                ) {
                    Text("好")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFavDialog = false }) {
                    Text("下次一定")
                }
            }
        )
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("关于本应用") },
            text = { Text("卡好刷 (Doge)\n使用 Kotlin + Jetpack Compose 构建\nMaterial 3 UI 设计\n极速的亮码体验") },
            confirmButton = {
                TextButton(
                    onClick = { showInfoDialog = false }
                ) {
                    Text("好的")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }

    if (showWalletDialog) {
        val walletObj = JSON.parseObject(walletDetails)
        var walletStr = ""
        val walletData = walletObj["data"] as List<Map<String, Any>>
        for (item in walletData) {
            val walletType = item["walletType"] as String
            val money = item["money"] as BigDecimal
            walletStr += "$walletType：$money 元\n"
        }
        AlertDialog(
            onDismissRequest = { showWalletDialog = false },
            title = { Text("钱包余额") },
            text = { Text(walletStr) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWalletDialog = false
                    }
                ) {
                    Text("好的")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWalletDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QrCodePagePreview() {
    val navController = rememberNavController()
    BaiyunCardTheme {
        QrCodePage(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun ShowQrCodePagePreview() {
    val navController = rememberNavController()
    BaiyunCardTheme {
        ShowQrCodePage(navController, "", "", "", "", "")
    }
}