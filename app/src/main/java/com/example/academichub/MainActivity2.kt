package com.example.academichub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.academichub.ui.theme.AcademicHubTheme
import com.example.academichub.ui.theme.Header2
import com.example.academichub.ui.theme.TextField2

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val finalGrade = intent.getDoubleExtra("FINAL_GRADE", 0.0)
        enableEdgeToEdge()
        setContent{
            AcademicHubTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Header2()
                        TextField2(finalGrade)
                    }
                }
            }
        }
    }
}
