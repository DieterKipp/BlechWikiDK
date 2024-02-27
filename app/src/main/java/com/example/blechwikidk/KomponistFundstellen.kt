package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blechwikidk.databinding.FundstellenBinding
import com.example.blechwikidk.model.FundstellenRecyclerMod
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.SessionLib
import kotlinx.coroutines.*

class KomponistFundstellen : AppCompatActivity() {

    private lateinit var binding: FundstellenBinding

    private var sort = "Buch"
    private var filterstatus = "alle"
    private lateinit var vTitelListmapped: List<FundstellenRecyclerMod>
    private val mainScope = MainScope()
    private var komponist = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FundstellenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            val localclassname = localClassName
            title = localclassname
        }
        //---------------------
        sort = SessionLib.getPref("Komponistensort")
        when (sort) {
            "noname" -> {
                SessionLib.setPref("Komponistensort", "Buch")
                sort = "Buch"
            }
        }

        komponist = intent.getStringExtra("Komponist")!!

        val vid = intent.getStringExtra("id")
        val uRLString = SessionLib.RestBasisURL + "Komponist/" + vid

        //------------------------------

        fetchData(uRLString)
        setUI()
        setListeners()
    }

    private fun setListeners() {
        //---------------------------------
        binding.FundstellenTextViewWeitere.setOnClickListener {
            Fundstellen.fundstellenAlleAnzeigen(binding.FundstellenRecyclerView,vTitelListmapped,binding.FundstellenTextViewWeitere, sort)
            filterstatus = "alle"
        }
        binding.fundstellenBtnInfo.setOnClickListener {

            val urlstring = "http://www.google.com/search?q=$komponist"
            val intent = Intent(it.context, WebViewer::class.java)

            intent.putExtra("browseURL", urlstring)

            startActivity(intent)
        }
    }

    private fun fetchData(uRLString:String)= runBlocking {
        val deferred: Deferred< List<FundstellenRecyclerMod>> = mainScope.async(Dispatchers.IO)  {
            Fundstellen.loadData(uRLString)
        }
        vTitelListmapped = deferred.await()
        //Log.d("Dieter", "Ende fetchData: ")
    }

    private fun setUI() {
        val rc = binding.FundstellenRecyclerView

        //-------------------------
        Fundstellen.fundstellentaggen(vTitelListmapped)

        rc.layoutManager =  LinearLayoutManager(this@KomponistFundstellen)

        if (Fundstellen.fundstellenFilter == "meine") {
            filterstatus = "nurmeine"
            Fundstellen.fundstellenNurmeineAnzeigen(rc, vTitelListmapped,binding.FundstellenTextViewWeitere,sort
            )

        } else {
            filterstatus = "alle"
            Fundstellen.fundstellenAlleAnzeigen(rc,vTitelListmapped,binding.FundstellenTextViewWeitere,sort
            )
        }
        binding.FundstellenTextViewHeadline.text = komponist
        binding.FundstellenProgressBar.visibility = View.GONE
    }

    //------------------------------------------------
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val item = menu.findItem(R.id.actionKomponistFundstelleTogglesort)
        if (sort =="Buch"){
            item?.setIcon(R.drawable.ic_alpha_green)
        }else{
            item?.setIcon(R.drawable.ic_alpha_yellow)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.actionKomponistFundstelleTogglesort -> {
                togglesort(item)
                return true
            }
            else -> {
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_komponist_fundstellen, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun togglesort(item: MenuItem) {
        if (sort == "Titel") {
            sort = "Buch"
            item.setIcon(R.drawable.ic_alpha_green)
            SessionLib.setPref("Komponistensort","Buch")
        } else {
            sort = "Titel"
            item.setIcon(R.drawable.ic_alpha_yellow)
            SessionLib.setPref("Komponistensort","Titel")
        }
        if (filterstatus == "alle") {
            Fundstellen.fundstellenAlleAnzeigen(binding.FundstellenRecyclerView,vTitelListmapped,binding.FundstellenTextViewWeitere,sort
            )

        } else {
            Fundstellen.fundstellenNurmeineAnzeigen(binding.FundstellenRecyclerView,vTitelListmapped,binding.FundstellenTextViewWeitere,sort
            )

        }
        Toast.makeText(this, "!sortiert nach $sort!", Toast.LENGTH_LONG).show()
    }
}