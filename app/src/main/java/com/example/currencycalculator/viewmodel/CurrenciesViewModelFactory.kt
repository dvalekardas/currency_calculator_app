package com.example.currencycalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencycalculator.model.db.Db

class CurrenciesViewModelFactory(private val db: Db): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrenciesViewModel(
            db.DataDao()
        ) as T
    }
}