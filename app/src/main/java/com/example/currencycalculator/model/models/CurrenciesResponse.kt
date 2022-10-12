package com.example.currencycalculator.model.models

import com.google.gson.annotations.SerializedName

data class CurrenciesResponse(
    @SerializedName("success")
    val result : Boolean,

    @SerializedName("symbols")
    val currenciesDetails : Map<String, String>
)
