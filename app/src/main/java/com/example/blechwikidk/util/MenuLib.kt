package com.example.blechwikidk.util

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.example.blechwikidk.*

object MenuLib {

    //muss in der MainActivity einmal durch Aufruf von 'MenuLib.ctx = this' aktiviert werden
    //lateinit var ctx: Context

    fun mainmenueIntent(vitem: MenuItem, ctx:Context): Intent {

        var intent = Intent(ctx, EGLieder::class.java)

        when (vitem.itemId) {
            R.id.actionKomponisten -> {
                intent = Intent(ctx, Komponist::class.java)
            }
            R.id.actionBücher -> {
                intent = Intent(ctx, Buecher::class.java)
            }
            R.id.actionTitel -> {
                intent = Intent(ctx, Titel::class.java)
            }
            R.id.actionEG -> {
                intent = Intent(ctx, EGLieder::class.java)
            }
            R.id.actionEGTextsearch -> {
                intent = Intent(ctx, EGLiederTextSearch::class.java)
            }
            R.id.actionSettings -> {
                intent = Intent(ctx, Settings::class.java)
            }
        }
        return intent
    }
}