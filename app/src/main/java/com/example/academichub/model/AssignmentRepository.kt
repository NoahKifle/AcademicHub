package com.example.academichub.model

import kotlinx.coroutines.flow.Flow

class AssignmentRepository(private val assignmentDao: AssignmentDao) {
    val allAssignments: Flow<List<AssignmentDetails>> = assignmentDao.getAllAssignments()

    suspend fun insert(assignment: AssignmentDetails) {
        assignmentDao.insertAssignment(assignment)
    }

    suspend fun update(assignment: AssignmentDetails) {
        assignmentDao.updateAssignment(assignment)
    }

    suspend fun delete(assignment: AssignmentDetails) {
        assignmentDao.deleteAssignment(assignment)
    }

    suspend fun deleteById(id: String) {
        assignmentDao.deleteById(id)
    }
}
