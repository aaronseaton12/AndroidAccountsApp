package com.aaronseaton.accounts.presentation.login

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaronseaton.accounts.R
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "LoginScreen"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel()
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            task.addOnSuccessListener { googleSignInAccount ->
                val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                loginViewModel.signWithCredential(credential)
            }.addOnFailureListener { exception ->
                Log.e("TAG", "Google sign in failed", exception)
            }
        }
    val context = LocalContext.current
    val token = stringResource(id = R.string.web_client_id)
    val googleSignInClient = GoogleSignIn.getClient(
        context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    )
    val signInLauncher = {
        launcher.launch(googleSignInClient.signInIntent).also {
            Log.d(TAG, "Sign In Launcher()")
        }
    }

    Log.d(TAG, "List of Tasks Impl")
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.receipt_icon_two),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp, 100.dp),
                )
                Text(
                    "Accounts App",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )


                OutlinedButton(
                    modifier = Modifier
                        .width(350.dp)
                        .heightIn()
                        .padding(horizontal = 20.dp),
                    onClick = { signInLauncher() },
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(
                        Dp.Hairline,
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    content = {
                        Icon(
                            tint = Color.Unspecified,
                            painter = painterResource(id = com.google.android.gms.base.R.drawable.googleg_standard_color_18),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                        Text(
                            text = "Sign in with Google"
                        )
                    }
                )

                OutlinedButton(
                    modifier = Modifier
                        .width(350.dp)
                        .heightIn()
                        .padding(horizontal = 20.dp),
                    onClick = { },
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(
                        Dp.Hairline,
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    content = {
                        Icon(
                            painterResource(id = R.drawable.logout_icon),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                        Text(text = "Logout of Accounts")
                    }
                )

                Text(
                    text = "Please sign in to enjoy all the benefits of this app and much, much more!",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier
                        .width(350.dp)
                        .heightIn()
                        .padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun LoginScreenPreview() {
    AccountsTheme {
        LoginScreen(loginViewModel = viewModel<LoginViewModel>())
    }
}