package com.yggdrasil.apitrackerlite.ui.interceptor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yggdrasil.apitrackerlite.ui.interceptor.adapter.InterceptorListAdapter
import com.yggdrasil.apitrackerlite.databinding.ActivityInterceptorOverallBinding
import com.yggdrasil.apitrackerlite.interceptor.ApiInterceptor
import com.yggdrasil.apitrackerlite.interceptor.DataHolder
import com.yggdrasil.apitrackerlite.ui.interceptor.detail.InterceptorDetailsActivity

internal class InterceptorListActivity : AppCompatActivity(), InterceptorListAdapter.OnInterceptorClick {

    private val adapter =
        InterceptorListAdapter(this)

    private val binding: ActivityInterceptorOverallBinding by lazy {
        ActivityInterceptorOverallBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpView()
        observeEvent()
    }

    override fun onTitleClick(host: String) {
        println("On Title Click")
    }

    override fun onBodyClick(params: ApiInterceptor.Params) {
        startActivity(InterceptorDetailsActivity.createIntent(this, params.timeStamp))
    }

    private fun observeEvent() {
        DataHolder.onDataChange.observe(this) {
            adapter.refreshData()
        }
    }

    private fun setUpView() {
        binding.recyclerView.apply {
            adapter = this@InterceptorListActivity.adapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(
                    this@InterceptorListActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        adapter.updateData()
    }
}