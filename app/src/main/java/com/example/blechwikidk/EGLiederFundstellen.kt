package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blechwikidk.databinding.FundstellenBinding
import com.example.blechwikidk.model.FundstellenRecyclerMod
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.SessionLib
import kotlinx.coroutines.*

class EGLiederFundstellen : AppCompatActivity() {

    private lateinit var binding: FundstellenBinding

    private lateinit var vTitelListmapped: List<FundstellenRecyclerMod>
    private val mainScope = MainScope()

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

        val vIxUr = intent.getStringExtra("IxUr")
        val vIx = intent.getStringExtra("Ix")
        val uRLString = SessionLib.RestBasisURL + "EG/" + vIxUr

        fetchData(uRLString)
        setUI()
        setListeners(vIx)
    }

    private fun setListeners(vIx: String?) {
        binding.FundstellenTextViewWeitere.setOnClickListener {
            Fundstellen.fundstellenAlleAnzeigen(binding.FundstellenRecyclerView,vTitelListmapped,binding.FundstellenTextViewWeitere,"Buch")
        }

        binding.FundstellenTextViewHeadline.setOnClickListener {
            //d("Dieter","setonclicklistener")

            val intent = Intent(this@EGLiederFundstellen, EGLiederText::class.java)
            intent.putExtra("vIx", vIx!!.toString())
            startActivity(intent)
        }
        binding.fundstellenBtnInfo.setOnClickListener {
            val intent = Intent(this@EGLiederFundstellen, EGLiederText::class.java)
            intent.putExtra("vIx", vIx!!.toString())
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
        val vEGLied = intent.getStringExtra("Lied")
        val rc = binding.FundstellenRecyclerView

        //-------------------------
        Fundstellen.fundstellentaggen(vTitelListmapped)

        rc.layoutManager = LinearLayoutManager(this@EGLiederFundstellen)

        if (Fundstellen.fundstellenFilter == "meine") {
            //nurmeineFundstellenAnzeigen()
            Fundstellen.fundstellenNurmeineAnzeigen(rc,vTitelListmapped,binding.FundstellenTextViewWeitere,"Buch")
        } else {
            Fundstellen.fundstellenAlleAnzeigen(rc,vTitelListmapped,binding.FundstellenTextViewWeitere,"Buch")
        }

        binding.FundstellenTextViewHeadline.text = vEGLied

        binding.FundstellenProgressBar.visibility = View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_egfundstellen, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionEG_Liedtext -> {

                val vIx = intent.getStringExtra("Ix")
                val intent = Intent(this@EGLiederFundstellen, EGLiederText::class.java)
                intent.putExtra("vIx", vIx!!.toString())
                startActivity(intent)
            }
        }
        return true
    }
}