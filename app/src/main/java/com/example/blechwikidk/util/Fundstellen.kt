package com.example.blechwikidk.util

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.FundstellenAdapter1
import com.example.blechwikidk.FundstellenAdapter2
import com.example.blechwikidk.FundstellenAdapter3
import com.example.blechwikidk.FundstellenAdapter4
import com.example.blechwikidk.model.FundstellenMod
import com.example.blechwikidk.model.FundstellenRecyclerMod
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.net.URL

object Fundstellen {

    var fundstellenFilter = "alle"
    // meine oder alle

    fun loadData(uRLString:String): List<FundstellenRecyclerMod> {

        val json = URL(uRLString).readText(charset("UTF-8"))

        val vTitelList =  Gson().fromJson(json, Array<FundstellenMod>::class.java).toList()
        val vTitelListmapped = vTitelList.map {
            FundstellenRecyclerMod(
                it.buch,
                it.nr,
                it.titel,
                it.zus,
                it.komponist,
                it.besetzung,
                it.vorzeichen,
                it.titelZ,
                it.buchId,
                it.quellekurz,
                ""
            )
        }
        return vTitelListmapped
    }

    fun fundstellenNurmeineAnzeigen(rc: RecyclerView, vTitelListmapped:List<FundstellenRecyclerMod>, tv: TextView, group:String) {

        when(group){
            "Buch" -> rc.adapter = FundstellenAdapter1(vTitelListmapped.filter { it.vorhanden == "true" }.sortedBy { it.buch })
            "Titel" -> rc.adapter = FundstellenAdapter2(vTitelListmapped.filter { it.vorhanden == "true" }.sortedBy { it.titel })
        }

        val anzahlalle = vTitelListmapped.size
        val anzahlfilter = vTitelListmapped.filter { it.vorhanden == "true" }.size

        if (anzahlalle == anzahlfilter) {
            tv.visibility = View.GONE

        } else {
            val weitereFundstellen = anzahlalle - anzahlfilter
            val meldetext = "weitere $weitereFundstellen Fundstellen vorhanden"
            tv.visibility = View.VISIBLE
            tv.text = meldetext
        }
    }

    fun fundstellenAlleAnzeigen(rc: RecyclerView, vTitelListmapped:List<FundstellenRecyclerMod>, tv: TextView, group:String) {

        when(group){
            "Buch" -> rc.adapter = FundstellenAdapter1(vTitelListmapped.sortedBy { it.buch })
            "Titel" -> rc.adapter = FundstellenAdapter2(vTitelListmapped.sortedBy { it.titel })
            "Nr" -> rc.adapter = FundstellenAdapter3(vTitelListmapped.sortedBy { it.nr })
            "TitelBuch" -> rc.adapter = FundstellenAdapter4(vTitelListmapped.sortedBy { it.titel })
        }
        tv.visibility = View.GONE
    }

    fun fundstellentaggen(vTitelListmapped:List<FundstellenRecyclerMod>){
        val vmeineBuecherList = DBLib.DatabaseInstance.buecherDAO().getAllmeineRCMod()

        for (i in vTitelListmapped.indices) {
            val find = vmeineBuecherList.find { it.buchId == vTitelListmapped[i].buchId }
            //d("Dieter", "find:$find -- ${vTitelListmapped[i].BuchId}")
            if (find != null) {
                vTitelListmapped[i].vorhanden = "true"
            }
        }
    }

    fun getspannable(egText:String, seasrchtermList:List<String>): SpannableString {

        var start: Int
        var end: Int

        val spannableString = SpannableString(egText)
        val yellow1 = BackgroundColorSpan(Color.YELLOW)
        val yellow2 = BackgroundColorSpan(Color.RED)
        val yellow3 = BackgroundColorSpan(Color.GREEN)

        for (i in seasrchtermList.indices) {
            start = egText.indexOf(seasrchtermList[i], 0, true)
            end = start + seasrchtermList[i].length
            when (i) {
                0 -> spannableString.setSpan(
                    yellow1,
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                1 -> spannableString.setSpan(
                    yellow2,
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                2 -> spannableString.setSpan(
                    yellow3,
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        }
        return spannableString
    }
}
