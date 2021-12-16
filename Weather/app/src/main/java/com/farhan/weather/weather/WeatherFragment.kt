package com.farhan.weather.weather

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.lifecycle.Observer
import com.farhan.weather.BR
import com.farhan.weather.R
import com.farhan.weather.base.BaseFragment
import com.farhan.weather.base.ScreenAction
import com.farhan.weather.base.ScreenState
import com.farhan.weather.common.*
import com.farhan.weather.databinding.FragmentWeatherBinding
import com.google.android.gms.location.LocationServices
import com.jakewharton.rxrelay2.PublishRelay
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.item_weather.*

class WeatherFragment : BaseFragment<FragmentWeatherBinding, WeatherViewModel>() {

    private val actionStream: PublishRelay<ScreenAction> = PublishRelay.create()
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }

    override fun getLayoutResId(): Int = R.layout.fragment_weather

    override fun getBindingVariableId(): Int? = BR.vm

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (actionStream.hasObservers().not()) viewModel.attach(actionStream)
        viewModel.screenState.observe(viewLifecycleOwner, Observer { screenState ->
            when (screenState) {
                is ScreenState.Loading -> refreshLayout.isRefreshing = screenState.isLoading
                is ScreenState.Error -> {
                    emptyLayout.visible()
                    noWifiLayout.gone()
                    weatherCardView.gone()
                }
                is ScreenState.NoInternet -> {
                    emptyLayout.gone()
                    noWifiLayout.visible()
                    weatherCardView.gone()
                }
                is ScreenState.Success -> {
                    emptyLayout.gone()
                    noWifiLayout.gone()
                    weatherCardView.visible()

                    when(screenState.data.isCity){
                        true -> favoriteIcon.visible()
                        else -> favoriteIcon.gone()
                    }

                    weatherTemp.text = screenState.data.temperature.toString()
                    weatherCondition.text = screenState.data.condition
                    weatherWind.text = "${screenState.data.wind} m/s"
                    weatherCity.text = screenState.data.city
                    weatherWindDirection.text = getString(screenState.data.windDirection.getStringRes())
                    weatherUpdatedOn.text = screenState.data.lastUpdatedAt.getFormattedDate()
                    weatherIcon.load(screenState.data.iconUrl.trim(), R.drawable.ic_error)
                }
            }
        })
    }

    override fun initUi() {
        binding.refreshLayout.setOnRefreshListener { actionStream.accept(ScreenAction.PullToRefreshAction) }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        RxPermissions(this)
            .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { granted ->
                if (granted) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location == null ){
                            emptyLayout.visible()
                            noWifiLayout.gone()
                            weatherCardView.gone()
                        }else
                            viewModel.loadWeather(arrayOf(location.latitude , location.longitude))
                    }
                } else {
                    emptyLayout.visible()
                    noWifiLayout.gone()
                    weatherCardView.gone()
                }
            }
            .addToComposite()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
        val searchView = (menu.findItem(R.id.search).actionView as SearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(qString: String): Boolean {
                return true
            }
            override fun onQueryTextSubmit(qString: String): Boolean {
                viewModel.loadWeatherByCity((qString))
                searchView.setQuery("",false)
                hideKeyboard(activity)
                return true
            }
        })
    }
}