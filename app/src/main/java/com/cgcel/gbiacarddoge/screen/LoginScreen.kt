package com.cgcel.gbiacarddoge.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alibaba.fastjson2.JSON
import com.cgcel.gbiacarddoge.R
import com.cgcel.gbiacarddoge.datastore.UserStore
import com.cgcel.gbiacarddoge.ui.theme.BaiyunCardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.Base64


@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {
    val context = LocalContext.current
    val store = UserStore(context)
    val scope = rememberCoroutineScope()

    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }

    var isLogin by remember { mutableStateOf(false) }
    var loginRespMsg by remember {
        mutableStateOf("")
    }

    val httpHelper = HttpHelper()
    var countdownSeconds by remember { mutableStateOf(0) }
    var isLoading by remember {
        mutableStateOf(false)
    }
    var showFavDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        LocalContext.current.getString(R.string.app_name),
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
                            contentDescription = "Localized description"
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
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
        content = {

        }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone number") },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            maxLines = 1,
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = "Localized description") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = verificationCode,
            onValueChange = { verificationCode = it },
            label = { Text("Verification code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1,
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Info, contentDescription = "Localized description") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 获取验证码按键
        Button(
            onClick = {
                countdownSeconds = 100 // 与原版一样的冷却时长
                httpHelper.getVerifyCode(phoneNumber)
            },
            enabled = countdownSeconds == 0,
            modifier = Modifier.width(280.dp),
            shape = MaterialTheme.shapes.small,

            ) {
            if (countdownSeconds > 0) {
                Text("$countdownSeconds s")
            } else {
                Text("获取验证码")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 登录按键
        Button(
            onClick = {
                isLoading = true
                CoroutineScope(Dispatchers.Main).launch {
                    httpHelper.login(phoneNumber, verificationCode).fold(
                        onSuccess = { result ->
                            val respJsonData = JSON.parseObject(result) // JSONObject
                            val token = respJsonData.getJSONObject("data").getString("token")
                            val payloadData = token.split(".")[1]
                            val decodedPayloadBytes = Base64.getUrlDecoder().decode(payloadData)
                            val decodedPayloadString =
                                String(decodedPayloadBytes, StandardCharsets.UTF_8)
                            val decodePayloadJson = JSON.parseObject(decodedPayloadString)
                            val phyCardId = decodePayloadJson?.getString("physicalCardId")
                            val userName = decodePayloadJson?.getString("userName")
                            val sessionID =
                                respJsonData.getJSONObject("data").getString("sessionID")
                            val code = respJsonData.getIntValue("code")

//                            CoroutineScope(Dispatchers.IO).launch {
//                                store.saveUserToken(token)
//                                store.saveUserSessionID(sessionID)
//                                if (physicalCardId != null) {
//                                    store.saveUserPhyCardId(physicalCardId)
//                                }
//                                if (userName != null) {
//                                    store.saveUserName(userName)
//                                }
//                            }
                            scope.launch {
                                store.saveUserToken(token)
                                store.saveUserSessionID(sessionID)
                                if (phyCardId != null) {
                                    store.saveUserPhyCardId(phyCardId)
                                }
                                if (userName != null) {
                                    store.saveUserName(userName)
                                }
                            }

                            if (code == 200) {
                                // Use the coroutine scope to launch a new coroutine on the main thread
                                isLogin = true
                            } else if (code == 400) {
                                loginRespMsg = respJsonData.getString("message")
                            }
                        },
                        onFailure = { error ->
                            loginRespMsg = error.message ?: "Unknown Error"
                        }
                    )
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.width(280.dp),
            shape = MaterialTheme.shapes.small,
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("登录")
            }
        }

        LaunchedEffect(isLogin) {
            if (isLogin) {
                navController.navigate("qrCode")
            } else {
//                Toast.makeText(context, loginRespMsg, Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(countdownSeconds) {
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds--
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
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    BaiyunCardTheme {
        val navController = rememberNavController()
        LoginPage(navController)
    }
}