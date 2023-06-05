package com.cgcel.gbiacarddoge.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alibaba.fastjson2.JSON
import com.cgcel.gbiacarddoge.R
import com.cgcel.gbiacarddoge.datastore.HelpingStore
import com.cgcel.gbiacarddoge.datastore.SingleHelpingData
import com.cgcel.gbiacarddoge.datastore.UserStore
import com.cgcel.gbiacarddoge.ui.theme.BaiyunCardTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpingPage(
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val helpingstore = HelpingStore(context)
    val userstore = UserStore(context)

    // 记录 helpingstore 中存储的代刷数据 json
    var storeData by remember { mutableStateOf("") }
    var savedToken by remember { mutableStateOf("") }
    var savedSessionID by remember { mutableStateOf("") }
    var savedPhyCardID by remember { mutableStateOf("") }
    var savedUserName by remember { mutableStateOf("") }
    var savedUserID by remember { mutableStateOf("") }

    // 记录 alertdialog 显示状态位
    var showFavDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // 记录返回的钱包信息
    val walletDetails by remember { mutableStateOf("") }

    var selectedItem by remember { mutableStateOf(2) }
    val items = listOf(
        context.getString(R.string.qrcode_page_icon_text),
        context.getString(R.string.wallet_page_icon_text),
        context.getString(R.string.helping_page_icon_text),
        context.getString(R.string.settings_page_icon_text),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        context.getString(R.string.app_name),
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
    ) {
        var state by remember { mutableStateOf(1) }
        val titles = listOf("我的代刷", "托人代刷")

        Column(
            modifier = Modifier.padding(top = 60.dp)
        ) {
            TabRow(selectedTabIndex = state) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            // 选择 tab 1 时
            if (state == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {

                        // 读取 datastore 中代刷数据
                        scope.launch {
                            storeData = helpingstore.getHelpingData.first()
                        }

                        if (storeData.isEmpty()) {
                            Text(text = "暂无代刷任务")
                        }

                        LazyColumn {
                            items(storeData.length) {
                                Card(
                                    onClick = { /* Do something */ },
                                    modifier = Modifier
                                        .height(100.dp)
                                        .fillMaxWidth()
                                ) {
                                    Box(Modifier.fillMaxSize()) {
                                        Text("Clickable", Modifier.align(Alignment.Center))
                                    }
                                }
                                Spacer(modifier = Modifier.padding(5.dp))

                            }
                        }

                    }
                    FilledTonalButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 15.dp, end = 15.dp),
                        shape = MaterialTheme.shapes.large

                    ) {
                        Text("帮人代刷")
                    }
                }
            }

            // 选择 tab 2 时
            if (state == 1) {
                Box(modifier = Modifier.fillMaxSize()) {
                    FilledTonalButton(
                        onClick = {
                            /*TODO*/
                            scope.launch {
                                savedToken = userstore.getUserToken.first()
                                savedSessionID = userstore.getUserSessionID.first()
                                savedPhyCardID = userstore.getUserPhyCardId.first()
                                savedUserName = userstore.getUserName.first()
                                savedUserID = userstore.getUserId.first()

                                val singleHelpingData = SingleHelpingData(
                                    savedToken,
                                    savedSessionID,
                                    savedPhyCardID,
                                    savedUserName,
                                    savedUserID,
                                    SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        Locale.getDefault()
                                    ).format(
                                        Date()
                                    )
                                )

                                val encodedData = Base64.getEncoder().encodeToString(
                                    JSON.toJSONString(singleHelpingData).toByteArray()
                                )
//                                println(encodedData)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 100.dp, end = 15.dp),

                        ) {
                        Text("请人代刷")
                    }
                }
            }

        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {


            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            if (index == 0) Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = item
                            )
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

        }

        LaunchedEffect(selectedItem) {
            if (selectedItem == 0) {
                navController.navigate("qrCode")
            }
            if (selectedItem == 3) {
                navController.navigate("settings")
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
                text = {
                    Text(
                        context.getString(R.string.app_name_without_emoji) + " - " + context.getString(
                            R.string.app_version
                        ) + "\n" + context.getString(R.string.app_desc)
                    )
                },
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
}


@Preview(showBackground = true)
@Composable
fun HelpingPagePreview() {
    val navController = rememberNavController()
    BaiyunCardTheme {
        HelpingPage(navController)
    }
}