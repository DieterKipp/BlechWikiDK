package com.example.blechwikidk

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blechwikidk.databinding.BuecherBinding
import com.example.blechwikidk.model.BuchRCMod
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.MenuLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import java.util.Locale

class Buecher : AppCompatActivity() {

    private lateinit var binding: BuecherBinding

    private lateinit var dataListFromDB: List<BuchRCMod>

    private var buecherFilter = "alle"
    // meine oder alle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BuecherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_blue)
            val localclassname = localClassName
            title = localclassname
        }
        //------------------
        buecherFilter = SessionLib.getPref("BuecherFilter")
        //---------------------

        when (buecherFilter) {
            "noname" -> {
                SessionLib.setPref("BuecherFilter", "alle")
                buecherFilter = "alle"
            }
        }

        setListeners()
        readFromDB()
        //---------------------
    }

    private fun setListeners() {
        binding.buecherNavView.setNavigationItemSelectedListener {
            binding.BuchDrawerLayout.closeDrawers()

            val intent = MenuLib.mainmenueIntent(it, this)
            startActivity(intent)

            true
        }
        IAbstrHelper().setTextWatcher(binding.buecherSearchEditText)

        binding.buecherSearchDelete.setOnClickListener {

            binding.buecherSearchEditText.setText("")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    //--------------------------------------------------
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val item = menu.findItem(R.id.actionBesitzFilter)
        if (buecherFilter =="meine"){
            item?.setIcon(R.drawable.ic_filter_yellow)
        }else{
            item?.setIcon(R.drawable.ic_filter_gray)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.actionBüchertableupdate -> {
                binding.buecherProgressBar.visibility=View.VISIBLE
                Toast.makeText(this, "!ich lade jetzt die aktuelle Liste vom Server!", Toast.LENGTH_LONG).show()

                SessionLib.setPref("BuchChangeCounter", "0")
                TableUpdate.checkforTableupdateBuecher()
                binding.buecherSearchEditText.setText("")
                readFromDB()
                return true
            }
            R.id.actionBesitzFilter -> {
                togglefilter(item)
                return true
            }
            else -> {
                binding.BuchDrawerLayout.openDrawer(GravityCompat.START)
                binding.buecherNavView.setCheckedItem(R.id.actionBücher)
                return true
            }
        }
    }

    private fun togglefilter(item: MenuItem) {

        if(buecherFilter == "meine"){
            buecherFilter = "alle"
            item.setIcon(R.drawable.ic_filter_gray)
            SessionLib.setPref("BuecherFilter","alle")
        }else{
            buecherFilter = "meine"
            item.setIcon(R.drawable.ic_filter_yellow)
            SessionLib.setPref("BuecherFilter","meine")
        }

        readFromDB()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_buecher, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //------------------------------------------------

    private fun readFromDB() {
        Log.d("Dieter", "Buch ReadFromDB")

        dataListFromDB = if(buecherFilter=="meine"){
            DBLib.DatabaseInstance.buecherDAO().getAllmeineRCMod()
        }else{
            DBLib.DatabaseInstance.buecherDAO().getAllRCMod()
        }

        binding.buecherProgressBar.visibility = View.GONE

        binding.buecherRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@Buecher)
            adapter = BuecherAdapter(dataListFromDB.filter { it.buch.contains(binding.buecherSearchEditText.text,true)}.sortedBy { it.buch })
        }
    }
    //------------------------------------------------

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Log.d("Dieter", "onSaveInstanceState")
        outState.putCharSequence("search",binding.buecherSearchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.buecherRecyclerView.requestFocus()
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    inner class IAbstrHelper : AbstrHelper() {

        override fun afterontextChanged(suchstring: String) {
            binding.buecherRecyclerView.adapter = BuecherAdapter(dataListFromDB.filter { it.buch.contains(suchstring,true)}.sortedBy {it.buch.lowercase(Locale.ROOT)})
        }
    }
}
