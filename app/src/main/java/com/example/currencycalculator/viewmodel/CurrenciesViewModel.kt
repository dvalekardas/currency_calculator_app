package com.example.currencycalculator.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencycalculator.model.db.CurrencyEntity
import com.example.currencycalculator.model.db.DataDao
import com.example.currencycalculator.model.models.ConversionResponse
import com.example.currencycalculator.model.models.CurrenciesResponse
import com.example.currencycalculator.model.network.CurrenciesService
import com.example.currencycalculator.model.models.Currency
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrenciesViewModel(private val currenciesDatabaseService: DataDao) : ViewModel(){
    private val currenciesNetworkService = CurrenciesService()
    val currencies = MutableLiveData<MutableList<Currency>>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val baseCurrency = MutableLiveData<String>()
    val conversionCurrency = MutableLiveData<String>()
    val conversionAmount = MutableLiveData<Double>()
    val inputAmount = MutableLiveData<Double>()
    val inputNumericExpression = MutableLiveData<String>()
    init{
        error.value = false
        loading.value = false
        currencies.value = mutableListOf()
        baseCurrency.value = "EUR"
        conversionCurrency.value = "USD"
    }

    fun getCurrencies(applicationContext: Context){
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val currenciesFromDb = currenciesDatabaseService.getCurrencies()
                if(currenciesFromDb.isNotEmpty()){
                    val helperList = mutableListOf<Currency>()
                    currenciesFromDb.forEach {
                            currencyDetails -> helperList.add(Currency(currencyDetails.shortName, currencyDetails.fullName))
                    }
                    loading.postValue(false)
                    currencies.postValue(helperList)

                }else{
                    currenciesNetworkService.getCurrencies()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object:DisposableSingleObserver<CurrenciesResponse>(){
                            override fun onSuccess(response: CurrenciesResponse) {
                                response.currenciesDetails.forEach{
                                        currencyDetails ->
                                    currencies.value?.add(Currency(currencyDetails.key, currencyDetails.value))
                                    viewModelScope.launch {
                                        currenciesDatabaseService.insertCurrency(CurrencyEntity(shortName = currencyDetails.key, fullName = currencyDetails.value ))
                                    }
                                }
                                loading.value = false
                            }

                            override fun onError(e: Throwable) {
                                loading.value = false
                                error.value = true
                                Toast.makeText(applicationContext,"Could not fetch currencies. Swipe to fetch them",
                                    Toast.LENGTH_SHORT).show();
                            }
                        })
                }
        }
    }

    fun convertCurrencies(amount: Double){
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            baseCurrency.value?.let { baseCurrency ->
                conversionCurrency.value?.let { conversionCurrency ->
                    currenciesNetworkService.convertCurrencies(baseCurrency, conversionCurrency, amount)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object:DisposableSingleObserver<ConversionResponse>(){
                            override fun onSuccess(response: ConversionResponse) {
                                if(response.conversionAmount!=null){
                                    conversionAmount.value = response.conversionAmount.toDouble()
                                }
                                loading.value = false
                            }

                            override fun onError(e: Throwable) {
                                loading.value = false
                                error.value = true
                            }
                        })
                }
            }
        }
    }

}