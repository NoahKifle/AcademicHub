package com.example.academichub

import android.app.Application
import com.example.academichub.model.AppDatabase
import com.example.academichub.model.AssignmentRepository

class AcademicHubApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AssignmentRepository(database.assignmentDao()) }
}
