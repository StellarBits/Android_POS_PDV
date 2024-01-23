package com.stellarbitsapps.androidpdv.ui.registertokens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stellarbitsapps.androidpdv.database.dao.TokensDao
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterTokenViewModel(private val tokensDao: TokensDao) : ViewModel() {
    fun setToken(token: Tokens) {
        viewModelScope.launch(Dispatchers.IO) {
            tokensDao.insertAll(token)
        }
    }

    fun deleteTokens() {
        viewModelScope.launch(Dispatchers.IO) {
            tokensDao.deleteTokens()
        }
    }
}

class RegisterTokenViewModelFactory(
    private val tokensDao: TokensDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterTokenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterTokenViewModel(tokensDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}