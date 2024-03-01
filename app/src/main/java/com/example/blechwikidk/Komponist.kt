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
import com.example.blechwikidk.database.KomponistFromDB
import com.example.blechwikidk.databinding.KomponistBinding
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.MenuLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import java.util.Locale

class Komponist : AppCompatActivity() {

    private lateinit var binding: KomponistBinding
    private lateinit var dataListFromDB: List<KomponistFromDB>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = KomponistBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_blue)
            val localclassname = localClassName
            title = localclassname
        }

        //------------------
        binding.KomponistNavView.setNavigationItemSelectedListener {
            binding.KomponistDrawerLayout.closeDrawers()

            val intent = MenuLib.mainmenueIntent(it, this)
            startActivity(intent)

            true

        }


        IAbstrHelper().setTextWatcher(binding.KomponistSearchEditText)

        binding.KomponistSearchDelete.setOnClickListener {

            binding.KomponistSearchEditText.setText("")

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        readFromDB()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.actionKomponisttableupdate -> {
                binding.KomponistProgressBar.visibility= View.VISIBLE
                Toast.makeText(this, "!ich lade jetzt die aktuelle Liste vom Server!", Toast.LENGTH_LONG).show()

                SessionLib.setPref("KomponistChangeCounter", "0")
                TableUpdate.checkforTableupdateKomponisten()
                binding.KomponistSearchEditText.setText("")
                readFromDB()
                return true
            }
            else -> {
                binding.KomponistDrawerLayout.openDrawer(GravityCompat.START)
                binding.KomponistNavView.setCheckedItem(R.id.actionKomponisten)
                return true
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_komponist, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //------------------------------------------------
    private fun readFromDB() {

        dataListFromDB = DBLib.DatabaseInstance.komponistDAO().getAll()

        binding.KomponistProgressBar.visibility = View.GONE
        binding.KomponistRecyclerView.apply {

            layoutManager = LinearLayoutManager(this@Komponist)
            adapter = KomponistAdapter(dataListFromDB.sortedBy { it.friendlyKomponistName.lowercase(Locale.ROOT) })
        }
        Log.d("Dieter", "Komponist ReadFromDB")
    }


    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    inner class IAbstrHelper : AbstrHelper() {

        override fun afterontextChanged(suchstring: String) {
            binding.KomponistRecyclerView.adapter = KomponistAdapter(dataListFromDB.filter { it.friendlyKomponistName.contains(suchstring,true)}.sortedBy {
                it.friendlyKomponistName.lowercase(
                    Locale.ROOT
                )
            })
        }
    }
}
