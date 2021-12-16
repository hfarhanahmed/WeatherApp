package com.farhan.weather.base

sealed class ScreenAction {
    object PullToRefreshAction : ScreenAction()
}