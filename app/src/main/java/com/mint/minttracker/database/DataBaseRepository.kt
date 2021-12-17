package com.mint.minttracker.database

import com.mint.minttracker.App
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Single
import java.util.*
import java.util.concurrent.Callable

class DataBaseRepository {

    fun saveMintLocation(mintLocation: MintLocation) {
        App.instance.database.mintLocationDao().insertMintLocation(mintLocation)
    }

    fun createTrack(): Single<Long> {
        return Single.fromCallable(
            Callable<Long> { App.instance.database.tracksDao().insertTrack(Track(0, System.currentTimeMillis()))}
        )
    }
}