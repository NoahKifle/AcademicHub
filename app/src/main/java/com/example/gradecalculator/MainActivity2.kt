package com.example.gradecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gradecalculator.ui.theme.GradeCalculatorTheme
import com.example.gradecalculator.ui.theme.Header
import com.example.gradecalculator.ui.theme.Header2
import com.example.gradecalculator.ui.theme.TextField
import com.example.gradecalculator.ui.theme.TextField2

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val finalGrade = intent.getDoubleExtra("FINAL_GRADE", 0.0)
        enableEdgeToEdge()
        setContent{
            GradeCalculatorTheme() {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()

                ){
                    Header2()

                    TextField2(finalGrade)




                }
            }
        }
    }
}



