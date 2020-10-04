package com.example.weatherapp.ui.utlis

import android.view.View
import androidx.fragment.app.Fragment
import com.example.weatherapp.data.network.Resource
import com.google.android.material.snackbar.Snackbar


fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}


fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> requireView().snackbar(
            "Please check your internet connection",
            retry
        )
        else -> {
            val message = " " + failure.errorCode?.toString() + "\n" + failure.errorBody?.toString()
            requireView().snackbar(message)
        }
    }
}