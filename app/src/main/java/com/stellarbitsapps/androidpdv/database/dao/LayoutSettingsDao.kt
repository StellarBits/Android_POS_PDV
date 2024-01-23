package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface LayoutSettingsDao {
    @Query("SELECT * FROM LayoutSettings")
    fun getAll(): Flow<LayoutSettings>

    @Query("SELECT COUNT(*) FROM LayoutSettings")
    fun getRowsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg layoutSettings: LayoutSettings)

    @Query("UPDATE LayoutSettings SET " +
            "header = :header," +
            "footer = :footer," +
            "image = :image")
    suspend fun updateConfigs(
        header: String,
        footer: String,
        image: String
    )
}