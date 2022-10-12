package com.example.currencycalculator.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true) val id :Int = 0,
    val shortName: String,
    val fullName: String
    )
