package com.example.skipperproject.MobilePackage.CommonUI.Tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                MainFun()
            }
        }
    }
}

@Composable
fun MainFun(){
    TextButtonTest()
}

@Composable
fun TextButtonTest(){
    TextButton(onClick = {}, modifier = Modifier.fillMaxSize()) {
        Text(text = "Напиши что-нибудь")
    }
}


@Composable
@Preview(showSystemUi = true)
fun Preview(){
    MainFun()
}