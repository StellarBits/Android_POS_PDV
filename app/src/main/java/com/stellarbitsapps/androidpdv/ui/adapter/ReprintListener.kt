package com.stellarbitsapps.androidpdv.ui.adapter

import com.stellarbitsapps.androidpdv.database.entity.Report

class ReprintListener(
    val clickListener: (report: Report) -> Unit,
) {
    fun onClick(report: Report) {
        clickListener(report)
    }
}