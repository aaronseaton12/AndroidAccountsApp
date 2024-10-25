package com.aaronseaton.accounts.presentation.about

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.aaronseaton.accounts.R
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalGraphicsApi
@Composable
fun AboutScreen(navigateTo: (String) -> Unit) {
    val context = LocalContext.current
    val description = "Back"
    val title = "About This Application"
    val leftIcon = Icons.Default.Person
    val onLeftIcon = { navigateTo(Routes.HOME) }
    val companyImage = R.drawable.aaronseaton
    val facebookImage = R.drawable.facebook_icon
    val twitterImage = R.drawable.twitter_icon
    val linkedinImage = R.drawable.linkedin_icon
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon, actions = {
                IconButton({ expanded = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Account") },
                        onClick = { navigateTo(Routes.INDIVIDUAL_USER + "/" + "NOVALUE") }
                    )
                    DropdownMenuItem(
                        text = { Text("Business") },
                        onClick = { navigateTo(Routes.BUSINESS_LIST) }
                    )
                    DropdownMenuItem(
                        text = { Text("About") },
                        onClick = { navigateTo(Routes.ABOUT_SCREEN) }
                    )
                }
            })
        },
        bottomBar = { AllBottomBar(navigateTo) }
    ) {
        AboutScreenContent(
            context,
            companyImage,
            facebookImage,
            twitterImage,
            linkedinImage,
        )
    }
}

@ExperimentalGraphicsApi
@Composable
fun AboutScreenContent(
    context: Context,
    companyImage: Int,
    facebookImage: Int,
    twitterImage: Int,
    linkedinImage: Int
) {
    val lightness = if (isSystemInDarkTheme()) 0.8F else 0.45F
    val scrollState = rememberScrollState(1)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
            /*.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary,
                        MaterialTheme.colors.background
                    )
                )
            )*/
        ) {
            Image(
                painter = painterResource(companyImage),
                contentDescription = "Aaron",
                modifier = Modifier
                    .size(125.dp)
                    .border(3.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.BottomCenter)
            )
        }
        Spacer(Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Aaron Seaton",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Mobile Developer",
                //style = MaterialTheme.typography.body2
            )
            Text(
                text = "I make apps for fun and to help my legal practice.",
                textAlign = TextAlign.Center,
                //style = MaterialTheme.typography.body2
            )
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(
                Modifier.padding(horizontal = 15.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/aseaton")
                            )
                        startActivity(context, browserIntent, null)
                    }
                ) {
                    Icon(
                        painter = painterResource(facebookImage),
                        contentDescription = null,
                        tint = Color.hsl(
                            230F,
                            1.0F,
                            lightness,
                            1.0F,
                            ColorSpaces.AdobeRgb
                        )
                    )
                    Text("Facebook")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.twitter.com/aaronseaton12")
                            )
                        startActivity(context, browserIntent, null)
                    }
                ) {
                    Icon(
                        painter = painterResource(twitterImage),
                        contentDescription = null,
                        tint = Color.hsl(
                            200F,
                            1.0F,
                            lightness,
                            1.0F,
                            ColorSpaces.AdobeRgb
                        )
                    )
                    Text("Twitter")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        val browserIntent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.linkedin.com/in/aaronseatonlaw/")
                            )
                        startActivity(context, browserIntent, null)
                    }
                ) {
                    Icon(
                        painter = painterResource(linkedinImage),
                        contentDescription = null,
                        tint = Color.hsl(
                            230F,
                            0.85F,
                            lightness,
                            1.0F,
                            ColorSpaces.AdobeRgb
                        )
                    )
                    Text("LinkedIn")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_EMAIL, "aaronseaton12@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "About your App")
                        }
                        startActivity(context, intent, null)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.hsl(
                            43F,
                            0.85F,
                            lightness,
                            1.0F,
                            ColorSpaces.AdobeRgb
                        )
                    )
                    Text("Email")
                }
            }
        }
    }
}

@OptIn(ExperimentalGraphicsApi::class)
@Preview
@Preview(name = "Night Mode", uiMode = UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun TestAbout() {
    AccountsTheme {
        AboutScreen(navigateTo = {})
    }
}