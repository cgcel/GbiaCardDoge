package com.cgcel.gbiacarddoge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cgcel.gbiacarddoge.datastore.UserStore
import com.cgcel.gbiacarddoge.screen.LoginPage
import com.cgcel.gbiacarddoge.screen.QrCodePage
import com.cgcel.gbiacarddoge.ui.theme.BaiyunCardTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val datastore = UserStore(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                val savedToken = datastore.getUserToken.first()
                val savedSessionID = datastore.getUserSessionID.first()
                val savedPhyCardId = datastore.getUserPhyCardId.first()
                val savedUserName = datastore.getUserName.first()
                val savedUserId = datastore.getUserId.first()

                val startDestination =
                    if (savedToken.isNullOrBlank() || savedSessionID.isNullOrBlank() || savedPhyCardId.isNullOrBlank() || savedUserName.isNullOrBlank() || savedUserId.isNullOrBlank()) {
                        "login"
                    } else {
                        "qrCode"
                    }

                setContent {
                    val navController = rememberNavController()
                    BaiyunCardTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
    //                    color = MaterialTheme.colorScheme.primary
                        ) {
                            MyNavHost(navController, startDestination)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginPage(navController)
        }
        composable("qrCode") {
            QrCodePage(navController)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    BaiyunCardTheme {
//        Greeting(Modifier.fillMaxSize(), )
//    }
//}