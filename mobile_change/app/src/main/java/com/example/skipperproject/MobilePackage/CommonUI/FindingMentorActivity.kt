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

@Preview(showBackground = true)
@Composable
fun FindingMentorScreen() {
    var searchQuery by remember { mutableStateOf("") }

    // Тестовые данные для сетки
    val mentors = listOf(
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук,\nпобедитель всех на свете"),
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук,\nпобедитель всех на свете"),
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук,\nпобедитель всех на свете"),
        Mentor("Герасимов\nНиколай\nВалерьевич", "Кандидат самых лучших наук,\nпобедитель всех на свете")
    )

    SkipperScreen2 {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Заголовок: "Приём, Skipper? Нужна помощь"
            Text(
                text = "Приём, Skipper?\nНужна помощь",
                style = MobileTextStyles.MainScreenText.copy(
                    fontSize = 38.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-1.5).sp
                ),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            // "Найди того, кто тебе нужен"
            Text(
                text = "Найди того, кто тебе нужен",
                style = MobileTextStyles.QuestionText.copy(fontSize = 18.sp),
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Поиск
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(36.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                CustomTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // "Популярные #теги"
            Text(
                text = "Популярные #теги",
                style = MobileTextStyles.QuestionText.copy(fontSize = 18.sp),
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Список тегов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TagChip("Бизнес", isSelected = true)
                TagChip("Логистика", isSelected = true)
                TagChip("Веб-дизайн", isSelected = false)
                TagChip("Психология", isSelected = false)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Сетка карточек менторов
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mentors) { mentor ->
                    MentorGridCard(mentor)
                }
            }
        }
    }
}

@Composable
fun TagChip(text: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Surface(
        color = if (isSelected) Color(0xFF444444) else Color(0xFF999999),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MobileTextStyles.SmallestText.copy(fontSize = 14.sp),
            color = Color.White
        )
    }
}

@Composable
fun MentorGridCard(mentor: Mentor, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF555555))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Фон/Фото
            mentor.photo?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Текст снизу
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = mentor.name,
                    style = MobileTextStyles.SmallestText.copy(
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = mentor.description,
                    style = MobileTextStyles.SmallestText.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2
                )
            }
        }
    }
}
