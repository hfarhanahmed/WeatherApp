package com.farhan.weather

import android.os.Bundle
import com.farhan.weather.base.BaseActivity
import kotlinx.android.synthetic.main.activity_launch.*

class LaunchActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_launch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_search)
    }
}
