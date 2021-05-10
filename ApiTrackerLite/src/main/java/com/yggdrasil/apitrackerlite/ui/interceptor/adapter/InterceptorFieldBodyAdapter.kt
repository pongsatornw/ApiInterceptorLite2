package com.example.custominterceptorapplication.ui.interceptor.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.custominterceptorapplication.databinding.ViewInterceptorTitleBinding
import com.example.custominterceptorapplication.databinding.ViewMapValueBinding
import com.example.custominterceptorapplication.interceptor.CustomInterceptor
import okhttp3.FormBody

class InterceptorFieldBodyAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<Pair<String, String>>()

    companion object {
        private const val TITLE = 0
        private const val BODY = 1
    }

    inner class PairDataViewHolder(private val binding: ViewMapValueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<String, String>) {
            binding.key.text = data.first
            binding.value.text = data.second
        }
    }

    inner class TitleDataViewHolder(private val binding: ViewInterceptorTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
            binding.title.text = "Query Params"
        }
    }

    fun updateData(data: CustomInterceptor.Params) {
        extractData(data)
        notifyDataSetChanged()
    }

    private fun extractData(data: CustomInterceptor.Params) {
        listData.clear()


    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> TITLE
        else -> BODY
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TITLE -> TitleDataViewHolder(
                ViewInterceptorTitleBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> PairDataViewHolder(
                ViewMapValueBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TitleDataViewHolder -> holder.bind()
            is PairDataViewHolder -> holder.bind(listData[position - 1])
        }
    }

    override fun getItemCount(): Int = listData.size + 1
}