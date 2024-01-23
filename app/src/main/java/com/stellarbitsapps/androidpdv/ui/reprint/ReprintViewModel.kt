package com.stellarbitsapps.androidpdv.ui.reprint

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.dao.ReprintReportDao
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.ReprintReport
import kotlinx.coroutines.launch

class ReprintViewModel(
    private val reprintReportDao: ReprintReportDao,
    private val reportDao: ReportDao
) : ViewModel() {
    val reprintReport = MutableLiveData<ReprintReport>()

    val report = MutableLiveData<List<Report>>()

    fun getReport(reportId: Int) {
        viewModelScope.launch {
            reprintReportDao.getReport(reportId).collect {
                reprintReport.postValue(it)
            }
        }
    }

    fun getAllReports() {
        viewModelScope.launch {
            reportDao.getAllReports().collect {
                report.postValue(it)
            }
        }
    }
}

class ReprintViewModelFactory(
    private val reprintReportDao: ReprintReportDao,
    private val reportDao: ReportDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReprintViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReprintViewModel(reprintReportDao, reportDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}