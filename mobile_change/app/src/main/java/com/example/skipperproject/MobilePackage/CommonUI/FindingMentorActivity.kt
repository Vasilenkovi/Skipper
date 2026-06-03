package com.example.skipperproject.MobilePackage.CommonUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.Tools.*
import com.example.skipperproject.MobilePackage.CommonUI.theme.*

class FindingMentorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                FindingMentorScreen()
            }
        }
    }
}

@Preview
@Composable
fun FindingMentorScreen() {
    var searchQuery by remember { mutableStateOf("") }

    // Тестовые данные
    val mentors = listOf(
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук, победитель всех на свете"),
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук, победитель всех на свете"),
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук, победитель всех на свете"),
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук, победитель всех на свете")
    )

    SkipperScreen2 {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Приём, Skipper?\nНужна помощь",
                style = MobileTextStyles.MainScreenText.copy(fontSize = 32.sp, lineHeight = 36.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Найди того, кто тебе нужен", style = MobileTextStyles.QuestionText, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            // Поиск с кнопкой-лупой
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Логика поиска */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(32.dp))
                }
                CustomTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Популярные #теги", style = MobileTextStyles.QuestionText, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            // Теги (теперь кликабельные)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip("Бизнес", isSelected = true, onClick = { /* Навигация по тегу Бизнес */ })
                TagChip("Логистика", isSelected = true, onClick = { /* Навигация по тегу Логистика */ })
                TagChip("Веб-дизайн", isSelected = false, onClick = { /* Навигация по тегу Веб-дизайн */ })
                TagChip("Психология", isSelected = false, onClick = { /* Навигация по тегу Психология */ })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Сетка менторов
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mentors) { mentor ->
                    MentorGridCard(mentor, onClick = { /* Переход в профиль ментора */ })
                }
            }
        }
    }
}

@Composable
fun TagChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFF444444) else Color(0xFFBBBBBB),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MobileTextStyles.SmallestText,
            color = Color.White
        )
    }
}

@Composable
fun MentorGridCard(mentor: Mentor, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clickable { onClick() }, // Сделали карточку кликабельной
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Фотография ментора
            if (mentor.photo != null) {
                Image(
                    painter = mentor.photo,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Информация поверх фото (градиент или полупрозрачный фон внизу)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(8.dp)
            ) {
                Text(
                    text = mentor.name,
                    style = MobileTextStyles.SmallestText.copy(fontSize = 13.sp, lineHeight = 15.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = mentor.description,
                    style = MobileTextStyles.SmallestText.copy(fontSize = 10.sp, lineHeight = 11.sp),
                    color = Color.LightGray
                )
            }
        }
    }
}
