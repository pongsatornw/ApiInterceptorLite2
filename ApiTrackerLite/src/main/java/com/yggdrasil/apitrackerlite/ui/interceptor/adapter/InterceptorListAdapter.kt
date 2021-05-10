package com.example.custominterceptorapplication.ui.interceptor.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.custominterceptorapplication.databinding.ViewInterceptorHeaderBinding
import com.example.custominterceptorapplication.databinding.ViewInterceptorOverallBinding
import com.example.custominterceptorapplication.databinding.ViewInterceptorTitleBinding
import com.example.custominterceptorapplication.interceptor.CustomInterceptor
import com.example.custominterceptorapplication.interceptor.DataHolder

class InterceptorListAdapter(
    private val onClickEvent: OnInterceptorClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list get() = DataHolder.getData()

    private val hostSet: List<String>
        get() = list.asSequence().map { Uri.parse(it.url).host ?: "" }
            .filter { it != "" }.distinct().toList()

    private val resultList: List<Any>
        get() {
            val result = mutableListOf<Any>()
            hostSet.forEach { host ->
                result.add(host)
                result.addAll(list.filter { it.url.contains(host) })
            }

            return result
        }

    fun refreshData() {
        notifyDataSetChanged()
    }

    class HeaderViewHolder(private val binding: ViewInterceptorHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
            binding.title.text = "API_LOG_OVER_ALL"
        }
    }

    class TitleViewHolder(private val binding: ViewInterceptorTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(url: String) {
            binding.title.text = url
        }
    }

    class BodyViewHolder(private val binding: ViewInterceptorOverallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CustomInterceptor.Params, isLastPosition: Boolean) {
            binding.title.text = data.createCodeAndMethod()
            binding.url.text = data.url
            binding.divider.visibility = when (isLastPosition) {
                true -> View.INVISIBLE
                false -> View.VISIBLE
            }
        }
    }

    enum class ViewType(val value: Int) {
        Header(0), Title(1), Body(2)
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
            ViewType.Title.value -> TitleViewHolder(
                ViewInterceptorTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ).apply {
                this.itemView.setOnClickListener { _ ->
                    onClickEvent.onTitleClick(resultList[this.adapterPosition - 1] as String)
                }
            }
            else -> BodyViewHolder(
                ViewInterceptorOverallBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            ).apply {
                this.itemView.setOnClickListener { _ ->
                    onClickEvent.onBodyClick(resultList[this.adapterPosition - 1] as CustomInterceptor.Params)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is TitleViewHolder -> holder.bind(resultList[position - 1] as String)
            is BodyViewHolder -> holder.bind(
                resultList[position - 1] as CustomInterceptor.Params,
                shouldHideLineDivider(position)
            )
        }
    }

    // If Given Position is String,  it is title type.
    private fun shouldHideLineDivider(position: Int): Boolean =
        (position == (itemCount - 1)) || (resultList.getOrNull(position + 1) is String)

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewType.Header.value
            else -> when (resultList[position - 1]) {
                is String -> ViewType.Title.value
                is CustomInterceptor.Params -> ViewType.Body.value
                else -> throw Exception("Invalid View Type")
            }
        }
    }

    override fun getItemCount(): Int = 1 + resultList.size

    interface OnInterceptorClick {
        fun onTitleClick(host: String)
        fun onBodyClick(params: CustomInterceptor.Params)
    }
}