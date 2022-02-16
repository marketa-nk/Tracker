package com.mint.minttracker.di.components

import android.content.Context
import com.mint.minttracker.di.modules.ContextModule
import com.mint.minttracker.di.modules.DatabaseModule
import com.mint.minttracker.di.modules.InteractorModule
import com.mint.minttracker.historyFragment.HistoryPresenter
import com.mint.minttracker.mapFragment.MapPresenter
import com.mint.minttracker.recordFragment.RecordPresenter
import com.mint.minttracker.services.LocationServiceForeground
import dagger.Component
import javax.inject.Scope

@AppScope
@Component(modules = [ContextModule::class, DatabaseModule::class, InteractorModule::class])
interface AppComponent {
//    fun getLocationService(): LocationRepository

    fun injectMapPresenter(mapPresenter: MapPresenter)
    fun injectHistoryPresenter(historyPresenter: HistoryPresenter)
    fun injectRecordPresenter(recordPresenter: RecordPresenter)
    fun injectLocationServiceForeground(locationServiceForeground: LocationServiceForeground)

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope