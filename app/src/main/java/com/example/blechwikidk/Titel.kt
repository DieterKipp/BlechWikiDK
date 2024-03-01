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
import com.example.blechwikidk.database.TitelFromDB
import com.example.blechwikidk.databinding.TitelBinding
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.MenuLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import java.util.Locale

class Titel : AppCompatActivity() {

    private lateinit var binding: TitelBinding

    private lateinit var dataListFromDB: List<TitelFromDB>
    private lateinit var dataListfiltered: List<TitelFromDB>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TitelBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_blue)
            val localclassname = localClassName
            title = localclassname
            //setSupportActionBar(toolbar)
        }
        //------------------
        binding.titelNavView.setNavigationItemSelectedListener {
            binding.TitelDrawerLayout.closeDrawers()

            val intent = MenuLib.mainmenueIntent(it, this)
            startActivity(intent)

            true
        }

        IAbstrHelper().setTextWatcher(binding.TitelSearchEditText)

        binding.TisteSearchDelete.setOnClickListener {

            binding.TitelSearchEditText.setText("")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
        readFromDB()
        //---------------------
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.actionTiteltableupdate -> {
                binding.TitelProgressBar.visibility= View.VISIBLE
                Toast.makeText(this, "!ich lade jetzt die aktuelle Liste vom Server!", Toast.LENGTH_LONG).show()

                SessionLib.setPref("TitelChangeCounter", "0")
                TableUpdate.checkforTableupdateTitel()
                binding.TitelSearchEditText.setText("")
                readFromDB()
                return true
            }
            else -> {
                binding.TitelDrawerLayout.openDrawer(GravityCompat.START)
                binding.titelNavView.setCheckedItem(R.id.actionTitel)
                return true
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_titel, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun readFromDB() {
        Log.d("Dieter", "Titel ReadFromDB beginn")

        dataListFromDB = DBLib.DatabaseInstance.titelDAO().getAll()
        dataListfiltered = dataListFromDB.sortedBy { it.titelohneKomma.lowercase(Locale.ROOT) }
        binding.TitelProgressBar.visibility = View.GONE

        binding.TitelRecyclerView.apply {

            layoutManager = LinearLayoutManager(this@Titel)
            adapter = TitelAdapter(dataListfiltered)
        }
        Log.d("Dieter", "Titel ReadFromDB ende")

    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    inner class IAbstrHelper : AbstrHelper() {

        override fun afterontextChanged(suchstring: String) {
            dataListfiltered =dataListFromDB.filter { it.titelohneKomma.contains(suchstring,true)}
            binding.TitelRecyclerView.adapter = TitelAdapter(dataListfiltered.sortedBy {
                it.titelohneKomma.lowercase(
                    Locale.ROOT
                )
            })
        }
    }

}
