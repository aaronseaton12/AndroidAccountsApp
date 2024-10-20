package com.aaronseaton.accounts.presentation.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun AccountsDialog(
    showDialog: Boolean,
    positiveButtonLabel: String = "Yes",
    negativeButtonLabel: String = "No",
    onNegativeButtonPressed: () -> Unit,
    onPositiveButtonPressed: () -> Unit,
    content: @Composable () -> Unit
) {
    if (showDialog)
        Dialog(onNegativeButtonPressed) {
            Surface {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 30.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        content()
                    }

                    AccountDivider()
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .clickable { onPositiveButtonPressed() }
                                .weight(0.5f)
                                .border(
                                    Dp.Hairline,
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                                )
                        ) {
                            Text(
                                text = positiveButtonLabel,
                                modifier = Modifier.padding(vertical = 20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .clickable { onNegativeButtonPressed() }
                                .weight(0.5f)
                                .border(
                                    Dp.Hairline,
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                                )
                        ) {
                            Text(
                                text = negativeButtonLabel,
                                modifier = Modifier.padding(vertical = 20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun AccountsDialogPreview() {
    Scaffold {
        AccountsDialog(
            showDialog = true,
            onNegativeButtonPressed = { },
            onPositiveButtonPressed = { }) {
            Text("This is a test")
        }
    }
}