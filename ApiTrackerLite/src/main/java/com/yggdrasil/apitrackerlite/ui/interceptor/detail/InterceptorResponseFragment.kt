package com.yggdrasil.apitrackerlite.ui.interceptor.detail

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.yggdrasil.apitrackerlite.databinding.FragmentInterceptorResponseBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InterceptorResponseFragment : Fragment() {

    private val viewModel by sharedViewModel<InterceptDetailsViewModel>()

    private lateinit var binding: FragmentInterceptorResponseBinding

    companion object {

        fun newInstance() = InterceptorResponseFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterceptorResponseBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpJsonRecyclerView()
        observeEvent()
    }

    private fun observeEvent() {
        viewModel.interceptorObject.observe(viewLifecycleOwner) {
            binding.jsonRecyclerView.bindJson(it.responseBody)
        }
    }

    private fun setUpJsonRecyclerView() {
        binding.jsonRecyclerView.apply {
            setTextSize(14f)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.set(12, 8, 12, 8)
                }
            })
        }
    }
}