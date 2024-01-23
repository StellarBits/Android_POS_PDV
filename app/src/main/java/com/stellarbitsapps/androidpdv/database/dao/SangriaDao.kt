package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.Sangria
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.flow.Flow

@Dao
interface SangriaDao {
    @Query("SELECT * FROM sangria WHERE reported == 0")
    fun getAll(): Flow<List<Sangria>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg sangria: Sangria)

    @Query("UPDATE sangria SET reported = 1")
    suspend fun reportSangria()
}