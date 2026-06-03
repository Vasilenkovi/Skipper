package com.example.skipperproject.MobilePackage.CommonUI.Profiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.Tools.*
import com.example.skipperproject.MobilePackage.CommonUI.theme.*

class PersonalProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                PersonalProfileScreen()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview() {
    PersonalProfileScreen()
}

@Composable
fun PersonalProfileScreen() {
    SkipperScreen2 {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Блок: Профиль (используем новый компонент из Tools)
            UserProfileHeader(
                name = "Герасимов\nНиколай\nВалерьевич",
                role = "ментор"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Блок: Расписание
            SectionCard(title = "Ваше расписание") {
                CalendarSection()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Блок: Курсы
            SectionCard(title = "Ваши курсы") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CourseYellowCard(
                        title = "Путь к успеху",
                        description = "Становимся крутыми за пару занятий. Еще какой-нибудь текст, можно многое что написать.",
                        price = "10.000 ₽"
                    )
                    CourseYellowCard(
                        title = "Путь к успеху",
                        description = "Становимся крутыми за пару занятий. Еще какой-нибудь текст...",
                        price = "10.000 ₽"
                    )
                    Text(
                        text = "Смотреть ещё",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { },
                        textAlign = TextAlign.Center,
                        style = MobileTextStyles.HelpButtonSmallUnderlined,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Блок: Ближайшие занятия
            SectionCard(title = "Ближайшие занятия") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LessonInfoCard(
                        courseTitle = "Путь к успеху",
                        mentorName = "Романов Роман Романович"
                    )
                    LessonInfoCard(
                        courseTitle = "Путь к успеху",
                        mentorName = "Романов Роман Романович",
                        date = "01.05.26",
                        time = "10:00 - 11:00"
                    )
                    Text(
                        text = "Смотреть ещё",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { },
                        textAlign = TextAlign.Center,
                        style = MobileTextStyles.HelpButtonSmallUnderlined,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(SkipperDimensions.dialogRound),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MobileTextStyles.QuestionText.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.Edit, contentDescription = "Edit", Modifier.size(14.dp), Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun CalendarSection() {
    Column {
        Text("◄Май►", style = MobileTextStyles.QuestionText, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                Text(it, style = MobileTextStyles.SmallestText, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            for (i in 9..15) {
                CalendarDayCell(day = i.toString(), isSelected = i == 11, isDark = i < 11)
            }
        }
    }
}
