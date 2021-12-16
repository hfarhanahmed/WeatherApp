package com.farhan.weather.weather

import com.farhan.domain.common.WError
import com.farhan.domain.entities.WeatherEntity
import com.farhan.domain.extensions.subscribeAddFavoriteCityResult
import com.farhan.domain.extensions.subscribeWeatherResult
import com.farhan.domain.usecases.AddFavoriteCityUseCase
import com.farhan.domain.usecases.GetWeatherByCityUseCase
import com.farhan.domain.usecases.GetWeatherUseCase
import com.farhan.weather.R
import com.farhan.weather.base.BaseViewModel
import com.farhan.weather.base.ScreenAction
import com.farhan.weather.base.ScreenState
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getWeatherByCityUseCase: GetWeatherByCityUseCase,
    private val addFavoriteCityUseCase: AddFavoriteCityUseCase
) : BaseViewModel<ScreenState<WeatherEntity>>() {

    private val refreshSubject = BehaviorSubject.createDefault(Any())
    private val locationSubject = BehaviorSubject.create<Array<Double>>()

    init {
        Observable.combineLatest(refreshSubject, locationSubject,
            BiFunction { refresh: Any, location: Array<Double> -> location })
            .flatMapSingle {
                getWeatherUseCase(it.component1(), it.component2()).addToLoadingState()
            }
            .subscribeWeatherResult({ _screenState.value = ScreenState.success(it) }, {
                when (it) {
                    is WError.Offline -> _screenState.value = ScreenState.noInternet()
                    is WError.Timeout -> _screenState.value = ScreenState.error(R.string.error_timeout_message)
                    is WError.Unknown -> _screenState.value = ScreenState.error(R.string.error_loading_failed_message)
                }
            })
            .addToComposite()
    }

    fun loadWeather(coordinates: Array<Double>) {
        locationSubject.onNext(coordinates)
    }

    fun loadWeatherByCity(cityName: String) {
        getWeatherByCityUseCase(cityName).addToLoadingState()
            .subscribeWeatherResult({
                it.isCity = true
                _screenState.value = ScreenState.success(it) }, {
            when (it) {
                is WError.Offline -> _screenState.value = ScreenState.noInternet()
                is WError.Timeout -> _screenState.value = ScreenState.error(R.string.error_timeout_message)
                is WError.Unknown -> _screenState.value = ScreenState.error(R.string.error_loading_failed_message)
            }
        }).addToComposite()
    }

    fun addCityToFavorite(cityName:String){
        addFavoriteCityUseCase(cityName)
    }

    fun attach(actionStream: Observable<ScreenAction>) = actionStream.subscribe { handleAction(it) }.addToComposite()

    private fun handleAction(action: ScreenAction) {
        when (action) {
            ScreenAction.PullToRefreshAction -> { refreshSubject.onNext(Any()) }
        }
    }
}