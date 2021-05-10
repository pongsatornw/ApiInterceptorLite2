package com.example.custominterceptorapplication.ui.interceptor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.custominterceptorapplication.databinding.ViewInterceptorBodyBinding
import com.example.custominterceptorapplication.databinding.ViewInterceptorHeaderBinding
import com.example.custominterceptorapplication.interceptor.CustomInterceptor
import com.example.custominterceptorapplication.interceptor.DataHolder

class InterceptorAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list get() = DataHolder.getData()

    class HeaderViewHolder(private val binding: ViewInterceptorHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
            binding.title.text = "API_LOG"
        }
    }

    class BodyViewHolder(private val binding: ViewInterceptorBodyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CustomInterceptor.Params) {
            binding.title.text = data.createCodeAndMethod()
            binding.url.text = data.url
            binding.header.text = data.headers.toString()
            binding.body.text = "Test Body"
        }
    }

    enum class ViewType(val value: Int) {
        Header(0), Body(1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.Header.value -> HeaderViewHolder(
                ViewInterceptorHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> BodyViewHolder(
                ViewInterceptorBodyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is BodyViewHolder -> holder.bind(list[position - 1])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewType.Header.value
            else -> ViewType.Body.value
        }
    }

    override fun getItemCount(): Int = list.size + 1
}