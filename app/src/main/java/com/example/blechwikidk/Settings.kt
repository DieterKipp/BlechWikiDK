package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.databinding.SettingsBinding
import com.example.blechwikidk.model.EGLKAMod
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate

class Settings : AppCompatActivity() {

    private lateinit var binding: SettingsBinding

    var vLKA = ""
    var oncreateflag = "true"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        SessionLib.setInsets(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_menu_blue)
            val localclassname = localClassName
            title = localclassname
            //setSupportActionBar(toolbar)
        }
        //------------------

        setListeners()

        spinnerinitialisieren()
        if (Fundstellen.fundstellenFilter == "meine") {
            binding.settingsSwitchMeineBuecher.isChecked = true
        }
        if (SessionLib.getPref("dietersBuecherVomServer") == "ja") {
            binding.settingsSwitchDietersBuecher.isChecked = true
        }

        oncreateflag = "false"


        binding.settingsSharedPrefs.layoutManager = LinearLayoutManager(this@Settings)


        //-----------
        //Altlasten
        SessionLib.sharedPref.edit { remove("nurFundstellenMeineBücher") }
        SessionLib.sharedPref.edit { remove("FundstellennurMeineBücher") }
        //-----------

        setRCAdapter()

    }

    private fun setRCAdapter() {
        //---------------------
        val settingsList = SessionLib.sharedPref.all.toList()

        binding.settingsSharedPrefs.adapter = SettingsTabelleAdapter(settingsList)
    }

    private fun setListeners() {

        binding.updateBtn.setOnClickListener {
            intent = Intent(this, Update::class.java)
            intent.putExtra("versionCodefromServer",99)
            intent.putExtra("versionNamefromServer","kein")
            startActivity(intent)
        }

        binding.sharelinkBtn.setOnClickListener {
            val filename = "${SessionLib.IDENT}/${SessionLib.IDENT}.apk"
            val apkUrl = "http://pcportal.ddns.net/downloads/apk/$filename"

            val intent = Intent(Intent.ACTION_SEND)
            //intent.setPackage("com.whatsapp")
            intent.type = "text/plain"
            //------------
            intent.putExtra(Intent.EXTRA_SUBJECT, "der link zum download der App...")
            intent.putExtra(Intent.EXTRA_TEXT, apkUrl)

            intent.putExtra(Intent.EXTRA_TITLE, SessionLib.IDENT)

            //d("Dieter", "$vURL")
            startActivity(Intent.createChooser(intent, "link teilen für ${SessionLib.IDENT}"))
        }

        // Set a checked change listener for switch button
        binding.settingsSwitchMeineBuecher.setOnCheckedChangeListener { _, isChecked ->
            if (oncreateflag == "false") {
                if (isChecked) {
                    // The switch is enabled/checked

                    //d("Dieter", "The switch is enabled/checked")
                    //binding.settingsSwitchMeineBuecher.background
                    SessionLib.setPref("FundstellenFilter", "meine")

                } else {
                    // The switch is disabled
                    //d("Dieter", "The switch is disabled")
                    //binding.settingsSwitchMeineBuecher.backgroundColor = Color.TRANSPARENT
                    SessionLib.setPref("FundstellenFilter", "alle")
                }
            }

            Fundstellen.fundstellenFilter = SessionLib.getPref("FundstellenFilter")
            setRCAdapter()
        }
        binding.settingsSwitchDietersBuecher.setOnCheckedChangeListener { _, isChecked ->


            if (oncreateflag == "false") {
                if (isChecked) {
                    // The switch is enabled/checked
                    SessionLib.setPref("dietersBuecherVomServer", "ja")

                } else {
                    // The switch is disabled
                    SessionLib.setPref("dietersBuecherVomServer", "nein")
                }
            SessionLib.setPref("BuchChangeCounter","0")
            TableUpdate.checkforTableupdateBuecher()
            }

        }
    }


    private fun spinnerinitialisieren() {
        val vLKAgelesen = SessionLib.getPref("EGLKA")

        val arraylist = arrayListOf<EGLKAMod>()

        arraylist.add(EGLKAMod("BEL", "Baden/Elsaß/Lothringen"))
        arraylist.add(EGLKAMod("BT", "Bayern/Thüringen"))
        arraylist.add(EGLKAMod("HN", "Hessen-Nassau"))
        arraylist.add(EGLKAMod("KW", "Kurhessen-Waldeck"))
        arraylist.add(EGLKAMod("M", "Mecklenburg"))
        arraylist.add(EGLKAMod("N", "Nordelbien"))
        arraylist.add(EGLKAMod("NB", "Niedersachsen/Bremen"))
        arraylist.add(EGLKAMod("Ö", "Österreich"))
        arraylist.add(EGLKAMod("OL", "Oldenburg"))
        arraylist.add(EGLKAMod("P", "Pfalz"))
        arraylist.add(EGLKAMod("R", "Reformierte"))
        arraylist.add(EGLKAMod("RWL", "Rheinland/Westfalen/Lippe"))
        arraylist.add(EGLKAMod("W", "Württemberg"))
        arraylist.add(EGLKAMod("alle", "alle"))

        val vLKAList = mutableListOf<String>()
        var vPosition = 0
        for ((i, element) in arraylist.withIndex()) {
            vLKAList.add(element.lkalang)
            if (element.lka == vLKAgelesen) {
                vPosition = i
            }
        }

        val adapter =
            //ArrayAdapter(this@Settings, android.R.layout.simple_spinner_item, LKAList)
            ArrayAdapter(this@Settings, R.layout.settings_spinner_item, vLKAList)
        binding.SettingsLKASpinner.adapter = adapter
        adapter.setDropDownViewResource(R.layout.settings_spinner_dropdown)
        binding.SettingsLKASpinner.setSelection(vPosition)

        binding.SettingsLKASpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    //val vLKAlang = vLKAList[position]
                    if (oncreateflag == "false") {
                        if (vLKA == "") {
                            //den ersten Aufruf bei oncreate nicht in die DB schreiben
                            vLKA = arraylist[position].lka
                        } else {
                            vLKA = arraylist[position].lka
                            d("Dieter", "LKA: $vLKA")
                            SessionLib.setPref("EGLKA", arraylist[position].lka)
                            Toast.makeText(this@Settings,"erfolgreich eingetragen!",Toast.LENGTH_LONG).show()

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                }
            }


    }

    //----------------------------------------------
    inner class SettingsTabelleAdapter(private val settingsList: List<Pair<String, Any?>>) : RecyclerView.Adapter<SettingsTabelleAdapter.ViewHolder>()  {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.settings_tabelle_row, parent, false)



            return ViewHolder(view)

        }

        override fun getItemCount(): Int = settingsList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val vsetting = settingsList[position]
            holder.vKey.text = vsetting.first
            holder.vValue.text = vsetting.second.toString()

        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val vKey: TextView = itemView.findViewById(R.id.settings_tabelle_key_textView)
            val vValue: TextView = itemView.findViewById(R.id.settings_tabelle_value_textView)

        }


    }
}
