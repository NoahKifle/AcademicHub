package com.example.gradecalculator.ui.theme

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gradecalculator.MainActivity2
import com.example.gradecalculator.MainActivity3
import kotlin.jvm.java

@Preview
@Composable
private fun TextField2Preview(){

}

@Composable
fun TextField2(double: Double){
    val context = LocalContext.current
    var desiredGrade by remember { mutableStateOf("") }
    var finalPercent by remember { mutableStateOf("") }
    var gradeNeeded by remember { mutableStateOf(0.0) }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Final Grade: ${double}", fontSize = 30.sp
        )
    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Enter Desired Grade"
        )
    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = desiredGrade,
            onValueChange = { text ->
                desiredGrade = text
                println(text)
            }, modifier = Modifier.size(100.dp, 56.dp)
        )
    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "What is your final worth"

        )
    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = finalPercent,
            onValueChange = { text ->
                finalPercent = text
                println(text)
            }, modifier = Modifier.size(100.dp, 56.dp)
        )
    }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {
            val finalWeight = (finalPercent.toDouble())/100
            gradeNeeded = ((desiredGrade.toDouble())-(double*(1-finalWeight)))/finalWeight

        },colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "calculate grade", color = Color.Green)
        }

    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()){
        Text(text = "grade needed ${gradeNeeded}")
    }
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){
        Button(onClick = {
            val intent = Intent(context, MainActivity3::class.java)
            context.startActivity(intent)
        },colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "gpa calculator",color = Color.Green)
        }
    }


}
