package com.example.currencycalculator.model.models

import com.google.gson.annotations.SerializedName

data class ConversionResponse(
    @SerializedName("result")
    val conversionAmount : String,

    @SerializedName("success")
    val success : Boolean
)
