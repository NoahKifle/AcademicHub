package com.example.academichub.ui.theme

import android.content.Intent
import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.academichub.MainActivity
import com.example.academichub.MainActivity2
import com.example.academichub.R

@Preview
@Composable
private fun TextFieldPreview(){
    TextField()

}
data class GradeItem(
    val name: String,
    val grade: Double,
    val weight: Double
)
@Composable
fun TextField() {
    val context = LocalContext.current

    var assignmentNames = remember {
        mutableStateListOf("","","")
    }
    var grades = remember {
        mutableStateListOf("","","")
    }
    var weights = remember {
        mutableStateListOf("","","")
    }
    val gradeItems= remember { mutableStateListOf<GradeItem>() }
    var finalGrade by remember { mutableStateOf(0.0) }
    var count by remember { mutableStateOf(0) }

    Row(horizontalArrangement = Arrangement.spacedBy(50.dp)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp, top = 10.dp)
        ) {
            items(3 + count) { i ->

                OutlinedTextField(
                    value = assignmentNames[i],
                    onValueChange = { text ->
                        assignmentNames[i] = text
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
            items(3 + count) { i ->

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
            items(3 + count) { i ->

                OutlinedTextField(
                    value = weights[i],
                    onValueChange = { text ->
                        weights[i] = text

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
            if (count < 5) {
                count++
                grades.add("")
                weights.add("")
                assignmentNames.add("")
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
            gradeItems.clear()
            var totalWeighted = 0.0
            var totalWeight = 0.0
            for(i in grades.indices) {
                val g = grades[i].toDoubleOrNull()
                val w = weights[i].toDoubleOrNull()
                val name  = assignmentNames[i]

                if(g != null && w!=null&&name.isNotBlank()) {
                    gradeItems.add(GradeItem(name, g, w))
                    totalWeighted += g * w
                    totalWeight += w
                }
            }
            finalGrade = totalWeighted/ totalWeight


        },colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "calculate grade",color = Color.Green)
        }

        LazyColumn(modifier = Modifier.padding(top = 20.dp)
            .padding(start = 20.dp)) {
            items(gradeItems.size) {i ->
                val item = gradeItems[i]
                Text(
                    text = "${item.name}: Grade ${item.grade}, Weight ${item.weight}"
                )
            }

        }












    }
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Final Grade: ${finalGrade}"

        )
    }
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
        .padding(top=20.dp)){
        Button(onClick={
            val intent = Intent(context, MainActivity2::class.java)
            intent.putExtra("FINAL_GRADE", finalGrade)
            context.startActivity(intent)

        }, colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(text = "final grade calculator",color = Color.Green)
        }
    }



}