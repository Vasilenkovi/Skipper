package com.example.skipperproject.MobilePackage.CommonUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.skipperproject.R
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperDimensions
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperProjectTheme

@Composable
fun EmailConfirmedDialog(
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        EmailConfirmedContent(onContinue = onContinue)
    }
}

@Composable
fun EmailConfirmedContent(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(SkipperDimensions.dialogRound),
        color = SkipperColors.lightGrey,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SkipperDimensions.dialogPadding)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = SkipperDimensions.dialogInterPaddingH,
                    vertical = SkipperDimensions.dialogInterPaddingV
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailConfirmedTitle()

            Spacer(modifier = Modifier.height(SkipperDimensions.bubbleVertSpace))

            EmailConfirmedSubtitle()

            Spacer(modifier = Modifier.height(SkipperDimensions.dialogPadding))

            ContinueButton(onClick = onContinue)
        }
    }
}

@Composable
private fun EmailConfirmedTitle() {
    Text(
        text = stringResource(R.string.email_confirmed_title),
        style = MobileTextStyles.ButtonsText.copy(
            fontSize = 24.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center
        ),
        color = Color.Black
    )
}

@Composable
private fun EmailConfirmedSubtitle() {
    Text(
        text = stringResource(R.string.email_confirmed_subtitle),
        style = MobileTextStyles.QuestionText.copy(
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        ),
        color = Color.Black
    )
}

@Composable
private fun ContinueButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = SkipperColors.mainYellow,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(SkipperDimensions.buttonRound),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.5.dp)
    ) {
        Text(
            text = stringResource(R.string.continue_button),
            style = MobileTextStyles.QuestionText.copy(
                fontWeight = FontWeight.Bold,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            modifier = Modifier.width(168.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmailConfirmedDialogPreview() {
    SkipperProjectTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmailConfirmedContent(onContinue = {})
        }
    }
}