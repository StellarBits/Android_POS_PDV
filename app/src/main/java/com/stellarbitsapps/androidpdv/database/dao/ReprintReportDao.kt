package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.stellarbitsapps.androidpdv.database.entity.ReprintReport
import kotlinx.coroutines.flow.Flow

@Dao
interface ReprintReportDao {
    @Transaction
    @Query("SELECT r.* FROM report r " +
            "LEFT JOIN Sangria s ON r.id = s.report_id " +
            "LEFT JOIN ReportError e ON r.id = e.report_id " +
            "WHERE r.id = :reportId")
    fun getReport(reportId: Int): Flow<ReprintReport>
}