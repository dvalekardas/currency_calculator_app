package com.example.currencycalculator.viewmodel

import android.content.Context
import android.util.Log
import com.example.currencycalculator.model.db.Db
import com.example.currencycalculator.model.network.CurrenciesService

object CurrenciesViewModelProvider {
    fun provideCurrenciesViewModelFactory(context : Context): CurrenciesViewModelFactory {
        val db = Db.getInstance(context)!!
        val currenciesNetworkService = CurrenciesService.getInstance()!!
        return CurrenciesViewModelFactory(
            db,
            currenciesNetworkService
        )
    }
}