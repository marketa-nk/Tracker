package com.mint.minttracker.di.modules

import android.content.Context
import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.domain.buttonControl.ButtonControlInteractorImpl
import com.mint.minttracker.domain.location.LocationInteractorImpl
import com.mint.minttracker.domain.buttonControl.ButtonControlInteractor
import com.mint.minttracker.domain.history.HistoryInteractorImpl
import com.mint.minttracker.domain.history.HistoryInteractor
import com.mint.minttracker.domain.location.LocationInteractor
import com.mint.minttracker.domain.map.MapInteractor
import com.mint.minttracker.domain.map.MapInteractorImpl
import com.mint.minttracker.domain.record.RecordInteractor
import com.mint.minttracker.domain.record.RecordInteractorImpl
import com.mint.minttracker.locationRepository.LocationRepository
import com.mint.minttracker.locationRepository.LocationService
import dagger.Module
import dagger.Provides

@Module
class InteractorModule() {

    @Provides
    fun provideHistoryInteractor(dataBaseRepository: DataBaseRepository): HistoryInteractor {
        return HistoryInteractorImpl(dataBaseRepository)
    }

    @Provides
    fun provideIRecordInteractor(dataBaseRepository: DataBaseRepository): RecordInteractor {
        return RecordInteractorImpl(dataBaseRepository)
    }

    @Provides
    fun provideIMapInteractor(dataBaseRepository: DataBaseRepository, locationService: LocationRepository): MapInteractor {
        return MapInteractorImpl(dataBaseRepository)
    }

    @Provides
    fun provideButtonControlInteractor(appContext: Context): ButtonControlInteractor {
        return ButtonControlInteractorImpl(appContext)
    }

    @Provides
    fun provideLocationInteractor(locationService: LocationRepository): LocationInteractor {
        return LocationInteractorImpl(locationService)
    }

    @AppScope
    @Provides
    fun provideLocationRepository(context: Context): LocationRepository {
        return LocationService(context)
    }
}
