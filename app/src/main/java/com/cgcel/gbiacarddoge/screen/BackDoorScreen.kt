package com.cgcel.gbiacarddoge.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cgcel.gbiacarddoge.R
import com.cgcel.gbiacarddoge.datastore.UserStore
import com.cgcel.gbiacarddoge.ui.theme.BaiyunCardTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackDoorPage(
    navController: NavHostController
) {
    val context = LocalContext.current
    val datastore = UserStore(context)

    var savedToken by remember { mutableStateOf("") }
    var savedSessionID by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        context.getString(R.string.debug_screen_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        /* doSomething() */
                        navController.navigate("qrCode")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .padding(top = 100.dp)
            ) {

                Text(
                    text = context.getString(R.string.input_user_token),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.padding(16.dp))

                    OutlinedTextField(
                        value = savedToken,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = { savedToken = it },
                        label = { Text(context.getString(R.string.user_token)) },
                        maxLines = 1,
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = "Token OutlinedTextField"
                            )
                        }
                    )

                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedTextField(
                    value = savedSessionID,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { savedSessionID = it },
                    label = { Text(context.getString(R.string.user_session_id)) },
                    maxLines = 1,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "SessionID OutlinedTextField"
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            datastore.saveUserToken(savedToken)
                            datastore.saveUserSessionID(savedSessionID)
                            Toast.makeText(context, context.getString(R.string.saved_success), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(context.getString(R.string.saving_button))
                }

                Spacer(modifier = Modifier.padding(16.dp))

                Divider(thickness = 5.dp, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.padding(16.dp))

                Text(
                    text = context.getString(R.string.copy_user_token),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.padding(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val userToken = datastore.getUserToken.first()
                                clipboardManager.setText(AnnotatedString((userToken)))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(context.getString(R.string.copy_button) + " " + context.getString(R.string.user_token))
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val userSessionID = datastore.getUserSessionID.first()
                                clipboardManager.setText(AnnotatedString((userSessionID)))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(context.getString(R.string.copy_button) + " " + context.getString(R.string.user_session_id))
                    }

                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BackDoorPagePreview() {
    val navController = rememberNavController()
    BaiyunCardTheme {
        BackDoorPage(navController)
    }
}