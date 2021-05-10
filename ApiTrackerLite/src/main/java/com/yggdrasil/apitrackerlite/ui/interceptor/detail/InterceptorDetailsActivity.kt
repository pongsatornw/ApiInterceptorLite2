package com.yggdrasil.apitrackerlite.ui.interceptor.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.yggdrasil.apitrackerlite.databinding.ActivityInterceptorDetailsBinding
import com.yggdrasil.apitrackerlite.interceptor.DataHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class InterceptorDetailsActivity : AppCompatActivity() {

    private val binding: ActivityInterceptorDetailsBinding by lazy {
        ActivityInterceptorDetailsBinding.inflate(layoutInflater)
    }

    private val adapter: InterceptorDetailsPagerAdapter by lazy {
        InterceptorDetailsPagerAdapter(this)
    }

    private val viewModel by viewModel<InterceptDetailsViewModel>()

    companion object {

        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f

        private const val EXTRAS_INTERCEPTOR_DATA = "extras-interceptor-data"
        private const val EXTRAS_TIMESTAMP_DATA = "extras-timestamp-data"

        fun createIntent(context: Context, timeStamp: Long): Intent {
            return Intent(context, InterceptorDetailsActivity::class.java).apply {
                putExtra(EXTRAS_TIMESTAMP_DATA, timeStamp)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        extractsExtra()
        setUpView()
        observeEvent()
    }

    private fun extractsExtra() {
        intent.extras?.let { bundle ->
            bundle.getParcelable(EXTRAS_INTERCEPTOR_DATA)
                ?: DataHolder.getDataByTimeStamp(bundle.getLong(EXTRAS_TIMESTAMP_DATA))
        }?.let { params ->
            viewModel.setInterceptorData(params)
        } ?: run {
            setErrorDataNotFound()
        }
    }

    /**
     * Data Not Found, Navigate to latest activity.
     * */
    private fun setErrorDataNotFound() {
        println("ERROR, Data not found")
    }

    private fun setUpView() {
        setViewPager()
        setTabLayout()
    }

    private fun observeEvent() {
        viewModel.interceptorObject.observe(this) {
            println("observeEvent_Activity")
        }
    }

    private fun setViewPager() {
        binding.viewPager.adapter = this.adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) {
                binding.viewPager.currentItem = position
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }

            override fun onPageScrollStateChanged(state: Int) = Unit
        })
        binding.viewPager.setPageTransformer { page: View, position: Float ->
            page.apply {
                val pageWidth = width
                val pageHeight = height
                alpha = when {
                    position < -1 -> {
                        0f
                    }
                    position <= 1 -> {
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> {
                        0f
                    }
                }
            }
        }
    }

    private fun setTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Overview"
                1 -> "Request"
                2 -> "Response"
                else -> throw Exception()
            }
            binding.viewPager.currentItem = position
        }.attach()
    }
}