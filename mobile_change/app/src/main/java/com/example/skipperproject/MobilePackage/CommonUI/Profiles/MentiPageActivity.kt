package com.example.skipperproject.MobilePackage.CommonUI.Profiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.Tools.*
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperProjectTheme

class MentiPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                MentiPageScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun MentiPageScreen(onBack: () -> Unit = {}) {
    val menti = Menti(
        name = "Романов\nРоман\nРоманович"
    )

    val lessons = listOf(
        MentiLesson("Путь к успеху",  "Герасимов Николай Валерьевич", "01.05.26", "10:00 - 11:00"),
        MentiLesson("Путь к успеху","Герасимов Николай Валерьевич", "01.05.26", "10:00 - 11:00"),
        MentiLesson("Путь к успеху",  "Герасимов Николай Валерьевич", "01.05.26", "10:00 - 11:00")
    )

    SkipperScreen2(onBackClick = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Заголовок "Профиль менти"
            Text(
                text = "Профиль менти",
                style = MobileTextStyles.MainScreenText.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Блок с информацией о менти (из MentiComponents)
            MentiHeader(menti)

            Spacer(modifier = Modifier.height(24.dp))

            // Секция "Ваши занятия"
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ваши занятия",
                        style = MobileTextStyles.QuestionText.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        lessons.forEach { lesson ->
                            MentiLessonCard(lesson)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Смотреть ещё",
                            style = MobileTextStyles.HelpButtonSmallUnderlined,
                            color = Color.Black,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
