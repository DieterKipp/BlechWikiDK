package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.blechwikidk.database.BuecherFromDB
import com.example.blechwikidk.databinding.BuchDetailsBinding
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.SessionLib

class BuchDetails: AppCompatActivity()  {

    private lateinit var binding: BuchDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BuchDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            val localclassname = localClassName
            title = localclassname
        }

        val vBuchId = intent.getStringExtra("BuchId")

        val dataListFromDB: List<BuecherFromDB> = DBLib.DatabaseInstance.buecherDAO().pickone(vBuchId!!.toInt())

        binding.BuchDetailsTextViewBuch.text = dataListFromDB[0].buch
        binding.BuchDetailsTextViewKurz.text = dataListFromDB[0].buchkurz
        binding.BuchDetailsTextViewErscheinjahr.text = dataListFromDB[0].erscheinjahr
        binding.BuchDetailsTextViewHerausgeberVorname.text = dataListFromDB[0].herausgvorname
        binding.BuchDetailsTextViewHerausgeber.text = dataListFromDB[0].herausgeber
        binding.BuchDetailsTextViewUntertitel.text = dataListFromDB[0].untertitel
        binding.BuchDetailsTextViewVerlag.text = dataListFromDB[0].verlag
        binding.BuchDetailsTextViewVerlagsnummer.text = dataListFromDB[0].verlagsnummer
        binding.BuchDetailsTextViewZulieferung.text = dataListFromDB[0].zulieferung
        binding.BuchDetailsTextViewRelevanz.text = dataListFromDB[0].relevanz.toString()


        val vpath  = SessionLib.BasisURLPicBuch + "mini240/" + dataListFromDB[0].buchkurz + ".jpg"
        binding.BuchDetailsImageView.load(vpath)

        binding.BuchDetailsImageView.setOnClickListener {
            val urlstring = SessionLib.BasisURLPicBuch +  dataListFromDB[0].buchkurz + ".jpg"
            val intent = Intent(it.context, WebViewer::class.java)

            intent.putExtra("browseURL", urlstring)

            startActivity(intent)
        }

    }
}