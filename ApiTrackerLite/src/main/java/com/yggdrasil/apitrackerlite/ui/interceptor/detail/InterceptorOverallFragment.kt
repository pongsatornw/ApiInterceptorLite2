package com.yggdrasil.apitrackerlite.ui.interceptor.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yggdrasil.apitrackerlite.ui.interceptor.adapter.InterceptorOverallAdapter
import com.yggdrasil.apitrackerlite.databinding.FragmentInterceptorOverallBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InterceptorOverallFragment : Fragment() {

    private val viewModel by sharedViewModel<InterceptDetailsViewModel>()

    private lateinit var binding: FragmentInterceptorOverallBinding

    private var adapter = InterceptorOverallAdapter()

    companion object {

        fun newInstance() = InterceptorOverallFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterceptorOverallBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpView()
        observeEvent()
    }

    private fun setUpView() {
         binding.recyclerView.apply {
             adapter = this@InterceptorOverallFragment.adapter
             setHasFixedSize(true)
             layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
         }
    }

    private fun observeEvent() {
        viewModel.interceptorObject.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }
}