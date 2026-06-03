package com.example.skipperproject.MobilePackage.CommonUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.Tools.Notification
import com.example.skipperproject.MobilePackage.CommonUI.Tools.SkipperScreen2
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperProjectTheme

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                val notifications = listOf(
                    Notification("Тема уведомления", "Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления.", "22:24"),
                    Notification("Тема уведомления", "Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления.", "22:24"),
                    Notification("Тема уведомления", "Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления.", "22:24"),
                    Notification("Тема уведомления", "Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления.", "22:24"),
                    Notification("Тема уведомления", "Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления. Текст уведомления.", "22:24")
                )
                NotificationScreen(notifications)
            }
        }
    }
}

@Composable
fun NotificationScreen(notifications: List<Notification>) {
    SkipperScreen2 {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Уведомления",
                style = MobileTextStyles.MainScreenText.copy(
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification) {
    Surface(
        color = Color(0xFFBEBEBE), // Цвет darkGrey из вашей палитры
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = notification.title,
                style = MobileTextStyles.QuestionText.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = notification.text,
                style = MobileTextStyles.SmallestText.copy(
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.Black.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.time,
                    style = MobileTextStyles.SmallestText.copy(fontSize = 12.sp),
                    color = Color.Black.copy(alpha = 0.6f)
                )
                
                IconButton(
                    onClick = { /* Удаление уведомления */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreview() {
    SkipperProjectTheme {
        val testData = listOf(
            Notification("Тема уведомления", "Пример текста уведомления для превью экрана.", "12:00")
        )
        NotificationScreen(testData)
    }
}
