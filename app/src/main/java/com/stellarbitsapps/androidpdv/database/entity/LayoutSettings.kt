package com.stellarbitsapps.androidpdv.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LayoutSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "header") val header: String = "",
    @ColumnInfo(name = "footer") val footer: String = "",
    @ColumnInfo(name = "image") val image: String = ""
)
