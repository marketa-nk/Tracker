package com.mint.minttracker.database

import androidx.room.*
import com.mint.minttracker.models.MintLocation
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface MintLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMintLocation(mintLocation: MintLocation): Single<Long>

    @Delete
    fun deleteMintLocations(list: List <MintLocation>): Single<Int>

    @Query("SELECT * FROM mintLocation")
    fun getAllRecords(): List<MintLocation>

    @Query("SELECT * FROM mintLocation WHERE idTrack = :id")
    fun getAllRecordsByID(id: Long): Single<List<MintLocation>>

    @Query("SELECT * FROM mintLocation WHERE idTrack = :id")
    fun getAllRecordsByIDTrack(id: Long): Observable<List<MintLocation>>

    @Query("SELECT * FROM mintLocation WHERE idTrack = :id ORDER BY segment DESC LIMIT 1")
    fun getLastLocationByTrackId(id: Long): Single<MintLocation>

    @Query("SELECT COUNT(id) idMint FROM mintlocation")
    fun getCount(): Int

}
