package com.yggdrasil.apitrackerlite.ui.interceptor.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.yggdrasil.apitrackerlite.ui.interceptor.adapter.InterceptorHeaderAdapter
import com.yggdrasil.apitrackerlite.ui.interceptor.adapter.InterceptorQueryParamsAdapter
import com.yggdrasil.apitrackerlite.databinding.FragmentInterceptorRequestBinding
import com.yggdrasil.apitrackerlite.interceptor.ApiInterceptor
import com.yuyh.jsonviewer.library.adapter.JsonViewerAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InterceptorRequestFragment : Fragment() {

    private val viewModel by sharedViewModel<InterceptDetailsViewModel>()

    private lateinit var binding: FragmentInterceptorRequestBinding

    private val headerAdapter: InterceptorHeaderAdapter = InterceptorHeaderAdapter()
    private val queryParamsAdapter: InterceptorQueryParamsAdapter = InterceptorQueryParamsAdapter()
    private lateinit var bodyAdapter: JsonViewerAdapter
    private lateinit var adapter: ConcatAdapter

    companion object {

        fun newInstance() = InterceptorRequestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterceptorRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeEvent()
    }

    private fun observeEvent() {
        viewModel.interceptorObject.observe(viewLifecycleOwner) {
            setView(it)
        }
    }

    private fun setView(params: ApiInterceptor.Params) {
        headerAdapter.updateData(params)
        queryParamsAdapter.updateData(params)

        adapter = try {
            bodyAdapter = JsonViewerAdapter(params.requestBody ?: "")
            ConcatAdapter(headerAdapter, queryParamsAdapter, bodyAdapter)
        } catch (ex: IllegalArgumentException) {
            ConcatAdapter(headerAdapter, queryParamsAdapter)
        }

        binding.recyclerView.apply {
            adapter = this@InterceptorRequestFragment.adapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}