package com.example.currencycalculator.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Insert
    suspend fun insertCurrency(currency: CurrencyEntity)

    @Query("SELECT  * FROM 'currency'")
    suspend fun getCurrencies(): List<CurrencyEntity>
}