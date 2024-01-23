package com.stellarbitsapps.androidpdv.ui.initialcash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.LayoutSettingsDao
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InitialCashViewModel(
    private val reportDao: ReportDao,
    private val layoutSettingsDao: LayoutSettingsDao
) : ViewModel() {

    val layoutSettings = MutableLiveData<LayoutSettings>()

    fun addReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            reportDao.insertReport(report)
        }
    }

    fun getConfigs() {
        viewModelScope.launch {
            getRowsCount().collect {
                if (it > 0) {
                    layoutSettingsDao.getAll().collect { configs ->
                        layoutSettings.postValue(configs)
                    }
                }
            }
        }
    }

    private fun getRowsCount(): Flow<Int> = layoutSettingsDao.getRowsCount()
}

class InitialCashViewModelFactory(
    private val reportDao: ReportDao,
    private val layoutSettingsDao: LayoutSettingsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InitialCashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InitialCashViewModel(reportDao, layoutSettingsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}