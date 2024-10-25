package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Util

@Composable
fun FinancialSummary(
    receipts: List<Receipt>,
    payments: List<Payment>,
    modifier: Modifier = Modifier
){
    val lightness = if (isSystemInDarkTheme()) 0.7F else 0.4F
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 0.85,
        fontSize = 15.sp
    )
    val netIncome = receipts.sumOf { it.amount } - payments.sumOf { it.amount }

    Surface(tonalElevation = 0.25.dp) {
        Column(modifier = modifier) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Earnings", style = labelStyle)
                Text(
                    text = "$${Util.decimalFormat.format(receipts.sumOf { it.amount })}",
                    style = labelStyle.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.hsl(
                            133F,
                            0.1F,
                            lightness,
                            1.0F,
                            ColorSpaces.AdobeRgb
                        )
                    )
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Expenses", style = labelStyle)
                Text(
                    text = "$${Util.decimalFormat.format(payments.sumOf { it.amount })}",
                    style = labelStyle.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.hsl(
                            0F,
                            0.15F,
                            lightness,
                            1.0F,
                            ColorSpaces.AdobeRgb
                        )
                    )
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Total Net Income", style = labelStyle)
                Text(
                    text = "$${ Util.decimalFormat.format( netIncome ) }",
                    style = labelStyle.copy(
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            }
        }
    }
}

@Preview (apiLevel = 33)
@Composable
fun FinancialSummaryPreview () {
    val title = "Individual Customer"
    val leftIcon = Icons.AutoMirrored.Filled.ArrowBack
    val onLeftIcon = { }
    AccountsTheme {
        Scaffold (
            topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
            bottomBar = { AllBottomBar({}) },
        ){
            Surface(modifier = Modifier.padding(it), tonalElevation = 10.dp) {
                Column (Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)){
                    HeaderComponent(
                        title = "Title of Item",
                        actionIcon = {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        },
                        menuItems = {
                            DropdownMenuItem(
                                text = { Text("Edit") }, onClick = { }
                            )
                        }
                    ) {
                        Text(
                            text = "Content line 1",
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                        Text(
                            text = "Much more context than the previous line. This is indeed a great and wonderful thing",
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                    }
                    FinancialSummary(
                        receipts = TestInfo.listOfTestReceipts,
                        payments = TestInfo.listOfTestPayments,

                    )
                }
            }
        }
    }

}