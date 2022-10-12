package com.example.currencycalculator.model.network

import com.example.currencycalculator.model.models.ConversionResponse
import com.example.currencycalculator.model.models.CurrenciesResponse
import retrofit2.http.GET
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Headers
import retrofit2.http.Query

const val API_KEY = "gAEI6wse9aXXsWk3PN3f6tGOwRLnaxtu"

interface CurrenciesApi {

    @Headers("apikey: $API_KEY")
    @GET("symbols")
    fun getCurrencies(): Single<CurrenciesResponse>

    @Headers("apikey: $API_KEY")
    @GET("convert")
    fun convertCurrencies(@Query("from") fromCurrency: String, @Query("to") toCurrency: String, @Query("amount") amount: Double):Single<ConversionResponse>
}