package com.example.weatherapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.weatherapp.data.network.Resource
import com.example.weatherapp.data.network.WeatherApi
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.ui.base.BaseFragment
import com.example.weatherapp.ui.utlis.updateUi

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, WeatherRepository>() {

    //private val mainUiViewModel: MainUiViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getWeather("-33.8523341", "151.2106085", "21d5615c0b94f68985f7079b0f592dd1")

        viewModel.weatherResponse.observe(viewLifecycleOwner, {
            when (it) {

                is Resource.Success -> {

                    Log.e("Hi", it.value.toString())
                    updateUi(binding.root, it.value)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Damn, Failed To Load Data", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    override fun getViewModal() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        WeatherRepository(remoteDataSource.buildApi(WeatherApi::class.java))
}
