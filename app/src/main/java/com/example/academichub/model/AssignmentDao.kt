package com.example.academichub.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments")
    fun getAllAssignments(): Flow<List<AssignmentDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentDetails)

    @Update
    suspend fun updateAssignment(assignment: AssignmentDetails)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentDetails)

    @Query("DELETE FROM assignments WHERE id = :assignmentId")
    suspend fun deleteById(assignmentId: String)
}
