package com.example.blechwikidk

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log.d
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.databinding.FundstellentextsearchBinding
import com.example.blechwikidk.model.EGTextsearchMod
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.MenuLib
import com.example.blechwikidk.util.SessionLib
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.util.*


class EGLiederTextSearch : AppCompatActivity() {
    private lateinit var binding: FundstellentextsearchBinding

    private lateinit var vEGFundstellenList: List<EGTextsearchMod>
    private lateinit var searchterms: List<String>
    private val mainScope = MainScope()
    private var vText = ""
    private var vIx = 0
    private var vadapterposition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FundstellentextsearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_blue)
            //setHomeAsUpIndicator(R.drawable.menu)
            val localclassname = localClassName
            title = localclassname
        }

        binding.fundstellentextsearchProgressBar.visibility = View.GONE


        setListeners()
        }

    private fun setListeners() {
        binding.fundstellentextsearchBtnGo.setOnClickListener {

            binding.fundstellentextsearchTvPattern.text = ""
            extractsearchterms()
            fetchdataFromURL()
        }

        binding.fundstellentextsearchTvPattern.setOnClickListener {

            val intent = Intent(this, EGLiederText::class.java)
            intent.putExtra("vIx", vIx.toString())
            startActivity(intent)
        }

        binding.fundstellentextsearchNavView.setNavigationItemSelectedListener {
            binding.EGDrawerLayout.closeDrawers()

            val intent = MenuLib.mainmenueIntent( it, this)
            startActivity(intent)

            true
        }
}

    private fun extractsearchterms() {
        var searchtermfull = binding.fundstellentextsearchEditviewSearch.text.toString().replace(" ", "+")

        searchtermfull = searchtermfull.dropLastWhile { !it.isLetter() }
        val st = searchtermfull.split("+")

        searchterms = if(st.size > 3) {
            SessionLib.dialoganzeigen(this,"mehr als 3 Begriffe geht nicht","verstanden","zuviel Wörter")
            st.dropLast(st.size-3)
        }else{
            st
        }
        val index2 = searchtermfull.indexOf(searchterms[st.size-1],0,false) + searchterms[st.size-1].length
        searchtermfull = searchtermfull.substring(0,index2)
        binding.fundstellentextsearchEditviewSearch.setText(searchtermfull)

        d("Dieter", "extractsearchterms $searchterms")
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun fetchtextpattern(vIx: Int) {
        // asynchronously read html from url
        // io dispatcher for background thread network operation
        var uRLString = ""
        when (searchterms.size) {
            1 -> uRLString =
                SessionLib.RestBasisURL + "EGText?EGIx=$vIx&searchterm=${searchterms[0]}"
            2 -> uRLString =
                SessionLib.RestBasisURL + "EGText?EGIx=$vIx&searchterm=${searchterms[0]}&searchterm2=${searchterms[1]}"
            3 -> uRLString =
                SessionLib.RestBasisURL + "EGText?EGIx=$vIx&searchterm=${searchterms[0]}&searchterm2=${searchterms[1]}&searchterm3=${searchterms[2]}"

        }
        d("Dieter", "fetchdatafromurl $uRLString")

        val json = mainScope.async(Dispatchers.IO) {
            try {
                URL(uRLString).readText(charset("UTF-8"))

            } catch (e: IOException) {
                // catch exception while reading it
                "Error....\n\n$e"
            }
        }

        //---
        // main dispatcher for interacting with ui views
        mainScope.launch(Dispatchers.Main) {
            try {
                //d("Dieter","json: ${json.await()}")

                vText = Gson().fromJson(json.await(), String::class.java).toString()
                vText = URLDecoder.decode(vText, "UTF-8")

                binding.fundstellentextsearchTvPattern.text = Fundstellen.getspannable(vText,searchterms)

            } catch (e: Exception) {
                SessionLib.dialoganzeigen(
                    this@EGLiederTextSearch,
                    "$e",
                    "verstanden",
                    "Fehler! fetchdataFromURL!"
                )
                binding.fundstellentextsearchProgressBar.visibility = View.GONE
            }
        }
    }

    //------------------------------------------------
    private fun fetchdataFromURL() {
        // asynchronously read html from url
        // io dispatcher for background thread network operation
        val uRLString: String = when (searchterms.size) {
            1 -> SessionLib.RestBasisURL + "EGText?searchterm=${searchterms[0]}"
            2 -> SessionLib.RestBasisURL + "EGText?searchterm=${searchterms[0]}&searchterm2=${searchterms[1]}"
            3 -> SessionLib.RestBasisURL + "EGText?searchterm=${searchterms[0]}&searchterm2=${searchterms[1]}&searchterm3=${searchterms[2]}"
            else -> {
                SessionLib.RestBasisURL + "EGText?searchterm=${searchterms[0]}&searchterm2=${searchterms[1]}&searchterm3=${searchterms[2]}"
            }
        }

        d("Dieter", "fetchdatafromurl $uRLString")

        val json = mainScope.async(Dispatchers.IO) {
            try {
                URL(uRLString).readText(charset("UTF-8"))

            } catch (e: IOException) {
                // catch exception while reading it
                "Error....\n\n$e"
            }
        }

        //---
        // main dispatcher for interacting with ui views
        mainScope.launch(Dispatchers.Main) {
            try {

                //Log.d("Dieter", " ${json.await()}")
                vEGFundstellenList =
                    Gson().fromJson(json.await(), Array<EGTextsearchMod>::class.java).toList()
                setUI()

            } catch (e: Exception) {
                SessionLib.dialoganzeigen(
                    this@EGLiederTextSearch,
                    "$e",
                    "verstanden",
                    "Fehler! fetchdataFromURL!"
                )
                binding.fundstellentextsearchProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setUI() {
        d("Dieter", "setUI $vEGFundstellenList")
        binding.fundstellentextsearchProgressBar.visibility = View.GONE

        binding.fundstellentextsearchRc.layoutManager = LinearLayoutManager(this)
        binding.fundstellentextsearchRc.adapter =
            InnerFundstellentextsearchAdapter(vEGFundstellenList.sortedBy { it.rank })


    }
    //---------------------------------------------------------
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.option_menu_eg, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        binding.EGDrawerLayout.openDrawer(GravityCompat.START)
        binding.fundstellentextsearchNavView.setCheckedItem(R.id.actionEGTextsearch)
        return true
    }

    //----------------------------------------------------------------------------
    //----------------------------------------------------------------------------

    inner class InnerFundstellentextsearchAdapter(private val searchlist: List<EGTextsearchMod>) :
        RecyclerView.Adapter<InnerFundstellentextsearchAdapter.ViewHolder>() {


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): InnerFundstellentextsearchAdapter.ViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fundstellentextsearch_row, parent, false)

            val vholder = ViewHolder(view)

            view.setOnClickListener {
                vIx = searchlist[vholder.adapterPosition].ix
                vadapterposition = vholder.adapterPosition
                fetchtextpattern(vIx)
                notifyDataSetChanged()

                //lastadapterposition = vholder.adapterPosition

            }

            return vholder
        }

        override fun getItemCount(): Int = searchlist.size

        override fun onBindViewHolder(
            holder: InnerFundstellentextsearchAdapter.ViewHolder,
            position: Int
        ) {
            d("Dieter","onbindviewholder $position")
            val vFundstelle = searchlist[position]
            holder.vRANK.text = vFundstelle.rank.toString()
            holder.vLied.text = vFundstelle.lied
            holder.vIx.text = vFundstelle.ix.toString()
            holder.vIxUr.text = vFundstelle.ixUr.toString()
            if(vadapterposition==position){
                holder.vLied.setBackgroundColor(Color.GREEN)
            }else{
                holder.vLied.setBackgroundColor(Color.WHITE)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val vRANK: TextView = itemView.findViewById(R.id.fundstellentextsearch_Rank)
            val vLied: TextView = itemView.findViewById(R.id.fundstellentextsearch_Lied)
            val vIx: TextView = itemView.findViewById(R.id.fundstellentextsearch_Ix)
            val vIxUr: TextView = itemView.findViewById(R.id.fundstellentextsearch_IxUr)
        }
    }

}