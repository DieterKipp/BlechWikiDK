package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log.d
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blechwikidk.database.EGLiederFromDB
import com.example.blechwikidk.databinding.FragEgBinding
import com.example.blechwikidk.model.EGLiedMod
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import kotlinx.coroutines.MainScope
import java.util.Locale

class FragEG : Fragment() {
    private lateinit var binding: FragEgBinding
    private lateinit var dataListFromDB: List<EGLiederFromDB>
    private lateinit var liederListmapped: List<EGLiedMod>
    private lateinit var dataListfiltered: List<EGLiedMod>

    private val mainScope= MainScope()

    private var sort: String = "abc"
    private var lka: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        binding = FragEgBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d("Dieter","FragEG")
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

        setmenu()
        readFromDB()

    }

    private fun setListeners() {

        IAbstrHelper().setTextWatcher(binding.EGLiederSearchEditText)

        //---------------------
        binding.EGLiederSearchDelete.setOnClickListener {

            binding.EGLiederSearchEditText.setText("")

        }
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
        binding.EGLiederRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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

    private fun setmenu() {
        //-----------------------------------------
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.option_menu_eg, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId)
                {

                    R.id.actionEGSort_toggle -> {
                        togglesort(menuItem)
                        return true
                    }
                    R.id.actionEGtableupdate -> {
                        binding.EGLiederProgressbar.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "!ich lade jetzt die aktuelle EG-Lieder Liste vom Server!", Toast.LENGTH_LONG).show()

                        SessionLib.setPref("EGLiedergelesen", "")
                        TableUpdate.checkforTableupdateEG()
                        binding.EGLiederSearchEditText.setText("")
                        readFromDB()
                        return true
                    }
                    R.id.actionEGTextsearch, R.id.actionEGTextsearchdouble -> {
                        val intent = Intent(requireContext(), EGLiederTextSearch::class.java)
                        startActivity(intent)
                        return true
                    }
                    R.id.actionSettings -> {
                        val intent = Intent(requireContext(), Settings::class.java)
                        startActivity(intent)
                        return true
                    }
                }

                return true
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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