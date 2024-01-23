package com.stellarbitsapps.androidpdv.ui.finalcash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.ReportErrorDao
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.dao.SangriaDao
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.ReportError
import com.stellarbitsapps.androidpdv.database.entity.ReprintReport
import com.stellarbitsapps.androidpdv.database.entity.Sangria
import kotlinx.coroutines.launch
import java.util.Calendar

class FinalCashViewModel(
    private val reportDao: ReportDao,
    private val sangriaDao: SangriaDao,
    private val reportErrorDao: ReportErrorDao
) : ViewModel() {

    val report = MutableLiveData<ReprintReport>()

    val sangria = MutableLiveData<List<Sangria>>()

    val error = MutableLiveData<List<ReportError>>()

    fun getReport() {
        viewModelScope.launch {
            reportDao.getAll().collect {
                report.postValue(it)
            }
        }
    }

    fun closeCashRegister(finalDate: Long, finalValue: Float) {
        viewModelScope.launch {
            reportDao.updateReportFinalValue(finalDate, finalValue)
        }
    }

    fun reportSangria() {
        viewModelScope.launch {
            sangriaDao.reportSangria()
        }
    }

    fun reportErrors() {
        viewModelScope.launch {
            reportErrorDao.reportErrors()
        }
    }
}

class FinalCashViewModelFactory(
    private val reportDao: ReportDao,
    private val sangriaDao: SangriaDao,
    private val reportErrorDao: ReportErrorDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinalCashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinalCashViewModel(reportDao, sangriaDao, reportErrorDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}