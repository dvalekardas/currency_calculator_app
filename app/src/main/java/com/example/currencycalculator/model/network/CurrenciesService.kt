package com.example.currencycalculator.model.network

import com.example.currencycalculator.model.models.ConversionResponse
import com.example.currencycalculator.model.models.CurrenciesResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CurrenciesService {
    private val BASE_URL = "https://api.apilayer.com/fixer/"
    private var currenciesApi: CurrenciesApi = Retrofit.Builder().baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build().create(CurrenciesApi::class.java)

     fun getCurrencies(): Single<CurrenciesResponse> {
        return currenciesApi.getCurrencies()
    }

    fun convertCurrencies(fromCurrency: String, toCurrency: String, amount: Double): Single<ConversionResponse>{
        return currenciesApi.convertCurrencies(fromCurrency, toCurrency, amount)
    }
}