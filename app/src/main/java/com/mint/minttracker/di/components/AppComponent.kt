package com.mint.minttracker.di.components

import com.mint.minttracker.di.modules.ContextModule
import com.mint.minttracker.di.modules.DatabaseModule
import com.mint.minttracker.di.modules.InteractorModule
import com.mint.minttracker.historyFragment.HistoryPresenter
import com.mint.minttracker.mapFragment.LocationServiceForeground
import com.mint.minttracker.mapFragment.MapPresenter
import com.mint.minttracker.recordFragment.RecordPresenter
import com.mint.minttracker.services.LocationService
import dagger.Component
import javax.inject.Scope

@AppScope
@Component(modules = [ContextModule::class, DatabaseModule::class, InteractorModule::class])
interface AppComponent {
//    fun getDatabase(): AppDatabase
    fun getLocationService(): LocationService

    fun injectMapPresenter(mapPresenter: MapPresenter)
    fun injectHistoryPresenter(historyPresenter: HistoryPresenter)
    fun injectRecordPresenter(recordPresenter: RecordPresenter)
    fun injectLocationServiceForeground(locationServiceForeground: LocationServiceForeground)
//    fun injectIHistoryInteractor(iHistoryInteractor: IHistoryInteractor)

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope