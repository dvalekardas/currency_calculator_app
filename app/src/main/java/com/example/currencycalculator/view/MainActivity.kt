package com.example.currencycalculator.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.currencycalculator.databinding.ActivityMainBinding
import com.example.currencycalculator.model.models.Currency
import com.example.currencycalculator.utils.IntentTypes
import com.example.currencycalculator.viewmodel.CurrenciesViewModel
import com.example.currencycalculator.viewmodel.CurrenciesViewModelProvider
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var currencies: MutableList<Currency> = mutableListOf()
    private var lastNumeric: Boolean = false
    private var lastDot: Boolean = false
    private var lastDivider: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        setOnClickListeners()
        binding.swipeRefresh.setOnRefreshListener{
            if(currencies.isEmpty()){
                currenciesViewModel.getCurrencies(applicationContext)
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                if(data!!.hasExtra(IntentTypes.BASE_CURRENCY)){
                    currenciesViewModel.baseCurrency.value = data.getStringExtra(IntentTypes.BASE_CURRENCY)
                }else if(data.hasExtra(IntentTypes.CONVERSION_CURRENCY)){
                    currenciesViewModel.conversionCurrency.value = data.getStringExtra(IntentTypes.CONVERSION_CURRENCY)
                }
            }
        }
    }

    private fun setViewModel(){
        val factory = CurrenciesViewModelProvider.provideCurrenciesViewModelFactory(this)
        currenciesViewModel = ViewModelProvider(this, factory)[CurrenciesViewModel::class.java]
        if(currencies.isEmpty()){
            currenciesViewModel.getCurrencies(applicationContext)
        }
        currenciesViewModel.currencies.observe(this,{
            currencies = it
        })

        currenciesViewModel.baseCurrency.observe(this,{
            binding.baseCurrencyText.text = it
        })
        currenciesViewModel.conversionCurrency.observe(this,{
            binding.conversionCurrencyText.text = it
        })
        currenciesViewModel.conversionAmount.observe(this,{
            if(it == 0.0){
                binding.outputText.text = ""

            }else{
                binding.outputText.text = it.toString()
            }
        })
        currenciesViewModel.inputNumericExpression.observe(this,{
            binding.inputText.text = it.toString()
        })

    }

    private fun setOnClickListeners(){
        binding.baseCurrencyText.setOnClickListener {
            if(currencies.isNotEmpty()){
                val intent = Intent(this,CurrencyListActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList(IntentTypes.CURRENCIES,currencies as ArrayList<Currency>)
                intent.putExtras(bundle)
                intent.putExtra(IntentTypes.BASE_CURRENCY,binding.baseCurrencyText.text)
                startActivityForResult(intent, 1)
            }

        }
        binding.conversionCurrencyText.setOnClickListener {
            if(currencies.isNotEmpty()) {
                val intent = Intent(this, CurrencyListActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    IntentTypes.CURRENCIES,
                    currencies as ArrayList<Currency>
                )
                intent.putExtras(bundle)
                intent.putExtra(
                    IntentTypes.CONVERSION_CURRENCY,
                    binding.conversionCurrencyText.text
                )
                startActivityForResult(intent, 1)
            }
        }
        binding.buttonConvert.setOnClickListener {
            convertCurrencies()
        }
        binding.buttonSwap.setOnClickListener {
            swapCurrencies()
        }

        binding.backspace.setOnLongClickListener{
            clear()
            true
        }
    }

    private fun swapCurrencies(){
        val temp : String? = currenciesViewModel.baseCurrency.value
        currenciesViewModel.baseCurrency.value = currenciesViewModel.conversionCurrency.value
        currenciesViewModel.conversionCurrency.value = temp
        convertCurrencies()
    }

    private fun convertCurrencies(){
        if(binding.inputText.text.toString() != ""){
            val numericExpression = binding.inputText.text.toString()
            val inputAmount: Double
            if(numericExpression.contains("-")
                || numericExpression.contains("+")
                || numericExpression.contains("*")
                || numericExpression.contains("/")
            ){
                inputAmount = calculateExpression(numericExpression)
                currenciesViewModel.inputAmount.value = inputAmount
                currenciesViewModel.inputNumericExpression.value = inputAmount.toString()
            }else{
                inputAmount = numericExpression.toDouble()
            }
            currenciesViewModel.convertCurrencies(inputAmount)
            binding.inputText.text = inputAmount.toString()
        }
    }

    private fun calculateExpression(numericExpression: String): Double {
        if(numericExpression.endsWith("-")
            ||numericExpression.endsWith("+")
            ||numericExpression.endsWith("/")
            ||numericExpression.endsWith("*")){
            val numericExpressionNew: String = numericExpression.dropLast(1)
            return ExpressionBuilder(numericExpressionNew).build().evaluate()
        }
        return ExpressionBuilder(numericExpression).build().evaluate()
    }

    fun onDigit(view: View){
        val digit = (view as Button).text
        if(digit!="0" || (digit == "0" && !lastDivider)){
            binding.inputText.append(digit)
            currenciesViewModel.inputNumericExpression.value = binding.inputText.text.toString()
            lastNumeric = true
            lastDot = false
            lastDivider = false
        }
    }

    fun onClear(view: View){
      clear()
    }

    private fun clear(){
        currenciesViewModel.inputAmount.value = 0.0
        currenciesViewModel.inputNumericExpression.value = ""
        currenciesViewModel.conversionAmount.value = 0.0
        binding.outputText.text = ""
        binding.inputText.text = ""
        lastNumeric = false
        lastDot = false
        lastDivider = false
    }

    fun onDecimalPoint(view: View){
        if( binding.inputText.text?.contains(".") == false && lastNumeric && !lastDot){
            binding.inputText.append(".")
            lastNumeric = false
            lastDot = true
            lastDivider = false
            currenciesViewModel.inputNumericExpression.value = binding.inputText.text.toString()
        }
    }

    fun onOperator(view: View){
        binding.inputText.text?.let {
            if(lastNumeric && !isOperatorAdded(it.toString())){
                val operator = (view as Button).text
                binding.inputText.append(operator)
                lastNumeric = false
                lastDot = false
                if(operator == "/"){
                    lastDivider = true
                }
                currenciesViewModel.inputNumericExpression.value = binding.inputText.text.toString()
            }
        }

    }

    fun onEqual(view: View){
        if(lastNumeric){
            currenciesViewModel.inputAmount.value = calculateExpression(binding.inputText.text.toString())
            currenciesViewModel.inputNumericExpression.value = currenciesViewModel.inputAmount.value.toString()
        }
    }

    fun onBackspace(view: View){
        binding.inputText.text = binding.inputText.text.toString().dropLast(1)
        currenciesViewModel.inputNumericExpression.value = binding.inputText.text.toString()
        if(binding.inputText.text.toString().endsWith("/")){
            lastDivider = true
            lastNumeric = false
            lastDot = false
        }else if(binding.inputText.text.toString().endsWith("-")|| binding.inputText.text.toString().endsWith("+") || binding.inputText.text.toString().endsWith("*")){
            lastDivider = false
            lastNumeric = false
            lastDot = false
        }else if(binding.inputText.text.toString().endsWith(".")){
            lastDivider = false
            lastNumeric = false
            lastDot = true
        }else{
            lastDivider = false
            lastNumeric = true
            lastDot = false
        }
        if(binding.inputText.text.toString().isEmpty()){
            clear()
        }
    }

    private fun isOperatorAdded(value: String): Boolean{
        return if(value.startsWith("-")){
            false
        }else{
            value.last().toString() == "/"
                    || value.last().toString() == "*"
                    || value.last().toString() == "+"
                    || value.last().toString() == "-"
        }
    }
}