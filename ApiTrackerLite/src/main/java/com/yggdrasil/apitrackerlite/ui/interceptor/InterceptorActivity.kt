package com.example.custominterceptorapplication.ui.interceptor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.custominterceptorapplication.databinding.ActivityInterceptorBinding
import com.example.custominterceptorapplication.ui.interceptor.adapter.InterceptorAdapter

class InterceptorActivity : AppCompatActivity() {

    private val adapter = InterceptorAdapter()

    private val binding: ActivityInterceptorBinding by lazy {
        ActivityInterceptorBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpView()
    }

    override fun onResume() {
        super.onResume()
        adapter.updateData()
    }

    private fun setUpView() {
        binding.recyclerView.apply {
            adapter = this@InterceptorActivity.adapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@InterceptorActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        adapter.updateData()
    }
}