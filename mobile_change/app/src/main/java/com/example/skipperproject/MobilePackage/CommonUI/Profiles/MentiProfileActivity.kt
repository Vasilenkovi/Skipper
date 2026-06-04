package com.example.skipperproject.MobilePackage.CommonUI.Profiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.EditingProfile
import com.example.skipperproject.MobilePackage.CommonUI.Tools.*
import com.example.skipperproject.MobilePackage.CommonUI.theme.*
import com.example.skipperproject.R

class MentiProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                MentiProfileScreen()
            }
        }
    }
}


@Composable
fun MentiProfileScreen() {
    var showEditingProfile by remember { mutableStateOf(false) }

    val menti = Menti(
        name = "Романов\nРоман\nРоманович"
    )

    val currentLessons = listOf(
        MentiLesson("Путь к успеху", "Герасимов Николай Валерьевич", "01.05.26", "10:00 - 11:00"),
        MentiLesson("Путь к успеху", "Герасимов Николай Валерьевич", "01.05.26", "10:00 - 11:00"),
        MentiLesson("Путь к успеху", "Герасимов Николай Валерьевич", "01.05.26", "10:00 - 11:00")
    )

    val pastLessons = listOf(
        MentiLesson("Путь к успеху", "Герасимов Николай Валерьевич", "01.04.26", "10:00 - 11:00", isPast = true),
        MentiLesson("Путь к успеху", "Герасимов Николай Валерьевич", "01.04.26", "10:00 - 11:00", isPast = true),
        MentiLesson("Путь к успеху", "Герасимов Николай Валерьевич", "01.04.26", "10:00 - 11:00", isPast = true)
    )

    if (showEditingProfile) {
        EditingProfile(
            onDismiss = { showEditingProfile = false },
            onSave = { showEditingProfile = false }
        )
    }

    SkipperScreen2 {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Заголовок "Личный профиль"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Личный профиль",
                    style = MobileTextStyles.MainScreenText.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.pen_icon),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showEditingProfile = true },
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Блок с фото и именем
            MentiHeader(menti)

            Spacer(modifier = Modifier.height(24.dp))

            // Секция "Текущие занятия"
            LessonSection(title = "Текущие занятия", lessons = currentLessons, showMore = true)

            Spacer(modifier = Modifier.height(24.dp))

            // Секция "Прошедшие занятия"
            LessonSection(title = "Прошедшие занятия", lessons = pastLessons)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LessonSection(title: String, lessons: List<MentiLesson>, showMore: Boolean = false) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
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
            
            if (showMore) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Смотреть ещё",
                        style = MobileTextStyles.HelpButtonSmallUnderlined,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MentiProfilePreview() {
    SkipperProjectTheme {
        MentiProfileScreen()
    }
}
