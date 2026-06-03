package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.R

@Composable
fun MentiHeader(menti: Menti) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Фото
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF666666), RoundedCornerShape(12.dp))
            ) {
                menti.photo?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Имя и Соцсети
            Column {
                Text(
                    text = menti.name,
                    style = MobileTextStyles.QuestionText.copy(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SocialIconSmall(R.drawable.vk_icon)
                    SocialIconSmall(R.drawable.whatsapp_icon)
                    SocialIconSmall(R.drawable.telegram_icon)
                }
            }
        }
    }
}

@Composable
fun SocialIconSmall(resId: Int, onClick: () -> Unit = {}) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .clickable { onClick() }
    )
}

@Composable
fun MentiLessonCard(lesson: MentiLesson) {
    Surface(
        color = Color(0xFFE8E8E8),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "\"${lesson.title}\"",
                style = MobileTextStyles.QuestionText.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = lesson.mentorName,
                style = MobileTextStyles.SmallestText,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LessonBadge(lesson.date, isPast = lesson.isPast)
                    LessonBadge(lesson.time, isPast = lesson.isPast)
                }
                
                Text(
                    text = if (lesson.isPast) "Оценить мероприятие" else "Перейти в профиль ментора",
                    style = MobileTextStyles.SmallestText.copy(
                        fontSize = 10.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

@Composable
fun LessonBadge(text: String, isPast: Boolean) {
    Surface(
        color = if (isPast) Color(0xFF666666) else SkipperColors.mainYellow,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MobileTextStyles.SmallestText.copy(fontWeight = FontWeight.Bold),
            color = if (isPast) Color.White else Color.Black
        )
    }
}

@Composable
fun CurrentLessonsSection(lessons: List<MentiLesson>) {
    LessonSection(title = "Текущие занятия", lessons = lessons, showMore = true)
}

@Composable
fun PastLessonsSection(lessons: List<MentiLesson>) {
    LessonSection(title = "Прошедшие занятия", lessons = lessons)
}

@Composable
private fun LessonSection(title: String, lessons: List<MentiLesson>, showMore: Boolean = false) {
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
                        color = Color.Black,
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }
}
