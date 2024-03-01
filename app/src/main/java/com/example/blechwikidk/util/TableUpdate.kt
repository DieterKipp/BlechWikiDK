package com.example.blechwikidk.util

import android.util.Log
import com.example.blechwikidk.database.BuecherFromDB
import com.example.blechwikidk.database.EGLiederFromDB
import com.example.blechwikidk.database.KomponistFromDB
import com.example.blechwikidk.database.TitelFromDB
import com.example.blechwikidk.model.BuchFromURL
import com.example.blechwikidk.model.EGLiedMod
import com.example.blechwikidk.model.KomponistFromURL
import com.example.blechwikidk.model.TitelFromURL
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

object TableUpdate {

    private val mainScope = MainScope()
    private var dietersBuecherVomServer = "ja"

    fun checkforTableupdateTitel() {

        if (SessionLib.isOnline == "true") {
            val resultAsync = mainScope.async(Dispatchers.IO) {
                getRESTDataTitel()
            }
            mainScope.launch(Dispatchers.Main) {
                if(resultAsync.await()=="fertig"){
                    //setUI(ctx)
                    Log.d("Dieter", "checkforTableupdatesTitel ende")
                }
            }
        }
    }

    fun checkforTableupdateKomponisten() {

        if (SessionLib.isOnline == "true") {
            val resultAsync = mainScope.async(Dispatchers.IO) {
                getRESTDataKomponisten()
            }
            mainScope.launch(Dispatchers.Main) {
                if(resultAsync.await()=="fertig"){
                    //setUI(ctx)
                    Log.d("Dieter", "checkforTableupdatesKomponisten ende")
                }
            }
        }
    }

    fun checkforTableupdateBuecher() {

        if (SessionLib.isOnline == "true") {
            val resultAsync = mainScope.async(Dispatchers.IO) {
                getRESTDataBuecher()
            }
            mainScope.launch(Dispatchers.Main) {
                if(resultAsync.await()=="fertig"){
                    //setUI(ctx)
                    Log.d("Dieter", "checkforTableupdatesBuecher ende")
                }
            }
        }
    }

    fun getRESTDataEG():String {
        Log.d("Dieter", "checkforTableupdatesEG beginn")

        val vURL = "${SessionLib.RestBasisURL}EG"
        val json =
            URL(vURL).readText(charset("UTF-8"))


        val jsonList = Gson().fromJson(json, Array<EGLiedMod>::class.java).toList()


        //Liste mappen für das insert statement
        val tableList = jsonList.map {
            EGLiederFromDB(
                null,
                it.ix,
                it.ixUr,
                it.nr,
                it.nr2,
                it.lied,
                it.anlass,
                it.egTeil
            )
        }

        DBLib.DatabaseInstance.egliederDAO().deleteAll()
        DBLib.DatabaseInstance.egliederDAO().insertAll(tableList)


        SessionLib.setPref("EGLiedergelesen", "true")

        return "fertig"
    }

    private fun getRESTDataTitel():String {
        Log.d("Dieter", "checkforTableupdatesTitel beginn")
        val changecounterstring = SessionLib.getPref("TitelChangeCounter")
        val changecounter = if (changecounterstring == "noname") {
            0
        } else {
            changecounterstring.toInt()
        }

        val vURL = "${SessionLib.RestBasisURL}Version?Tabelle=Titel&counter=$changecounter"
        val json =
            URL(vURL).readText(charset("UTF-8"))

        val jsonList = Gson().fromJson(json, Array<TitelFromURL>::class.java).toList()

        if (jsonList.isNotEmpty()) {

            val maxchangecounter =
                jsonList.maxByOrNull { it.changecounter }?.changecounter ?: 0
            //d("Dieter","$maxchangecounter")

            if (changecounter == 0) {
                //Liste mappen für das insert statement
                val tableList =
                    jsonList.map { TitelFromDB(null, it.titelohneKomma, it.titel, it.ix) }

                DBLib.DatabaseInstance.titelDAO().deleteAll()
                DBLib.DatabaseInstance.titelDAO().insertAll(tableList)

            } else {

                val tableListinsert = jsonList.filter { it.change == "new" }
                    .map { TitelFromDB(null, it.titelohneKomma, it.titel, it.ix) }
                val tableListdelete =
                    jsonList.filter { it.change == "delete" }.map { it.ix }

                //d("Dieter","$tableListinsert")
                DBLib.DatabaseInstance.titelDAO().insertAll(tableListinsert)
                DBLib.DatabaseInstance.titelDAO().deletebyListofIx(tableListdelete)
            }

            if (maxchangecounter != 0) {
                SessionLib.setPref(
                    "TitelChangeCounter",
                    maxchangecounter.toString()
                )
            }
        }
        return "fertig"
    }

