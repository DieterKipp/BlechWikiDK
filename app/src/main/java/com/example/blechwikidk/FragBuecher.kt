package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
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
import com.example.blechwikidk.databinding.FragBuecherBinding
import com.example.blechwikidk.model.BuchRCMod
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import java.util.Locale

class FragBuecher : Fragment() {
    private lateinit var binding: FragBuecherBinding

    private lateinit var dataListFromDB: List<BuchRCMod>

    private var buecherFilter = "alle"
    // meine oder alle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        binding = FragBuecherBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d("Dieter","FragBuecher")

        buecherFilter = SessionLib.getPref("BuecherFilter")
        //---------------------

        when (buecherFilter) {
            "noname" -> {
                SessionLib.setPref("BuecherFilter", "alle")
                buecherFilter = "alle"
            }
        }

        setListeners()
        setmenu()
        readFromDB()
    }

    private fun setListeners() {

        IAbstrHelper().setTextWatcher(binding.buecherSearchEditText)

        binding.buecherSearchDelete.setOnClickListener {

            binding.buecherSearchEditText.setText("")

        }
    }

    //--------------------------------------------------

    private fun readFromDB() {
        d("Dieter", "Buch ReadFromDB")

        dataListFromDB = if(buecherFilter=="meine"){
            DBLib.DatabaseInstance.buecherDAO().getAllmeineRCMod()
        }else{
            DBLib.DatabaseInstance.buecherDAO().getAllRCMod()
        }

        binding.buecherProgressBar.visibility = View.GONE

        binding.buecherRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = BuecherAdapter(dataListFromDB.filter { it.buch.contains(binding.buecherSearchEditText.text,true)}.sortedBy { it.buch })
        }
    }
    //------------------------------------------------
    private fun setmenu() {
        //-----------------------------------------
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.option_menu_buecher, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId)
                {
                    R.id.actionBüchertableupdate -> {
                        binding.buecherProgressBar.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            "!ich lade jetzt die aktuelle Liste vom Server!",
                            Toast.LENGTH_LONG
                        ).show()

                        SessionLib.setPref("BuchChangeCounter", "0")
                        TableUpdate.checkforTableupdateBuecher()
                        binding.buecherSearchEditText.setText("")
                        readFromDB()
                        //SessionLib.dialoganzeigen(this,"dieser Menüzweig ist vorübergehend deaktiviert","ok","deaktiviert")
                        return true
                    }

                    R.id.actionBesitzFilter -> {
                        togglefilter(menuItem)
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

/*    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Log.d("Dieter", "onSaveInstanceState")
        outState.putCharSequence("search",binding.buecherSearchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.buecherRecyclerView.requestFocus()
    }*/

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    inner class IAbstrHelper : AbstrHelper() {

        override fun afterontextChanged(suchstring: String) {
            binding.buecherRecyclerView.adapter = BuecherAdapter(dataListFromDB.filter { it.buch.contains(suchstring,true)}.sortedBy {it.buch.lowercase(Locale.ROOT)})
        }
    }

}