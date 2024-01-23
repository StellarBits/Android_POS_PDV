package com.stellarbitsapps.androidpdv.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class Sangria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "sangria") val sangria: Float = 0f,
    @ColumnInfo(name = "date") val date: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo(name = "reported") val reported: Int = 0,
    @ColumnInfo(name = "report_id") val reportId: Int? = null
)
