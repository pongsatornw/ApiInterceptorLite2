package com.example.custominterceptorapplication.ui.interceptor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.custominterceptorapplication.databinding.ViewInterceptorTitleBinding
import com.example.custominterceptorapplication.databinding.ViewMapValueBinding
import com.example.custominterceptorapplication.interceptor.CustomInterceptor
import okhttp3.internal.http2.Header
import okhttp3.internal.toHeaderList
import java.nio.charset.Charset

class InterceptorHeaderAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<Header>()

    companion object {
        private const val TITLE = 0
        private const val BODY = 1
    }

    inner class PairDataViewHolder(private val binding: ViewMapValueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Header) {
            binding.key.text = data.name.string(Charset.defaultCharset())
            binding.value.text = data.value.string(Charset.defaultCharset())
        }
    }

    inner class TitleDataViewHolder(private val binding: ViewInterceptorTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
            binding.title.text = "Header"
        }
    }

    fun updateData(data: CustomInterceptor.Params) {
        extractData(data)
        notifyDataSetChanged()
    }

    private fun extractData(data: CustomInterceptor.Params) {
        listData.clear()

        listData.addAll(data.headers.toHeaderList())
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