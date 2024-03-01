package com.example.blechwikidk

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log.d
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blechwikidk.database.EGLiederFromDB
import com.example.blechwikidk.databinding.EgliederBinding
import com.example.blechwikidk.model.EGLiedMod
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.MenuLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.Locale

class EGLieder : AppCompatActivity() {

    private lateinit var binding: EgliederBinding

    private lateinit var dataListFromDB: List<EGLiederFromDB>
    private lateinit var liederListmapped: List<EGLiedMod>
    private lateinit var dataListfiltered: List<EGLiedMod>

    private val mainScope= MainScope()

    private var sort: String = "abc"
    private var lka: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = EgliederBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_blue)
        }

        SessionLib.sharedPref = getPreferences(MODE_PRIVATE)
        //d("Dieter", SessionLib.isinitialized)

        //einmaliges initialisieren der Bibliotheken -- onlinestatus first!
        if(SessionLib.isinitialized=="false"){
            DBLib.getDBInstance(this, "true")
            SessionLib.getOnlineStatus(this)
            SessionLib.checkforNewVersion(this)
            val vEGLiedergelesen = SessionLib.getPref("EGLiedergelesen")
            if (vEGLiedergelesen != "true") {
                Toast.makeText(this, "!ich lade jetzt die aktuelle EG-Lieder Liste vom Server!", Toast.LENGTH_LONG).show()
                fetchEGLiederFromURL()
            }

            TableUpdate.checkforTableupdateTitel()
            TableUpdate.checkforTableupdateKomponisten()
            TableUpdate.checkforTableupdateBuecher()

            val fundstellenfilter = SessionLib.getPref("FundstellenFilter")
            if (fundstellenfilter=="noname")  {
                SessionLib.setPref("FundstellenFilter", "alle")
                Fundstellen.fundstellenFilter = "alle"
                }else{
                Fundstellen.fundstellenFilter = fundstellenfilter
                }


            SessionLib.isinitialized = "true"
        }
        //------
        val versiontext = "Version ${SessionLib.currentVersionName}"
        binding.VersionTextview.text = versiontext

        //------------------
        setListeners()
        //---------------------
        sort = SessionLib.getPref("EGsort")
        lka = SessionLib.getPref("EGLKA")
        //---------------------

        when (sort) {
            "noname" -> {
                SessionLib.setPref("EGsort", "abc")
                sort = "abc"
            }

        }

        if (lka == "noname") {
            SessionLib.setPref( "EGLKA", "RWL")
            lka = "RWL"
        }
        supportActionBar?.title="$localClassName ($lka)"
        readFromDB()
    }

    private fun setListeners() {

        binding.buecherNavView.setNavigationItemSelectedListener {
            binding.EGDrawerLayout.closeDrawers()

            val intent = MenuLib.mainmenueIntent( it, this)
            startActivity(intent)

            true
        }
        IAbstrHelper().setTextWatcher(binding.EGLiederSearchEditText)

        //---------------------
        binding.EGLiederSearchDelete.setOnClickListener {

            binding.EGLiederSearchEditText.setText("")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun fetchEGLiederFromURL()= runBlocking {
        // das muss ich in dieser Activity machen, damit das UI nach dem download aktualisiert werden kann (readfromDB)
        val deferred: Deferred<String> = mainScope.async(Dispatchers.IO)  {
            TableUpdate.getRESTDataEG()
        }
        val result = deferred.await()
        readFromDB()
        d("Dieter", "Ende fetchEGLiederFromURL  result $result: ")
    }

    //---------------------------------------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionEGSort_toggle -> {
                togglesort(item)
                return true
            }
            R.id.actionEGtableupdate -> {
                binding.EGLiederProgressbar.visibility = View.VISIBLE
                Toast.makeText(this, "!ich lade jetzt die aktuelle EG-Lieder Liste vom Server!", Toast.LENGTH_LONG).show()

                SessionLib.setPref("EGLiedergelesen", "")
                fetchEGLiederFromURL()
                binding.EGLiederSearchEditText.setText("")
                readFromDB()
                return true
            }

            else -> {
                binding.EGDrawerLayout.openDrawer(GravityCompat.START)
                binding.buecherNavView.setCheckedItem(R.id.actionEG)
                return true
            }
        }
    }

    private fun togglesort(item: MenuItem) {

        binding.EGLiederSearchEditText.setText("")
        if (sort == "abc") {
            sort = "Nr"
            item.setIcon(R.drawable.ic_list_numbered_yellow)
            SessionLib.setPref("EGsort","Nr")
        } else {
            sort = "abc"
            item.setIcon(R.drawable.ic_list_numbered_green)
            SessionLib.setPref("EGsort","abc")
        }
        setAdapter()
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val item = menu.findItem(R.id.actionEGSort_toggle)
        if (sort =="abc"){
            item?.setIcon(R.drawable.ic_list_numbered_green)
        }else{
            item?.setIcon(R.drawable.ic_list_numbered_yellow)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_eg, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("EGSort", sort)
        SessionLib.setPref( "EGsort", sort)
    }

    private fun readFromDB() {
        d("Dieter", "EGLieder ReadFromDB - LKA:$lka")

        dataListFromDB = if(lka=="alle"){
            DBLib.DatabaseInstance.egliederDAO().getAll()
        }else{
            DBLib.DatabaseInstance.egliederDAO().getbyLKA(lka)
        }

        liederListmapped = dataListFromDB.map {
            EGLiedMod(it.nr, it.nr2, it.lied, it.anlass, it.ix, it.ixUr, it.egTeil)
        }
        IAbstrHelper().afterontextChanged("")
        binding.EGLiederRecyclerView.layoutManager = LinearLayoutManager(this@EGLieder)
        setAdapter()

        binding.EGLiederProgressbar.visibility = View.GONE
    }
    private fun setAdapter(){

        if(sort=="abc") {
            binding.EGLiederRecyclerView.adapter = EGLiederAdapterABC(dataListfiltered.sortedBy { it.lied.lowercase(Locale.ROOT) },sort)
            binding.EGLiederSearchEditText.inputType = (InputType.TYPE_CLASS_TEXT)
        }else{
            binding.EGLiederRecyclerView.adapter = EGLiederAdapterNr(dataListfiltered.sortedBy { it.nr},sort)
            binding.EGLiederSearchEditText.inputType = (InputType.TYPE_CLASS_NUMBER)
        }
    }


    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    inner class IAbstrHelper : AbstrHelper() {

        override fun afterontextChanged(suchstring: String) {
            dataListfiltered = if(sort=="abc") {
                liederListmapped.filter { it.lied.contains(suchstring,true)}
            }else{
                liederListmapped.filter { it.nr.toString().startsWith(suchstring,true)}
            }
            setAdapter()
        }
    }
}
