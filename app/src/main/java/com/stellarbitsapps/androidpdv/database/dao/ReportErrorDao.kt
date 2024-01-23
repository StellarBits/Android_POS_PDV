package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.ReportError
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportErrorDao {
    @Query("SELECT * FROM reportError WHERE reported == 0")
    fun getAll(): Flow<List<ReportError>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg error: ReportError)

    @Query("UPDATE reportError SET reported = 1")
    suspend fun reportErrors()
}