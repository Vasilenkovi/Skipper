package com.example.skipperproject.MobilePackage.CommonUI.FirstSteps

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skipperproject.R
import com.example.skipperproject.MobilePackage.CommonUI.Tools.SkipperScreen
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperDimensions
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperProjectTheme

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                MainFunc()
            }
        }
    }
}

@Composable
fun MainFunc() {
    SkipperScreen {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            WelcomeMessage() // Сообщение "Приём, Skipper?"

            Spacer(modifier = Modifier.height(32.dp))

            ReadyToHelpMessage() // Сообщение "Мы готовы помочь!"

            Spacer(modifier = Modifier.height(16.dp))

            FindMentorMessage() // Сообщение "Найди своего ментора"

            Spacer(modifier = Modifier.weight(1f))

            StartSearchButton()

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

// --- Мелкие компоненты ---

@Composable
fun WelcomeMessage() {
    ChatBubble(
        text = stringResource(R.string.welcome_message),
        backgroundColor = SkipperColors.darkGrey,
        alignment = Alignment.Start,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
        textAlign = TextAlign.Left
    )
}

@Composable
fun ReadyToHelpMessage() {
    ChatBubble(
        text = stringResource(R.string.ready_to_help),
        backgroundColor = SkipperColors.darkGrey,
        alignment = Alignment.End,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
        textAlign = TextAlign.Right
    )
}

@Composable
fun FindMentorMessage() {
    ChatBubble(
        text = stringResource(R.string.find_mentor),
        backgroundColor = SkipperColors.darkGrey,
        alignment = Alignment.End,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp),
        textAlign = TextAlign.Right
    )
}

@Composable
fun StartSearchButton() {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = SkipperColors.mainYellow,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(SkipperDimensions.buttonRound),
            contentPadding = PaddingValues(horizontal = SkipperDimensions.mediumButtonHor, vertical = SkipperDimensions.mediumButtonVert)
        ) {
            Text(
                text = stringResource(R.string.start_search),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ChatBubble(
    text: String,
    backgroundColor: Color,
    alignment: Alignment.Horizontal,
    shape: RoundedCornerShape,
    textAlign: TextAlign
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = backgroundColor,
            shape = shape
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = SkipperDimensions.bubbleHorSpace, vertical = SkipperDimensions.bubbleVertSpace),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                textAlign = textAlign
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    SkipperProjectTheme {
        MainFunc()
    }
}
