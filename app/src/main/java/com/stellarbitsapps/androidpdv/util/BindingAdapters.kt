package com.stellarbitsapps.androidpdv.util

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

@SuppressLint("SetTextI18n")
@BindingAdapter("bind:value")
fun setValue(textView: TextView, receiptValue: Float?) {
    if (receiptValue != null) {
        textView.text = "R$: ${String.format("%.2f", receiptValue)}"
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("bind:date")
fun setDate(textView: TextView, date: Long?) {
    if (date != null) {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        textView.text = format.format(date)
    }
}