    private fun getRESTDataKomponisten():String {
        Log.d("Dieter", "checkforTableupdatesKomponisten beginn")
        val changecounterstring = SessionLib.getPref("KomponistChangeCounter")
        val changecounter = if (changecounterstring == "noname") {
            0
        } else {
            changecounterstring.toInt()
        }

        val vURL = "${SessionLib.RestBasisURL}Version?Tabelle=Komponist&counter=$changecounter"
        val json =
            URL(vURL).readText(charset("UTF-8"))

        val jsonList = Gson().fromJson(json, Array<KomponistFromURL>::class.java).toList()

        if (jsonList.isNotEmpty()) {

            val maxchangecounter =
                jsonList.maxByOrNull { it.changecounter }?.changecounter ?: 0

            if (changecounter == 0) {
                //Liste mappen für das insert statement
                val tableList =
                    jsonList.map {
                        KomponistFromDB(
                            null,
                            it.id,
                            it.kurz,
                            it.komponist,
                            it.friendlyKomponistName
                        )
                    }

                DBLib.DatabaseInstance.komponistDAO().deleteAll()
                DBLib.DatabaseInstance.komponistDAO().insertAll(tableList)

            } else {

                val tableListinsert = jsonList
                    .filter { it.change == "new" }
                    .map {
                        KomponistFromDB(
                            null,
                            it.id,
                            it.kurz,
                            it.komponist,
                            it.friendlyKomponistName
                        )
                    }
                val tableListdelete =
                    jsonList.filter { it.change == "delete" }.map { it.id }
                val tableListupdate = jsonList
                    .filter { it.change == "update" }
                    .map {
                        KomponistFromDB(
                            null,
                            it.id,
                            it.kurz,
                            it.komponist,
                            it.friendlyKomponistName
                        )
                    }

                if (tableListinsert.isNotEmpty()) {
                    DBLib.DatabaseInstance.komponistDAO().insertAll(tableListinsert)
                }
                if (tableListdelete.isNotEmpty()) {
                    DBLib.DatabaseInstance.komponistDAO().deletebyListofId(tableListdelete)
                }
                if (tableListupdate.isNotEmpty()) {
                    //DBLib.DatabaseInstance.komponistDAO().update(tableListupdate)

                    for (i in tableListupdate.indices) {
                        DBLib.DatabaseInstance.komponistDAO().update(
                            tableListupdate[i].id,
                            tableListupdate[i].kurz,
                            tableListupdate[i].komponist,
                            tableListupdate[i].friendlyKomponistName
                        )
                    }
                }
            }

            if (maxchangecounter != 0) {
                SessionLib.setPref(
                    "KomponistChangeCounter",
                    maxchangecounter.toString()
                )
            }
        }
        return "fertig"
    }

