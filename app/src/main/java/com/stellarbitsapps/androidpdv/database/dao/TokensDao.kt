package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.flow.Flow

@Dao
interface TokensDao {
    @Query("SELECT * FROM tokens ORDER BY value ASC")
    fun getAll(): Flow<List<Tokens>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg tokens: Tokens)

    @Query("DELETE FROM tokens")
    suspend fun deleteTokens()
}