package com.example.weatherapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.weatherapp.data.network.Resource
import com.example.weatherapp.data.network.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.ui.base.BaseFragment
import com.example.weatherapp.ui.utlis.handleApiError
import com.example.weatherapp.ui.utlis.updateUi

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, WeatherRepository>() {

    private val apiKey = "*****************api***************"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.swipeToRefresh.isRefreshing = true
        viewModel.getWeather("-33.8523341", "151.2106085", apiKey)

        viewModel.weatherResponse.observe(viewLifecycleOwner, {
            when (it) {

                is Resource.Success -> {
                    updateUi(binding.root, it.value, true)
                    binding.swipeToRefresh.isRefreshing = false
                }
                is Resource.Failure -> {
                    binding.swipeToRefresh.isRefreshing = false
                    handleApiError(it) {
                        viewModel.getWeather(
                            "-33.8523341",
                            "151.2106085",
                            apiKey
                        )
                    }
                }
            }
        })
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.getWeather("29.8523341", "76.2106085", apiKey)
        }
    }

    override fun getViewModal() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        WeatherRepository(remoteDataSource.buildApi(WeatherApi::class.java))


}
