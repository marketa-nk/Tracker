package com.mint.minttracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mint.minttracker.models.MintLocation
import io.reactivex.Single

@Dao
interface MintLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMintLocation(mintLocation: MintLocation): Single<Long>

    @Query("SELECT * FROM mintLocation")
    fun getAllRecords(): List<MintLocation>

    @Query("SELECT * FROM mintLocation WHERE idTrack = :id")
    fun getAllRecordsByID(id: Long): Single<List<MintLocation>>

    @Query("SELECT * FROM mintLocation WHERE idTrack = :id")
    fun getLastRecordByTrackId(id: Long): Single<MintLocation>

    @Query("SELECT COUNT(id) idMint FROM mintlocation")
    fun getCount(): Int

}
