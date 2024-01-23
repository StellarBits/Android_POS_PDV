package com.stellarbitsapps.androidpdv.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tokens(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "value") val value: Float,
    @ColumnInfo(name = "cash_one") val cashOne: Int,
    @ColumnInfo(name = "cash_two") val cashTwo: Int,
    @ColumnInfo(name = "cash_four") val cashFour: Int,
    @ColumnInfo(name = "cash_five") val cashFive: Int,
    @ColumnInfo(name = "cash_six") val cashSix: Int,
    @ColumnInfo(name = "cash_eight") val cashEight: Int,
    @ColumnInfo(name = "cash_ten") val cashTen: Int
)
