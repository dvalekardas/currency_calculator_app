package com.example.currencycalculator.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencycalculator.databinding.ActivityCurrencyListBinding
import com.example.currencycalculator.model.models.Currency
import com.example.currencycalculator.utils.IntentTypes
import java.util.*

class CurrencyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrencyListBinding
    private var currencyList : List<Currency> = emptyList()
    private var baseCurrency :String? = null
    private var conversionCurrency :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentExtras()
        setCurrenciesAdapter()
        setActionBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(baseCurrency!=null){
            supportActionBar?.title = "Base Currency: $baseCurrency"
        }else{
            supportActionBar?.title = "Conversion Currency: $conversionCurrency"
        }
    }

    private fun setCurrenciesAdapter() {
        val currenciesAdapter  = CurrencyAdapter(this@CurrencyListActivity,arrayListOf(), baseCurrency, conversionCurrency)
        currenciesAdapter.updateCurrencies(currencyList)
        currenciesAdapter.setOnClickListener(object: CurrencyAdapter.OnClickListener{
            override fun onClick(position: Int, model: Currency) {
                val intentReturn = Intent()
                if(baseCurrency != null){
                    intentReturn.putExtra(IntentTypes.BASE_CURRENCY,model.shortName)
                }else if(conversionCurrency !=null){
                    intentReturn.putExtra(IntentTypes.CONVERSION_CURRENCY,model.shortName)
                }
                setResult(Activity.RESULT_OK, intentReturn)
                finish()
            }
        })
        binding.currenciesRecyclerview.adapter = currenciesAdapter
        binding.currenciesRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.searchCurrency.doOnTextChanged { text, _, _, _ ->
            if(text.toString()!=""){
                currenciesAdapter.updateCurrencies(currencyList.filter {
                        currency -> currency.shortName!!.lowercase(Locale.getDefault())
                    .contains(text.toString().lowercase(Locale.getDefault()))
                        || currency.fullName!!.lowercase(Locale.getDefault()).contains(text.toString().lowercase(Locale.getDefault()))})
            }else{
                currenciesAdapter.updateCurrencies(currencyList)
            }
        }
    }

    private fun getIntentExtras(){
        val bundle = intent.extras
        currencyList = bundle!!.getParcelableArrayList(IntentTypes.CURRENCIES)!!
        if(intent.hasExtra(IntentTypes.BASE_CURRENCY)){
            baseCurrency = intent.getStringExtra(IntentTypes.BASE_CURRENCY)
        }else{
            conversionCurrency  = intent.getStringExtra(IntentTypes.CONVERSION_CURRENCY)
        }
    }
}