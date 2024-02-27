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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class BuecherFundstellen : AppCompatActivity() {

    private lateinit var binding: FundstellenBinding

    private lateinit var vTitelListmapped: List<FundstellenRecyclerMod>
    private var sort = "Nr"
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

        val vBuchId = intent.getStringExtra("BuchId")
        val uRLString = SessionLib.RestBasisURL + "Buch/" + vBuchId

        fetchData(uRLString)
        setUI()
        setListeners(vBuchId)


    }

    private fun setListeners(vBuchId: String?) {
        binding.FundstellenTextViewHeadline.setOnClickListener {
            //Log.d("Dieter", "setonclicklistener")

            val intent = Intent(this, BuchDetails::class.java)
            intent.putExtra("BuchId", vBuchId.toString())
            startActivity(intent)
        }
        binding.fundstellenBtnInfo.setOnClickListener {
            val intent = Intent(this, BuchDetails::class.java)
            intent.putExtra("BuchId", vBuchId.toString())
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
        val vBuch = intent.getStringExtra("Buch")
        //hier muss ich nicht auf meine Bücher taggen
        binding.FundstellenRecyclerView.layoutManager = LinearLayoutManager(this@BuecherFundstellen)
        Fundstellen.fundstellenAlleAnzeigen(
            binding.FundstellenRecyclerView,
            vTitelListmapped,
            binding.FundstellenTextViewWeitere,
            sort
        )

        binding.FundstellenTextViewHeadline.text = vBuch
        binding.FundstellenProgressBar.visibility = View.GONE
    }

    private fun togglesort(item: MenuItem) {
        if (sort == "Nr") {
            sort = "TitelBuch"
            item.setIcon(R.drawable.ic_alpha_yellow)
        } else {
            sort = "Nr"
            item.setIcon(R.drawable.ic_alpha_green)
        }
        Fundstellen.fundstellenAlleAnzeigen(
            binding.FundstellenRecyclerView,
            vTitelListmapped,
            binding.FundstellenTextViewWeitere,
            sort
        )
        Toast.makeText(this, "!sortiert nach $sort!", Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.actionBuchFundstelleTogglesort -> {
                togglesort(item)
                return true
            }
            else -> {
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_buch_fundstellen, menu)
        return super.onCreateOptionsMenu(menu)
    }
}