package com.example.blechwikidk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blechwikidk.databinding.FundstellenBinding
import com.example.blechwikidk.model.FundstellenRecyclerMod
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.Fundstellen.loadData
import com.example.blechwikidk.util.SessionLib
import kotlinx.coroutines.*

class TitelFundstellen : AppCompatActivity() {

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
        //-----------------
        val vIx = intent.getStringExtra("Ix")
        val uRLString = SessionLib.RestBasisURL + "Titel/" + vIx

        fetchData(uRLString)
        setUI()

        binding.FundstellenTextViewWeitere.setOnClickListener {
            Fundstellen.fundstellenAlleAnzeigen(binding.FundstellenRecyclerView,vTitelListmapped,binding.FundstellenTextViewWeitere,"Buch")
        }
        binding.fundstellenBtnInfo.visibility = View.GONE
    }

    private fun fetchData(uRLString:String)= runBlocking {
        val deferred: Deferred< List<FundstellenRecyclerMod>> = mainScope.async(Dispatchers.IO)  {
            loadData(uRLString)
        }
        vTitelListmapped = deferred.await()
        //Log.d("Dieter", "Ende fetchData: ")
    }

    private fun setUI() {
        val vTitel = intent.getStringExtra("Titel")
        val rc = binding.FundstellenRecyclerView

        //-------------------------
        Fundstellen.fundstellentaggen(vTitelListmapped)

        rc.layoutManager =
            LinearLayoutManager(this@TitelFundstellen)

        if (Fundstellen.fundstellenFilter == "meine") {
            //nurmeineFundstellenAnzeigen()
            Fundstellen.fundstellenNurmeineAnzeigen(
                rc,
                vTitelListmapped,
                binding.FundstellenTextViewWeitere, "Buch"
            )
        } else {
            Fundstellen.fundstellenAlleAnzeigen(
                rc,
                vTitelListmapped,
                binding.FundstellenTextViewWeitere,
                "Buch"
            )
        }
        binding.FundstellenTextViewHeadline.text = vTitel
        binding.FundstellenProgressBar.visibility = View.GONE
    }
}