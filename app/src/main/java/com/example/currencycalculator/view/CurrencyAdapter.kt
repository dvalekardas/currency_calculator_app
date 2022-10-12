package com.example.currencycalculator.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.currencycalculator.R
import com.example.currencycalculator.databinding.CurrencyItemBinding
import com.example.currencycalculator.model.models.Currency

class CurrencyAdapter(private val context: Context, private val currencies: ArrayList<Currency>, private val baseCurrency: String?, private val conversionCurrency: String?) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyAdapter.CurrencyViewHolder {
        val binding = CurrencyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyAdapter.CurrencyViewHolder, position: Int) {
        val currency: Currency = currencies[position]
        holder.bind(currency)
        if(baseCurrency!=null){
            if(currency.shortName ==baseCurrency){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            }else{
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }
        }else if(conversionCurrency!=null){
            if(currency.shortName == conversionCurrency){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            }else{
                holder.itemView.setBackgroundColor(Color.LTGRAY)
            }
        }
        holder.itemView.setOnClickListener {
            if(onClickListener!=null){
                onClickListener!!.onClick(position, currency)
            }
        }
    }

    override fun getItemCount() = currencies.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateCurrencies(newCurrencies: List<Currency>){
        currencies.clear()
        currencies.addAll(newCurrencies)
        notifyDataSetChanged()
    }

    interface OnClickListener{
        fun onClick(position:Int, model: Currency)
    }

    fun setOnClickListener(listener:OnClickListener){
        this.onClickListener = listener
    }

    inner class CurrencyViewHolder(private val binding: CurrencyItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currency:Currency){
            binding.currencyShortName.text = currency.shortName
            binding.currencyFullName.text = currency.fullName
        }
    }
}