    private fun getRESTDataBuecher():String {
        Log.d("Dieter", "checkforTableupdatesBuecher beginn")
        val changecounterstring = SessionLib.getPref("BuchChangeCounter")
        val changecounter = if (changecounterstring == "noname") {
            0
        } else {
            changecounterstring.toInt()
        }

        val vURL = "${SessionLib.RestBasisURL}Version?Tabelle=Buch&counter=$changecounter"
        val json =
            URL(vURL).readText(charset("UTF-8"))

        val jsonList = Gson().fromJson(json, Array<BuchFromURL>::class.java).toList()

        if (jsonList.isNotEmpty()) {

            val maxchangecounter =
                jsonList.maxByOrNull { it.changecounter }?.changecounter ?: 0


            if (changecounter == 0) {
                //Liste mappen für das insert statement

                val tableList = if (dietersBuecherVomServer == "ja") {
                    jsonList.map {
                        BuecherFromDB(
                            null,
                            it.buchId,
                            it.buchkurz,
                            it.buch,
                            it.untertitel,
                            it.erscheinjahr,
                            it.herausgeber,
                            it.herausgvorname,
                            it.verlag,
                            it.verlagsnummer,
                            it.zulieferung,
                            it.relevanz,
                            it.vorhanden
                        )
                    }
                } else {
                    jsonList.map {
                        BuecherFromDB(
                            null,
                            it.buchId,
                            it.buchkurz,
                            it.buch,
                            it.untertitel,
                            it.erscheinjahr,
                            it.herausgeber,
                            it.herausgvorname,
                            it.verlag,
                            it.verlagsnummer,
                            it.zulieferung,
                            it.relevanz,
                            ""
                        )
                    }
                }


                DBLib.DatabaseInstance.buecherDAO().deleteAll()
                DBLib.DatabaseInstance.buecherDAO().insertAll(tableList)
                //todo tableList mit .Filter{it == "new"} übergeb? -- testen!!

            } else {


                val tableListinsert = if (dietersBuecherVomServer == "ja") {
                    jsonList
                        .filter { it.change == "new" }
                        .map {
                            BuecherFromDB(
                                null,
                                it.buchId,
                                it.buchkurz,
                                it.buch,
                                it.untertitel,
                                it.erscheinjahr,
                                it.herausgeber,
                                it.herausgvorname,
                                it.verlag,
                                it.verlagsnummer,
                                it.zulieferung,
                                it.relevanz,
                                it.vorhanden
                            )
                        }
                }else{
                    jsonList
                        .filter { it.change == "new" }
                        .map {
                            BuecherFromDB(
                                null,
                                it.buchId,
                                it.buchkurz,
                                it.buch,
                                it.untertitel,
                                it.erscheinjahr,
                                it.herausgeber,
                                it.herausgvorname,
                                it.verlag,
                                it.verlagsnummer,
                                it.zulieferung,
                                it.relevanz,
                                ""
                            )
                        }
                }

                val tableListdelete =
                    jsonList.filter { it.change == "delete" }.map { it.buchId }

                val tableListupdate = jsonList
                    .filter { it.change == "update" }
                    .map {
                        BuecherFromDB(
                            null,
                            it.buchId,
                            it.buchkurz,
                            it.buch,
                            it.untertitel,
                            it.erscheinjahr,
                            it.herausgeber,
                            it.herausgvorname,
                            it.verlag,
                            it.verlagsnummer,
                            it.zulieferung,
                            it.relevanz,
                            ""
                        )
                    }

                if (tableListinsert.isNotEmpty()) {
                    DBLib.DatabaseInstance.buecherDAO().insertAll(tableListinsert)
                }
                if (tableListdelete.isNotEmpty()) {
                    DBLib.DatabaseInstance.buecherDAO().deletebyListofId(tableListdelete)
                }
                if (tableListupdate.isNotEmpty()) {
                    for (i in tableListupdate.indices) {
                        DBLib.DatabaseInstance.buecherDAO().update(
                            tableListupdate[i].buchId,
                            tableListupdate[i].buchkurz,
                            tableListupdate[i].buch,
                            tableListupdate[i].untertitel,
                            tableListupdate[i].erscheinjahr,
                            tableListupdate[i].herausgeber,
                            tableListupdate[i].herausgvorname,
                            tableListupdate[i].verlag,
                            tableListupdate[i].verlagsnummer,
                            tableListupdate[i].zulieferung,
                            tableListupdate[i].relevanz
                        )
                    }
                }
            }

            if (maxchangecounter != 0) {
                SessionLib.setPref(
                    "BuchChangeCounter",
                    maxchangecounter.toString()
                )
            }
        }
        return "fertig"
    }

}