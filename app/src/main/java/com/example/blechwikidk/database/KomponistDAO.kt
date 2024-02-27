package com.example.blechwikidk.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface KomponistDAO {

    @Query("SELECT * FROM KomponistFromDB")
    fun getAll(): List<KomponistFromDB>


    @Query("SELECT * FROM KomponistFromDB where friendlyKomponistName like :term")
    fun searchFor(term:String):List<KomponistFromDB>


    @Query("DELETE FROM KomponistFromDB")
    fun deleteAll()

    @Query("DELETE FROM KomponistFromDB  WHERE id  IN (:listofId)")
    fun deletebyListofId(listofId: List<Int>)

    @Insert
    fun insertAll(list: List<KomponistFromDB>)

    @Update
    fun update(list: List<KomponistFromDB>)
    //das room update funktioniert nur, wenn die uid in der Liste enthalten ist
    //deswegen handgestrickt mit der ID vom Server

    @Query("UPDATE KomponistFromDB SET kurz = :kurz, komponist = :komponist, friendlyKomponistName = :friendly WHERE (id = :id)")
    fun update(id:Int,kurz:String,komponist:String,friendly:String)
}