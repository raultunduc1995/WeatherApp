package com.example.weatherapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.repository.geocoder.api.domain.Result
import com.example.weatherapp.ui.activities.MainActivity
import com.example.weatherapp.ui.lifecycleobservers.LifecycleAwareCompositeDisposable
import com.example.weatherapp.ui.listeners.ContainerOnTouchListener
import com.example.weatherapp.ui.listeners.DoneOnEditorActionListener
import com.example.weatherapp.ui.viewmodels.MainViewModel
import com.example.weatherapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_entry_point.*
import javax.inject.Inject

class EntryPointFragment : Fragment() {

    companion object {
        private val TAG by lazy { EntryPointFragment::class.java.simpleName }
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var lifecycleAwareSubscriptions: LifecycleAwareCompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleAwareSubscriptions = LifecycleAwareCompositeDisposable(lifecycle).apply {
            lifecycle.addObserver(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).entryComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entry_point, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showLoading: () -> Unit = {
            getLocationWeatherButton.disable()
            getLocationWeatherButton.text = ""
            circularProgress.show()
        }
        val hideLoading: () -> Unit = {
            circularProgress.remove()
            getLocationWeatherButton.text = getString(R.string.get_location_weather)
            getLocationWeatherButton.enable()
        }
        val resetFields: () -> Unit = {
            countryTextInputWrapper.error = null
            cityTextInputWrapper.error = null
        }
        val areFieldsValid: () -> Boolean = {
            var areFieldsValid = true
            if (!cityTextInput.isValid()) {
                cityTextInputWrapper.error = getString(R.string.fill_in_with_correct_data)
                areFieldsValid = false
            }
            if (!countryTextInput.isValid()) {
                countryTextInputWrapper.error = getString(R.string.fill_in_with_correct_data)
                areFieldsValid = false
            }
            areFieldsValid
        }
        val showWeatherDetails = Consumer<Result> {
            hideLoading()
            (activity as AppCompatActivity).replaceFragment(
                R.id.container,
                WeatherDetailsFragment()
            )
        }
        val setErrorOnBothFields: () -> Unit = {
            cityTextInputWrapper.error = getString(R.string.fill_in_with_correct_data)
            countryTextInputWrapper.error = getString(R.string.fill_in_with_correct_data)
        }
        val handleLocationError = Consumer<Throwable> { error ->
            hideLoading()
            setErrorOnBothFields()
            Log.e(TAG, "Unknown provided location details: ${error.message}")
        }
        val fetchLocationInfo: () -> Unit = {
            val city = cityTextInput.text.toString()
            val country = countryTextInput.text.toString()

            lifecycleAwareSubscriptions.subscribe(
                viewModel.getLocationDetails(city, country)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(showWeatherDetails, handleLocationError)
            )
        }
        val buttonListener = View.OnClickListener {
            showLoading()
            resetFields()
            if (areFieldsValid()) {
                fetchLocationInfo()
            } else {
                hideLoading()
            }
        }

        viewModel.init()
        fragmentContainer.setOnTouchListener(ContainerOnTouchListener())
        cityTextInput.setOnEditorActionListener(DoneOnEditorActionListener())
        getLocationWeatherButton.setOnClickListener(buttonListener)
    }
}
