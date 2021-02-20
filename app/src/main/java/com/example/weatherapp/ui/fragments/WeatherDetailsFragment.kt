package com.example.weatherapp.ui.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.repository.darksky.api.domain.LocationWeatherDetails
import com.example.weatherapp.repository.database.domain.LocalWeatherInfo
import com.example.weatherapp.ui.activities.MainActivity
import com.example.weatherapp.ui.lifecycleobservers.LifecycleAwareCompositeDisposable
import com.example.weatherapp.ui.viewmodels.MainViewModel
import com.example.weatherapp.utils.remove
import com.example.weatherapp.utils.scrollSmoothlyToEnd
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_weather_details.*
import kotlinx.android.synthetic.main.layout_weather_details_holder.view.*
import javax.inject.Inject
import kotlin.math.roundToLong

class WeatherDetailsFragment : Fragment() {
    companion object {
        private const val PROGRESS_VIEW_Y_TRANSLATION = -300f
        private const val PROGRESS_VIEW_TRANSLATION_DURATION = 1000L

        private val TAG by lazy { WeatherDetailsFragment::class.java.simpleName }
    }

    @Inject
    lateinit var viewModel: MainViewModel
    private lateinit var lifecycleAwareSubscriptions: LifecycleAwareCompositeDisposable
    private val locationsWeatherAdapter = LocationsWeatherAdapter()

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val initLocationsWeatherRecyclerView: () -> Unit = {
            locationsWeatherList.setHasFixedSize(true)
            locationsWeatherList.layoutManager = LinearLayoutManager(context)
            locationsWeatherList.adapter = locationsWeatherAdapter
            locationsWeatherAdapter.submitList(mutableListOf())
        }

        initLocationsWeatherRecyclerView()
    }

    private fun subscribeToWeatherDetails() {
        lifecycleAwareSubscriptions.subscribe(
            viewModel.getAllWeatherDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    locationsWeatherAdapter.submitList(it)
                }, {
                    Log.e(TAG, "Cannot get stored locations' weather details")
                })
        )
    }

    private fun fetchCurrentLocationWeatherDetails() {
        val hideLoadingIndicator: () -> Unit = {
            ObjectAnimator.ofFloat(progressView, "translationY", PROGRESS_VIEW_Y_TRANSLATION)
                .apply {
                    duration = PROGRESS_VIEW_TRANSLATION_DURATION
                    start()
                }
        }
        val onSuccess = Consumer<LocationWeatherDetails> {
            Log.d(TAG, "Successfully fetched location's weather data")
            hideLoadingIndicator()
            locationsWeatherList.scrollSmoothlyToEnd()
        }
        val onError = Consumer<Throwable> {
            Log.e(TAG, "Cannot fetch location weather: ${it.message}")
            hideLoadingIndicator()
        }

        lifecycleAwareSubscriptions.subscribe(
            viewModel.getAndSaveSelectedLocationWeather()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError)
        )
    }

    override fun onStart() {
        super.onStart()
        val fetchWeatherDetailsIfNeeded: () -> Unit = {
            if (!viewModel.isDataLoaded) {
                fetchCurrentLocationWeatherDetails()
            } else {
                progressView.remove()
            }
        }

        subscribeToWeatherDetails()
        fetchWeatherDetailsIfNeeded()
    }
}

class LocationsWeatherAdapter :
    ListAdapter<LocalWeatherInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_weather_details_holder, parent, false)
        return WeatherDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WeatherDetailsViewHolder) {
            holder.bind(getItem(position))
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<LocalWeatherInfo> =
            object : DiffUtil.ItemCallback<LocalWeatherInfo>() {
                override fun areItemsTheSame(
                    oldItem: LocalWeatherInfo,
                    newItem: LocalWeatherInfo
                ): Boolean {
                    return oldItem.city == newItem.city
                }

                override fun areContentsTheSame(
                    oldItem: LocalWeatherInfo,
                    newItem: LocalWeatherInfo
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class WeatherDetailsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(locationWeatherInfo: LocalWeatherInfo) {
            view.city.text = locationWeatherInfo.city
            view.country.text = locationWeatherInfo.country
            view.degrees.text = locationWeatherInfo.weather.temperature.roundToLong().toString()
            view.weatherIcon.setImageResource(
                locationWeatherInfo.weather.getIconResourceId()
            )
            view.timestamp.text = locationWeatherInfo.weather.getFormattedDate()
        }
    }
}
