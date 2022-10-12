package com.example.currencycalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencycalculator.model.db.Db
import com.example.currencycalculator.model.network.CurrenciesService

class CurrenciesViewModelFactory(private val db: Db, private val currenciesNetworkService: CurrenciesService): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrenciesViewModel(
            db.DataDao(),
            currenciesNetworkService
        ) as T
    }
}