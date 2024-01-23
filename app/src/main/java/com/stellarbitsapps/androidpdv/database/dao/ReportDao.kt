package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.ReprintReport
import kotlinx.coroutines.flow.Flow

const val WHERE_CLAUSE = "((strftime('%Y-%m-%d', datetime(initial_date / 1000, 'unixepoch', 'localtime')) = strftime('%Y-%m-%d', 'now', 'localtime')) and final_date is NULL)"
@Dao
interface ReportDao {
    @Transaction
    @Query("SELECT r.* FROM report r " +
            "LEFT JOIN Sangria s ON r.id = s.report_id " +
            "LEFT JOIN ReportError e ON r.id = e.report_id " +
            "WHERE $WHERE_CLAUSE")
    fun getAll(): Flow<ReprintReport>

    @Query("SELECT * FROM report ORDER BY id")
    fun getAllReports(): Flow<List<Report>>

    @Query("SELECT id FROM report ORDER BY id DESC LIMIT 1")
    fun getLastReportId(): Flow<Int>

    @Query("SELECT $WHERE_CLAUSE FROM report ORDER BY id DESC LIMIT 1")
    fun cashRegisterIsOpen(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReport(vararg data: Report)

    @Query("UPDATE report SET " +
            "cash_one_tokens_sold = cash_one_tokens_sold + :cashOne," +
            "cash_two_tokens_sold = cash_two_tokens_sold + :cashTwo," +
            "cash_four_tokens_sold = cash_four_tokens_sold + :cashFour," +
            "cash_five_tokens_sold = cash_five_tokens_sold + :cashFive," +
            "cash_six_tokens_sold = cash_six_tokens_sold + :cashSix," +
            "cash_eight_tokens_sold = cash_eight_tokens_sold + :cashEight," +
            "cash_ten_tokens_sold = cash_ten_tokens_sold + :cashTen," +
            "payment_cash = payment_cash + :paymentCash," +
            "payment_pix = payment_pix + :paymentPix," +
            "payment_debit = payment_debit + :paymentDebit," +
            "payment_credit = payment_credit + :paymentCredit" +
            " WHERE $WHERE_CLAUSE")
    suspend fun updateReportTokens(
        cashOne: Int,
        cashTwo: Int,
        cashFour: Int,
        cashFive: Int,
        cashSix: Int,
        cashEight: Int,
        cashTen: Int,
        paymentCash: Float,
        paymentPix: Float,
        paymentDebit: Float,
        paymentCredit: Float,
    )

    @Query("UPDATE report SET final_cash = :finalCash, final_date = :finalDate WHERE $WHERE_CLAUSE")
    suspend fun updateReportFinalValue(finalDate: Long, finalCash: Float)

    @Query("DELETE FROM report WHERE $WHERE_CLAUSE")
    suspend fun deleteReport()
}