package com.example.currencycalculator.viewmodel

import android.content.Context
import com.example.currencycalculator.model.db.Db

object CurrenciesViewModelProvider {
    fun provideCurrenciesViewModelFactory(context : Context): CurrenciesViewModelFactory {
        val db = Db.getInstance(context)!!
        return CurrenciesViewModelFactory(
            db
        )
    }
}