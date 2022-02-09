package com.mint.minttracker.di.modules

import com.mint.minttracker.database.DataBaseRepository
import com.mint.minttracker.database.IDataBaseRepository
import com.mint.minttracker.di.components.AppScope
import com.mint.minttracker.domain.history.HistoryInteractor
import com.mint.minttracker.domain.history.IHistoryInteractor
import com.mint.minttracker.domain.map.IMapInteractor
import com.mint.minttracker.domain.map.MapInteractor
import com.mint.minttracker.domain.record.IRecordInteractor
import com.mint.minttracker.domain.record.RecordInteractor
import com.mint.minttracker.services.LocationService
import dagger.Module
import dagger.Provides

@Module
class InteractorModule() {
    @AppScope
    @Provides
    fun provideIHistoryInteractor(dataBaseRepository: IDataBaseRepository): IHistoryInteractor {
        return HistoryInteractor(dataBaseRepository)
    }
    @AppScope
    @Provides
    fun provideIRecordInteractor(dataBaseRepository: IDataBaseRepository): IRecordInteractor {
        return RecordInteractor(dataBaseRepository)
    }
    @AppScope
    @Provides
    fun provideIMapInteractor(dataBaseRepository: IDataBaseRepository, locationService: LocationService): IMapInteractor {
        return MapInteractor(dataBaseRepository, locationService)
    }
}