package com.example.skipperproject.MobilePackage.CommonUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// Используем набор Rounded для более мягкого стиля
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.R
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

@Composable
fun PersonalProfileScreen() {
    SkipperScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Блок: Профиль
            ProfileHeaderCard()

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
                        modifier = Modifier.fillMaxWidth().clickable { },
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
                        modifier = Modifier.fillMaxWidth().clickable { },
                        textAlign = TextAlign.Center,
                        style = MobileTextStyles.HelpButtonSmallUnderlined,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            BottomNavBarMock()
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeaderCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(SkipperDimensions.dialogRound)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Личный профиль",
                    style = MobileTextStyles.QuestionText.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                // Используем Icons.Rounded.Edit
                Icon(Icons.Rounded.Edit, contentDescription = "Edit", Modifier.size(14.dp), Color.Gray)
                Spacer(Modifier.weight(1f))
                Surface(color = SkipperColors.mainYellow, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = "ментор",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MobileTextStyles.SmallestText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row {
                Box(Modifier.size(80.dp).background(Color.LightGray, RoundedCornerShape(8.dp)))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Герасимов\nНиколай\nВалерьевич",
                        style = MobileTextStyles.QuestionText.copy(lineHeight = 20.sp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(painterResource(R.drawable.vk_icon), null, Modifier.size(24.dp), Color.Unspecified)
                        Icon(painterResource(R.drawable.whatsapp_icon), null, Modifier.size(24.dp), Color.Unspecified)
                        Icon(painterResource(R.drawable.telegram_icon), null, Modifier.size(24.dp), Color.Unspecified)
                    }
                }
            }
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
                Text(title, style = MobileTextStyles.QuestionText.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold)
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

@Composable
fun BottomNavBarMock() {
    Surface(
        color = SkipperColors.mainYellow,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).background(Color.Black, RoundedCornerShape(4.dp)))
                Spacer(Modifier.width(8.dp))
                Text("Skipper", style = MobileTextStyles.QuestionText, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Используем закругленные версии
                Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                Icon(Icons.Rounded.Notifications, contentDescription = "Notifications")
                Icon(Icons.Rounded.Person, contentDescription = "Profile")
            }
        }
    }
}
