package com.stellarbitsapps.androidpdv.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "initial_date") val initialDate: Long? = null,
    @ColumnInfo(name = "initial_cash") val initialCash: Float = 0f,
    @ColumnInfo(name = "final_cash") val finalCash: Float = 0f,
    @ColumnInfo(name = "final_date") val finalDate: Long? = null,
    @ColumnInfo(name = "cash_one_tokens_sold") val cashOneTokensSold: Int = 0,
    @ColumnInfo(name = "cash_two_tokens_sold") val cashTwoTokensSold: Int = 0,
    @ColumnInfo(name = "cash_four_tokens_sold") val cashFourTokensSold: Int = 0,
    @ColumnInfo(name = "cash_five_tokens_sold") val cashFiveTokensSold: Int = 0,
    @ColumnInfo(name = "cash_six_tokens_sold") val cashSixTokensSold: Int = 0,
    @ColumnInfo(name = "cash_eight_tokens_sold") val cashEightTokensSold: Int = 0,
    @ColumnInfo(name = "cash_ten_tokens_sold") val cashTenTokensSold: Int = 0,
    @ColumnInfo(name = "payment_cash") val paymentCash: Float = 0f,
    @ColumnInfo(name = "payment_pix") val paymentPix: Float = 0f,
    @ColumnInfo(name = "payment_debit") val paymentDebit: Float = 0f,
    @ColumnInfo(name = "payment_credit") val paymentCredit: Float = 0f,
)