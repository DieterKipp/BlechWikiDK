package com.example.blechwikidk.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TitelDAO {

    @Query("SELECT * FROM TitelFromDB")
    fun getAll(): List<TitelFromDB>


    @Query("SELECT * FROM TitelFromDB where titelohneKomma like :term")
    fun searchFor(term:String):List<TitelFromDB>


    @Query("DELETE FROM TitelFromDB")
    fun deleteAll()

    @Query("DELETE FROM TitelFromDB WHERE ix  IN (:listofIx)")
    fun deletebyListofIx(listofIx:List<Int>)

    @Insert
    fun insertAll(list:List<TitelFromDB>)
}