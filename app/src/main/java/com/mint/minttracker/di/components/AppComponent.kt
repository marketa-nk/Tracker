package com.mint.minttracker.di.components

import com.mint.minttracker.di.modules.ContextModule
import com.mint.minttracker.di.modules.DatabaseModule
import com.mint.minttracker.di.modules.InteractorModule
import com.mint.minttracker.historyFragment.HistoryFragment
import com.mint.minttracker.mapFragment.MapFragment
import com.mint.minttracker.recordFragment.RecordFragment
import com.mint.minttracker.recordFragment.RecordViewModel
import com.mint.minttracker.services.LocationServiceForeground
import dagger.Component
import javax.inject.Scope

@AppScope
@Component(modules = [ContextModule::class, DatabaseModule::class, InteractorModule::class])
interface AppComponent {

    fun injectRecordViewModel(recordViewModel: RecordViewModel)
    fun injectLocationServiceForeground(locationServiceForeground: LocationServiceForeground)
    fun injectRecordFragment(recordFragment: RecordFragment)
    fun injectHistoryFragment(historyFragment: HistoryFragment)
    fun injectMapFragment(mapFragment: MapFragment)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope