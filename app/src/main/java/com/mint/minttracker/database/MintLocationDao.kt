package com.mint.minttracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Track

@Dao
interface MintLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMintLocation(mintLocation: MintLocation)

    @Query("SELECT * FROM mintLocation")
    fun getAllRecords(): List<MintLocation>

    @Query("SELECT COUNT(idMint) idMint FROM mintlocation")
    fun getCount(): Int

}
