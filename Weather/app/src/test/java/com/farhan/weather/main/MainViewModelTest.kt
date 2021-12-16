package com.farhan.weather.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.farhan.domain.common.WError
import com.farhan.domain.common.WResult
import com.farhan.domain.usecases.GetWeatherUseCase
import com.farhan.weather.base.ScreenAction
import com.farhan.weather.base.ScreenState
import com.farhan.weather.weather.WeatherViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.jraska.livedata.test
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private val getWeatherUseCase: GetWeatherUseCase = mock()
    private lateinit var weatherViewModel: WeatherViewModel
    private val actionStream: PublishRelay<ScreenAction> = PublishRelay.create()

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        weatherViewModel = WeatherViewModel(getWeatherUseCase)
    }

    @After fun tearDown() {
        verifyNoMoreInteractions(getWeatherUseCase)
    }

    @Test fun givenCoordinatesAreGivenThenWeatherIsLoaded() {
        whenever(getWeatherUseCase(anyDouble(), anyDouble())).thenReturn(Single.just(WResult.Success(MockDataHelper.getWeatherEntity())))

        val testObserver = weatherViewModel.screenState.test()

        weatherViewModel.loadWeather(arrayOf(50.0, 55.0))

        testObserver
            .assertHasValue()
            .assertHistorySize(3)
            .assertValue { it is ScreenState.Success }
            .assertNever { it is ScreenState.NoInternet }
            .assertNever { it is ScreenState.Error }

        verify(getWeatherUseCase).invoke(anyDouble(), anyDouble())
    }

    @Test fun givenUserRefreshesThenWeatherIsLoadedForASecondTime() {
        whenever(getWeatherUseCase(anyDouble(), anyDouble())).thenReturn(Single.just(WResult.Success(MockDataHelper.getWeatherEntity())))

        val testObserver = weatherViewModel.screenState.test()

        weatherViewModel.loadWeather(arrayOf(50.0, 55.0))
        weatherViewModel.attach(actionStream)
        actionStream.accept(ScreenAction.PullToRefreshAction)

        testObserver
                .assertHasValue()
                .assertHistorySize(6)
                .assertValue { it is ScreenState.Success }
                .assertNever { it is ScreenState.NoInternet }
                .assertNever { it is ScreenState.Error }

        verify(getWeatherUseCase, times(2)).invoke(anyDouble(), anyDouble())
    }

    @Test fun givenUserIsOfflineThenShowOfflineState() {
        whenever(getWeatherUseCase(anyDouble(), anyDouble()))
                .thenReturn(Single.just(WResult.Failure(WError.Offline(Exception()))))

        val testObserver = weatherViewModel.screenState.test()

        weatherViewModel.loadWeather(arrayOf(50.0, 55.0))

        testObserver
                .assertHasValue()
                .assertHistorySize(3)
                .assertNever { it is ScreenState.Success }
                .assertValue { it is ScreenState.NoInternet }
                .assertNever { it is ScreenState.Error }

        verify(getWeatherUseCase).invoke(anyDouble(), anyDouble())
    }

}