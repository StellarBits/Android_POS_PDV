package com.stellarbitsapps.androidpdv.ui.tokens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.LayoutSettingsDao
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.dao.ReportErrorDao
import com.stellarbitsapps.androidpdv.database.dao.SangriaDao
import com.stellarbitsapps.androidpdv.database.dao.TokensDao
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.ReportError
import com.stellarbitsapps.androidpdv.database.entity.Sangria
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TokensViewModel(
    private val tokensDao: TokensDao,
    private val reportDao: ReportDao,
    private val layoutSettingsDao: LayoutSettingsDao,
    private val sangriaDao: SangriaDao,
    private val reportErrorDao: ReportErrorDao
) : ViewModel() {

    val tokensList = MutableLiveData<List<Tokens>>()

    val layoutSettings = MutableLiveData<LayoutSettings>()

    fun getTokens() {
        viewModelScope.launch {
            tokensDao.getAll().collect {
                tokensList.postValue(it)
            }
        }
    }

    fun updateReportTokens(report: Report) {
        viewModelScope.launch {
            reportDao.updateReportTokens(
                report.cashOneTokensSold,
                report.cashTwoTokensSold,
                report.cashFourTokensSold,
                report.cashFiveTokensSold,
                report.cashSixTokensSold,
                report.cashEightTokensSold,
                report.cashTenTokensSold,
                report.paymentCash,
                report.paymentPix,
                report.paymentDebit,
                report.paymentCredit
            )
        }
    }

    fun deleteReport() {
        viewModelScope.launch {
            reportDao.deleteReport()
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

    fun insertSangria(sangria: Float, reportId: Int) {
        viewModelScope.launch {
            sangriaDao.insertAll(Sangria(sangria = sangria, reportId = reportId))
        }
    }

    fun insertError(error: Float, reportId: Int) {
        viewModelScope.launch {
            reportErrorDao.insertAll(ReportError(error = error, reportId = reportId))
        }
    }
}

class TokensViewModelFactory(
    private val tokensDao: TokensDao,
    private val reportDao: ReportDao,
    private val layoutSettingsDao: LayoutSettingsDao,
    private val sangriaDao: SangriaDao,
    private val reportErrorDao: ReportErrorDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TokensViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TokensViewModel(tokensDao, reportDao, layoutSettingsDao, sangriaDao, reportErrorDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}