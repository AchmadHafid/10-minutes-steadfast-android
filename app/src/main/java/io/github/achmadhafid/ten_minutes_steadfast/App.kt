package io.github.achmadhafid.ten_minutes_steadfast

import android.app.Application
import io.github.achmadhafid.ten_minutes_steadfast.di.DaggerAppComponent

class App : Application() {

    val appComponent by lazy {
        DaggerAppComponent.builder()
            .context(this)
            .build()
    }

}
