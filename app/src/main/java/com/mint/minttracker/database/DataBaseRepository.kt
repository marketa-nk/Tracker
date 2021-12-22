package com.mint.minttracker.database

import com.mint.minttracker.App
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track
import io.reactivex.Single

class DataBaseRepository {

    private val tracksDao = App.instance.database.tracksDao()
    private val mintLocationDao = App.instance.database.mintLocationDao()

    fun saveMintLocation(mintLocation: MintLocation) {
       mintLocationDao.insertMintLocation(mintLocation)
    }

    fun createTrack(): Single<Long> {
        println("Track created - Nata")
        return tracksDao.insertTrack(Track(0, System.currentTimeMillis()))
    }
}