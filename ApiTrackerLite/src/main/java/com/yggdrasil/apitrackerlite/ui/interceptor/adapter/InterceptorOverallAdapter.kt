package com.example.custominterceptorapplication.ui.interceptor.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.custominterceptorapplication.databinding.ViewMapValueBinding
import com.example.custominterceptorapplication.interceptor.CustomInterceptor
import kotlin.math.abs

class InterceptorOverallAdapter :
    RecyclerView.Adapter<InterceptorOverallAdapter.PairDataViewHolder>() {

    private val listData = mutableListOf<Pair<String, Any>>()

    inner class PairDataViewHolder(private val binding: ViewMapValueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<String, Any>) {
            binding.key.text = data.first
            binding.value.text = data.second.toString()
        }
    }

    fun updateData(data: CustomInterceptor.Params) {
        extractData(data)
        notifyDataSetChanged()
    }

    private fun extractData(data: CustomInterceptor.Params) {
        listData.clear()
        val url = Uri.parse(data.url)
        listData.apply {
            add(Pair("Raw Url", data.url))
            add(Pair("Host", url.host ?: "Error Extract Host"))
            add(Pair("Path", url.path ?: "Error Extract Path"))
            add(Pair("Response Code", data.code))
            add(Pair("Request Time", "${data.requestTime} ms."))
            add(Pair("Response Time", "${data.responseTime} ms."))
            add(Pair("RTT", "${abs(data.responseTime - data.requestTime)} ms."))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairDataViewHolder {
        return PairDataViewHolder(
            ViewMapValueBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PairDataViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size
}