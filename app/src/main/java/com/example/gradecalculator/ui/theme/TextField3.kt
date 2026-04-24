package com.example.gradecalculator.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TextField3(){

    var courseNames = remember {
        mutableStateListOf("")
    }
    var grades = remember {
        mutableStateListOf("")
    }
    var credits = remember {
        mutableStateListOf("")
    }
    var gpa by remember { mutableStateOf(0.0) }

    Row(horizontalArrangement = Arrangement.spacedBy(50.dp)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp, top = 10.dp)
        ) {
            items(courseNames.size) { i ->

                OutlinedTextField(
                    value = courseNames[i],
                    onValueChange = { text ->
                        courseNames[i] = text
                        println(text)
                    },
                    modifier = Modifier
                        .size(100.dp, 56.dp)

                    ,


                    )
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp, top = 10.dp)
        ) {
            items(courseNames.size) { i ->

                OutlinedTextField(
                    value = grades[i],
                    onValueChange = { text ->
                        grades[i] = text

                    },
                    modifier = Modifier
                        .size(70.dp, 56.dp)

                    ,


                    )
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp, top = 10.dp)

        ) {
            items(courseNames.size) { i ->

                OutlinedTextField(
                    value = credits[i],
                    onValueChange = { text ->
                        credits[i] = text

                    },
                    modifier = Modifier
                        .size(70.dp, 56.dp)

                    ,


                    )
            }
        }


    }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
        .padding(top = 20.dp)) {
        Button(onClick ={
            if (courseNames.size < 5) {

                grades.add("")
                credits.add("")
                courseNames.add("")
            }else {
                println("too many rows")
            }
        }, colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "add more rows" , color = Color.Green)
        }
    }

    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()
        .padding(top = 20.dp)) {
        Button(onClick ={


            var totalcredits = 0.0
            var totalgradepoints = 0.0
            for(i in grades.indices) {
                val g = grades[i]
                val c = credits[i].toDoubleOrNull() ?:0.0


                if (g == "A" || g == "a"){
                    totalgradepoints += (4 * c)
                }else if(g == "B" || g == "b"){
                    totalgradepoints += (3 *c)
                }else if(g == "C" || g == "c"){
                    totalgradepoints += (2*c)
                }else if(g == "D" || g == "d"){
                    totalgradepoints += (1*c)
                }else if(g == "F" || g == "f"){
                    totalgradepoints += (0*c)
                }
                totalcredits += c






            }
            gpa = (totalgradepoints/totalcredits)




        },colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "calculate grade",color = Color.Green)
        }

        LazyColumn(modifier = Modifier.padding(top = 20.dp)
            .padding(start = 20.dp)) {
            items(grades.size) {i ->

                Text(
                    text = "${courseNames[i]}: Grade ${grades[i]}, Credits ${credits[i]}"
                )
            }

        }












    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()){
        Text(
            text = "GPA: ${gpa}"

        )
    }
}