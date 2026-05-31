package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperDimensions

/**
 * Ячейка календаря
 * @param day - Текст внутри ячейки (число)
 * @param isSelected - Флаг для выделения (желтый фон)
 * @param isDark - Флаг для темного фона (прошедшие/занятые дни)
 * @param onClick - Действие при нажатии
 */
@Composable
fun CalendarDayCell(
    day: String,
    isSelected: Boolean = false,
    isDark: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color = when {
                    isSelected -> SkipperColors.mainYellow
                    isDark -> Color(0xFF444444)
                    else -> SkipperColors.darkGrey.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = MobileTextStyles.SmallestText,
            color = if (isSelected) Color.Black else if (isDark) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Желтая карточка курса
 * @param title - Название курса
 * @param description - Описание курса
 * @param price - Цена или доп. информация в темном теге
 */
@Composable
fun CourseYellowCard(
    title: String,
    description: String,
    price: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SkipperColors.mainYellow, RoundedCornerShape(SkipperDimensions.bubbleRound))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = "\"$title\"",
            style = MobileTextStyles.QuestionText,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MobileTextStyles.SmallestText.copy(lineHeight = 16.sp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = Color(0xFF333333),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = price,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = Color.White,
                style = MobileTextStyles.SmallestText
            )
        }
    }
}

/**
 * Карточка ближайшего занятия
 * @param courseTitle - Название курса
 * @param mentorName - Имя преподавателя
 * @param date - Дата (опционально)
 * @param time - Время (опционально)
 * @param onActionClick - Действие для ссылки (например, переход в профиль)
 */
@Composable
fun LessonInfoCard(
    courseTitle: String,
    mentorName: String,
    date: String? = null,
    time: String? = null,
    onActionClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2), RoundedCornerShape(SkipperDimensions.bubbleRound))
            .padding(12.dp)
    ) {
        Text(
            text = "\"$courseTitle\"",
            style = MobileTextStyles.QuestionText,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = mentorName,
            style = MobileTextStyles.SmallestText,
            color = Color.DarkGray
        )
        
        if (date != null && time != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    YellowBadge(date)
                    Spacer(modifier = Modifier.width(8.dp))
                    YellowBadge(time)
                }
                Text(
                    text = "Перейти в профиль менти",
                    style = MobileTextStyles.HelpButtonSmallUnderlined.copy(fontSize = 10.sp),
                    modifier = Modifier.clickable { onActionClick() }
                )
            }
        }
    }
}

@Composable
private fun YellowBadge(text: String) {
    Surface(
        color = SkipperColors.mainYellow,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
            style = MobileTextStyles.SmallestText,
            fontWeight = FontWeight.Bold
        )
    }
}
