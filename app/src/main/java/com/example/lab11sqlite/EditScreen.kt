package com.example.lab11sqlite

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun EditScreen(navController: NavHostController) {
    val data =
        navController.previousBackStackEntry?.savedStateHandle?.get<Student>("data")
            ?: Student("", "", "",0  )

    ///ให้นศ.เขียนคำสั่งเอง
    var textFieldID by remember { mutableStateOf(data.std_id) }
    val contextForToast = LocalContext.current
    var genderValue by remember { mutableStateOf(data.std_gender) }

    ////ให้นศ.เพิ่ม
    var textFieldName by remember { mutableStateOf(data.std_name) }
    var textFieldAge by remember { mutableStateOf(data.std_age.toString()) }

    // For Alert Dialog: Confirm to delete Student
    var deleteDialog by remember { mutableStateOf(false) }

    val dbHandler= DatabaseHelper.getInstance(contextForToast)
    dbHandler.writableDatabase

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Edit a student",
            fontSize = 25.sp
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldID,
            onValueChange = { textFieldID = it },
            label = { Text("Student ID") },
            enabled = false,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldName,
            onValueChange = { textFieldName = it },
            label = { Text("Student name") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        genderValue  =  EditRadioGroupUsage(genderValue)

        val mMaxLength = 2
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldAge,
            onValueChange = { if (it.length <= mMaxLength) textFieldAge = it
            else Toast.makeText(contextForToast,
                "This text field cannot accept more than two digits.",
                Toast.LENGTH_SHORT).show()  },
            label = { Text("Student age") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),
            )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Button(modifier = Modifier
                .width(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = {
                    // ให้นักศึกษาเพิ่มคำสั่งสร้าง Dialog
                    deleteDialog = true
                }) {
                Text("Delete")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(modifier = Modifier
                .width(100.dp),
                onClick = {

                    //      textFieldID,textFieldName , genderValue,textFieldAge.toInt()
                    val result = dbHandler.updateStudent(
                        Student(textFieldID,textFieldName , genderValue,textFieldAge.toInt()))

                    if (result > -1) {
                        Toast.makeText(contextForToast, "Updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(contextForToast, "Update Failure", Toast.LENGTH_LONG).show()  }
                    navController.navigate(Screen.Home.route)
                }) {
                Text("Update")
            }
            Spacer(modifier = Modifier.width(10.dp))

            Button(modifier = Modifier
                .width(100.dp),
                onClick = {
                    navController.navigate(Screen.Home.route)
                }) {
                Text("Cancel")
            }
        }


        ///////

        if (deleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    deleteDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            deleteDialog = false

                            val result = dbHandler.deleteStudent(textFieldID)
                            if (result > -1) {
                                Toast.makeText(contextForToast, "Deleted successfully",
                                    Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(contextForToast, "Delete Failure",
                                    Toast.LENGTH_LONG).show()
                            }

                            Toast.makeText(contextForToast,
                                " $textFieldID is deleted",
                                Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Home.route)
                        }
                    ) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            deleteDialog = false
                            Toast.makeText(contextForToast,
                                "Click on No", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(text = "No")
                    }
                },
                title = { Text(text = "Warning") },
                text = { Text(text = "Do you want to delete a student: ${textFieldID}?") },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }/// End delete

    }
}

@Composable
fun EditRadioGroupUsage(s:String):String {
    val kinds = listOf("Male", "Female", "Other")
    val (selected, setSelected) = remember { mutableStateOf(s) }
    Text(
        text = "Student Gender :",
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, top = 10.dp),
    )
    Row (modifier = Modifier.fillMaxWidth()
        .padding(start = 16.dp)){
        EditeRadioGroup(
            mItems = kinds,
            selected, setSelected
        )
    }
    return selected
}

@Composable
fun EditeRadioGroup(
    mItems: List<String>,
    selected: String,
    setSelected: (selected: String) -> Unit,
) {
    Row( modifier = Modifier.fillMaxWidth(),
    ) {
        mItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == item,
                    onClick = {
                        setSelected(item)
                    },
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green )
                )
                Text(text = item, modifier = Modifier.padding(start = 5.dp))
            }
        }
    }
}