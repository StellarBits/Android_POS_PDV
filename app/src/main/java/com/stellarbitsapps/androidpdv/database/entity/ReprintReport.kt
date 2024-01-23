package com.stellarbitsapps.androidpdv.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class ReprintReport(
    @Embedded val report: Report,

    @Relation(
        parentColumn = "id",
        entityColumn = "report_id"
    ) val sangria: List<Sangria>,

    @Relation(
        parentColumn = "id",
        entityColumn = "report_id"
    ) val error: List<ReportError>,
)
