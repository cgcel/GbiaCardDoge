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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alibaba.fastjson2.JSON
import com.cgcel.gbiacarddoge.R
import com.cgcel.gbiacarddoge.datastore.UserStore
import com.cgcel.gbiacarddoge.ui.theme.BaiyunCardTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodePage(navController: NavHostController) {
    val context = LocalContext.current
    val datastore = UserStore(context)
    val httpHelper = HttpHelper()

    // 记录 isLogin 状态位
    var isLogin by remember { mutableStateOf(true) }

    var savedToken by remember {
        mutableStateOf("")
    }
    var savedSessionID by remember {
        mutableStateOf("")
    }
    var savedPhyCardId by remember {
        mutableStateOf("")
    }
    var savedUserName by remember {
        mutableStateOf("")
    }
    var savedUserId by remember {
        mutableStateOf("")
    }
    var formatUserName by remember {
        mutableStateOf("")
    }

//    val savedToken = datastore.getUserToken.collectAsState(initial = "")
//    val savedSessionID = datastore.getUserSessionID.collectAsState(initial = "")
//    val savedPhyCardId = datastore.getUserPhyCardId.collectAsState(initial = "")
//    val savedUserName = datastore.getUserName.collectAsState(initial = "")
//    val savedUserId = datastore.getUserId.collectAsState(initial = "")

//    formatUserName = Util().formatUserName(savedUserName)

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

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        context.getString(R.string.qrcode_page_icon_text),
        context.getString(R.string.wallet_page_icon_text),
        context.getString(R.string.helping_page_icon_text),
        context.getString(R.string.settings_page_icon_text),
    )

    var pressedIconsCount by remember {mutableStateOf(0)}

    // 进入页面时首先检查 token 是否有效
    LaunchedEffect(Unit){
        scope.launch {
            savedToken = datastore.getUserToken.first()
            savedSessionID = datastore.getUserSessionID.first()
            savedPhyCardId = datastore.getUserPhyCardId.first()
            savedUserName = datastore.getUserName.first()
            savedUserId = datastore.getUserId.first()
            formatUserName = Util().formatUserName(savedUserName)

            if (savedToken.isNotEmpty() && savedSessionID.isNotEmpty()) {
                isLogin = httpHelper.checkIsLogin(savedToken, savedSessionID)
            } else {
                isLogin = false
            }

            if (!isLogin) {
                navController.navigate("login"){
                    popUpTo("qrCode"){
                        inclusive = true
                    }
                }
            } else {
                if (savedToken.isNotBlank() && savedSessionID.isNotBlank()) {
                    scope.launch {
                        bitmap = httpHelper.getQrCodeBitmap(savedToken, savedSessionID, savedPhyCardId)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    ClickableText(
                        AnnotatedString(context.getString(R.string.app_name) + " - " + formatUserName),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp
                        ),
                        onClick = {
                            // 点击文本时的处理逻辑
                            pressedIconsCount ++
                        }
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
        }
    ) {

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

        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        if (index == 0) Icon(Icons.Filled.ShoppingCart, contentDescription = item)
                        if (index == 1) Icon(Icons.Filled.Person, contentDescription = item)
                        if (index == 2) Icon(Icons.Filled.AccountBox, contentDescription = item)
                        if (index == 3) Icon(Icons.Filled.Settings, contentDescription = item)
                           },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index }
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd) // Align to bottom end
                .offset(x = (-15).dp, y = (-100).dp), // Offset by NavigationBar height
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
            Icon(Icons.Filled.Refresh, "QrCode Refresh Button")
        }

    }

    LaunchedEffect(selectedItem){
        if (selectedItem == 1){
            scope.launch {
                val resp =
                    httpHelper.getWalletDetails(savedToken, savedSessionID, savedUserId)
                if (resp != null) {
                    walletDetails = resp
                    showWalletDialog = true
                }
            }
        }
        if (selectedItem == 2){
            showEditDialog = true
        }
        if (selectedItem == 3){
            navController.navigate("settings"){
                popUpTo("qrCode"){
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(pressedIconsCount){
        if (pressedIconsCount == 5){
            navController.navigate("backDoor")
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
            title = { Text(context.getString(R.string.helping_page_title)) },
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
fun ShowQrCodePagePreview() {
    val navController = rememberNavController()
    BaiyunCardTheme {
        QrCodePage(navController)
    }
}