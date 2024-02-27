package com.example.blechwikidk.util

import android.content.Context
import androidx.room.Room
import com.example.blechwikidk.database.AppDatabase

object DBLib {

    //muss in der MainActivity einmal durch Aufruf von getDBInstance aktiviert werden
    lateinit var DatabaseInstance: AppDatabase

    fun getDBInstance(ctx: Context, allowmainthreadtrue: String) {

        DatabaseInstance = if (allowmainthreadtrue == "true") {
            Room.databaseBuilder(
                ctx.applicationContext,
                AppDatabase::class.java, "database-BlechWiki"
            )
                .fallbackToDestructiveMigration()
                //.addMigrations(MIGRATION_10_11)
                .allowMainThreadQueries()
                .build()

        } else {
            Room.databaseBuilder(
                ctx.applicationContext,
                AppDatabase::class.java, "database-BlechWiki"
            )
                .fallbackToDestructiveMigration()
                //.addMigrations(MIGRATION_10_11)
                .build()
        }
    }

/*    fun settingswertlesen(settingskey: String): String {

        //Elvis operator (?:) wenn NULL dann ""
        return DatabaseInstance.settingsDAO().searchForOnekey(settingskey) ?: ""

    }*/

/*    fun settingswertschreiben(settingskey: String, settingswert: String): Boolean {

        val uid = DatabaseInstance.settingsDAO().getID(settingskey)

        //DatabaseInstance.settingsDAO().deleteWhere(settingskey)

        if (uid == 0) {
            //neuer Datensatz
            if (settingswert != "") {
                DatabaseInstance.settingsDAO()
                    .insertAll(SettingsFromDB(null, settingskey, settingswert))
            }
        } else {
            //update
            DatabaseInstance.settingsDAO()
                .update(SettingsFromDB(uid, settingskey, settingswert))
        }

        return true
    }*/

/*
    fun settingswertdelete(settingskey:String):Boolean{

        if(settingskey==""){
            DatabaseInstance.settingsDAO().deleteAll()
        }else{
            DatabaseInstance.settingsDAO().deleteWhere(settingskey)
        }
        return true
    }

 */


/*    private val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //database.execSQL("ALTER TABLE BuecherFromDB ADD COLUMN vorhanden String")
            database.execSQL("ALTER TABLE BuecherFromDB ADD COLUMN vorhanden Text NOT NULL DEFAULT ''")
        }
    }*/

/*    // Migration from 2 to 3, Room 2.2.0
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE new_Song (
                    id INTEGER PRIMARY KEY NOT NULL,
                    name TEXT,
                    tag TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent())
            database.execSQL("""
                INSERT INTO new_Song (id, name, tag)
                SELECT id, name, tag FROM Song
                """.trimIndent())
            database.execSQL("DROP TABLE Song")
            database.execSQL("ALTER TABLE new_Song RENAME TO Song")
        }
    }*/
}