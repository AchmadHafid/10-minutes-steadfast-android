package io.github.achmadhafid.ten_minutes_steadfast.di

import android.content.Context
import io.github.achmadhafid.ten_minutes_steadfast.service.LockerService
import io.github.achmadhafid.ten_minutes_steadfast.ui.activity.HomeActivity
import dagger.BindsInstance
import dagger.Component
import io.github.achmadhafid.ten_minutes_steadfast.App
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {

    fun inject(homeActivity: HomeActivity)
    fun inject(lockerService: LockerService)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }

}

fun HomeActivity.inject() {
    (application as App)
        .appComponent
        .inject(this)
}

fun LockerService.inject() {
    (application as App)
        .appComponent
        .inject(this)
}
