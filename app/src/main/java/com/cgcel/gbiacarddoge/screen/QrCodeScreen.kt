package com.cgcel.gbiacarddoge.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
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
import java.math.BigDecimal


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
        navController.navigate("login")
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

    // 记录 isLogin 状态位
    var isLogin by remember { mutableStateOf(true) }

    val formatUserName = Util().formatUserName(savedUserName)

    // 记录二维码 bitmap
    var bitmap by remember {
        mutableStateOf(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    }

    // 记录 alertdialog 显示状态位
    var showFavDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // 记录返回的钱包信息
    var walletDetails by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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
                            val resp =
                                httpHelper.getWalletDetails(savedToken, savedSessionID, savedUserId)
                            if (resp != null) {
                                walletDetails = resp
                                showWalletDialog = true
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Person, contentDescription = "Get wallet details")
                    }
                    IconButton(onClick = {
                        /* doSomething() */
                        showEditDialog = true
                    }) {
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
                                scope.launch {
                                    bitmap = httpHelper.getQrCodeBitmap(
                                        savedToken,
                                        savedSessionID,
                                        savedPhyCardId
                                    )
                                }
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
            isLogin = httpHelper.checkIsLogin(savedToken, savedSessionID)
        } else {
            isLogin = false
        }
    }


    LaunchedEffect(isLogin) {
        if (!isLogin) {
            navController.navigate("login")
        } else {
            if (savedToken.isNotEmpty() && savedSessionID.isNotEmpty()) {
                scope.launch {
                    bitmap = httpHelper.getQrCodeBitmap(savedToken, savedSessionID, savedPhyCardId)
                }
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
            title = { Text(context.getString(R.string.like_this_app)) },
            text = { Text(context.getString(R.string.ask_for_star)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_VIEW).setData(Uri.parse(context.getString(R.string.project_url)))
                        context.startActivity(intent)
                        showFavDialog = false
                    }
                ) {
                    Text(context.getString(R.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFavDialog = false }) {
                    Text(context.getString(R.string.dialog_close))
                }
            }
        )
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(context.getString(R.string.about_app)) },
            text = { Text(context.getString(R.string.app_name_without_emoji) + " - " + context.getString(R.string.app_version) + "\n" + context.getString(R.string.app_desc)) },
            confirmButton = {
                TextButton(
                    onClick = { showInfoDialog = false }
                ) {
                    Text(context.getString(R.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(context.getString(R.string.dialog_close))
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
            title = { Text(context.getString(R.string.wallet_details_title)) },
            text = { Text(walletStr) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWalletDialog = false
                    }
                ) {
                    Text(context.getString(R.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showWalletDialog = false }) {
                    Text(context.getString(R.string.dialog_close))
                }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
//            title = { Text("施工中...") },
            text = { Text(context.getString(R.string.unfinished_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                    }
                ) {
                    Text(context.getString(R.string.dialog_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(context.getString(R.string.dialog_close))
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