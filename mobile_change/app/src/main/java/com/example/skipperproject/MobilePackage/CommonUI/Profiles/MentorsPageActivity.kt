package com.example.skipperproject.MobilePackage.CommonUI.Profiles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.TagChip
import com.example.skipperproject.MobilePackage.CommonUI.Tools.*
import com.example.skipperproject.MobilePackage.CommonUI.theme.*
import com.example.skipperproject.R

class MentorsPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                // Тестовые данные
                val mentor = Mentor(
                    name = "Герасимов\nНиколай\nВалерьевич",
                    description = "Кандидат клоунских наук, победитель премии \"Каво\", ещё какая-либо инфа, что-то написать"
                )
                val courses = listOf(
                    Course("Путь к успеху", "Становимся крутыми за пару занятий. Ещё какой-нибудь текст, можно многое что написать. Просто описание.", "10.000 Р"),
                    Course("Путь к успеху", "Становимся крутыми за пару занятий. Ещё какой-нибудь текст, можно многое что написать. Просто описание.", "10.000 Р")
                )
                val reviews = listOf(
                    Review("Шишкин Роман Романович", "Крутой приятный чел. Стал миллионером за пару занятий, реально. Был нищим, а Николай сказал, что это некруто. Испра...", 5),
                    Review("Шишкин Роман Романович", "Крутой приятный чел. Стал миллионером за пару занятий, реально. Был нищим, а Николай сказал, что это некруто. Испра...", 4)
                )

                MentorsPage(mentor, courses, reviews, onBack = { finish() })
            }
        }
    }
}



@Composable
fun MentorsPage(
    mentor: Mentor,
    courses: List<Course>,
    reviews: List<Review>,
    onBack: () -> Unit = {}
) {
    SkipperScreen2(
        onBackClick = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Теги
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip("Бизнес", isSelected = true)
                TagChip("Логистика", isSelected = true)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Основная информация о менторе
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Текстовая карточка
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = mentor.name,
                            style = MobileTextStyles.QuestionText.copy(
                                fontSize = 22.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mentor.description,
                            style = MobileTextStyles.SmallestText.copy(
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            color = Color.Gray
                        )
                    }
                }

                // Фото ментора
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color(0xFF444444), RoundedCornerShape(12.dp))
                ) {
                    mentor.photo?.let {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Иконки соцсетей
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SocialMediaButton(R.drawable.vk_icon)
                SocialMediaButton(R.drawable.whatsapp_icon)
                SocialMediaButton(R.drawable.telegram_icon)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Секция курсов
            Text(
                text = "Курсы ментора",
                style = MobileTextStyles.QuestionText.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(courses) { course ->
                    CourseInfoCard(course)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Секция отзывов
            Text(
                text = "Отзывы от менти",
                style = MobileTextStyles.QuestionText.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(reviews) { review ->
                    ReviewInfoCard(review)
                }
            }
        }
    }
}

@Composable
fun SocialMediaButton(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .size(44.dp)
            .clickable { /* Перейти по ссылке */ }
    )
}

@Composable
fun CourseInfoCard(course: Course) {
    Surface(
        color = SkipperColors.mainYellow,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(260.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "\"${course.title}\"",
                style = MobileTextStyles.QuestionText,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = course.description,
                style = MobileTextStyles.SmallestText.copy(fontSize = 11.sp, lineHeight = 14.sp),
                color = Color.Black,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF444444),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = course.price,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MobileTextStyles.SmallestText.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Surface(
                    color = Color(0xFF444444),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.clickable { /* Выбор времени */ }
                ) {
                    Text(
                        text = "Выбрать время",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MobileTextStyles.SmallestText.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewInfoCard(review: Review) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF444444), CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = review.name,
                    style = MobileTextStyles.SmallestText.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp
                    ),
                    color = Color.Black,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = review.text,
                style = MobileTextStyles.SmallestText.copy(fontSize = 11.sp, lineHeight = 14.sp),
                color = Color.Black,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = SkipperColors.mainYellow
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MentorsPreview(){
    val mentor = Mentor(
        name = "Герасимов\nНиколай\nВалерьевич",
        description = "Кандидат клоунских наук, победитель премии \"Каво\", ещё какая-либо инфа, что-то написать"
    )
    val courses = listOf(
        Course("Путь к успеху", "Становимся крутыми за пару занятий. Ещё какой-нибудь текст, можно многое что написать. Просто описание.", "10.000 Р"),
        Course("Путь к успеху", "Становимся крутыми за пару занятий. Ещё какой-нибудь текст, можно многое что написать. Просто описание.", "10.000 Р")
    )
    val reviews = listOf(
        Review("Шишкин Роман Романович", "Крутой приятный чел. Стал миллионером за пару занятий, реально. Был нищим, а Николай сказал, что это некруто. Испра...", 5),
        Review("Шишкин Роман Романович", "Крутой приятный чел. Стал миллионером за пару занятий, реально. Был нищим, а Николай сказал, что это некруто. Испра...", 4)
    )

    MentorsPage(mentor, courses, reviews, onBack = { })
}
