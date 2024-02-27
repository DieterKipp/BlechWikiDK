package com.example.blechwikidk.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.blechwikidk.model.BuchRCMod

@Dao
interface BuecherDAO {

    @Query("SELECT * FROM BuecherFromDB")
    fun getAll(): List<BuecherFromDB>

    @Query("SELECT buchId,buchkurz,buch,untertitel,vorhanden FROM BuecherFromDB")
    fun getAllRCMod(): List<BuchRCMod>

    @Query("SELECT * FROM BuecherFromDB where vorhanden = 'true' OR vorhanden = 'truetrue'")
    fun getAllmeineRCMod(): List<BuchRCMod>


    @Query("SELECT * FROM BuecherFromDB where buch like :term")
    fun searchFor(term:String):List<BuecherFromDB>

    @Query("SELECT * FROM BuecherFromDB where buchId = :term")
    fun pickone(term:Int):List<BuecherFromDB>


    @Query("DELETE FROM BuecherFromDB")
    fun deleteAll()


    @Query("DELETE FROM BuecherFromDB  WHERE buchId  IN (:listofId)")
    fun deletebyListofId(listofId:List<Int>)

    @Insert
    fun insertAll(list: List<BuecherFromDB>)


    //bei update bleibt das Feld 'vorhanden' unverändert

    @Query("UPDATE BuecherFromDB SET buchkurz = :buchkurz , buch = :buch ,untertitel= :untertitel ,erscheinjahr = :erscheinjahr ,herausgeber = :herausgeber,herausgvorname = :herausgvorname,verlag = :verlag,verlagsnummer = :verlagsnummer,zulieferung = :zulieferung,relevanz = :relevanz WHERE (buchId = :buchId)")
    fun update(
        buchId: Int,
        buchkurz: String,
        buch: String,
        untertitel: String,
        erscheinjahr: String,
        herausgeber: String,
        herausgvorname: String,
        verlag: String,
        verlagsnummer: String,
        zulieferung: String,
        relevanz: Int
    )

    @Query("UPDATE BuecherFromDB SET vorhanden = :vorhanden WHERE (buchId = :buchId)")
    fun updatebuchvorhanden(
        buchId: Int,
        vorhanden: String
    )
}