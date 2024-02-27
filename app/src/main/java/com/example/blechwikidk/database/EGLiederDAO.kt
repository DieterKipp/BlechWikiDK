package com.example.blechwikidk.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EGLiederDAO {

    @Query("SELECT * FROM EGLiederFromDB")
    fun getAll(): List<EGLiederFromDB>


    @Query("SELECT * FROM EGLiederFromDB where (egTeil = 'Stamm' OR egTeil = :lka)")
    fun getbyLKA(lka:String):List<EGLiederFromDB>

    @Query("DELETE FROM EGLiederFromDB")
    fun deleteAll()

    @Insert
    fun insertAll(list: List<EGLiederFromDB>)
}