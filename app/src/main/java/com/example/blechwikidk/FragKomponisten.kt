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
import com.example.blechwikidk.database.KomponistFromDB
import com.example.blechwikidk.databinding.FragKomponistenBinding
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import java.util.Locale

class FragKomponisten : Fragment() {
    private lateinit var binding: FragKomponistenBinding

    private lateinit var dataListFromDB: List<KomponistFromDB>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        binding = FragKomponistenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d("Dieter","FragKomponisten")

        IAbstrHelper().setTextWatcher(binding.KomponistSearchEditText)

        binding.KomponistSearchDelete.setOnClickListener {

            binding.KomponistSearchEditText.setText("")

        }

        setmenu()
        readFromDB()
    }
    private fun readFromDB() {

        dataListFromDB = DBLib.DatabaseInstance.komponistDAO().getAll()

        binding.KomponistProgressBar.visibility = View.GONE
        binding.KomponistRecyclerView.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = KomponistAdapter(dataListFromDB.sortedBy { it.friendlyKomponistName.lowercase(Locale.ROOT) })
        }
        d("Dieter", "Komponist ReadFromDB")
    }
    private fun setmenu() {
        //-----------------------------------------
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.option_menu_komponist, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId)
                {
                    R.id.actionKomponisttableupdate -> {
                        binding.KomponistProgressBar.visibility= View.VISIBLE
                        Toast.makeText(requireContext(), "!ich lade jetzt die aktuelle Liste vom Server!", Toast.LENGTH_LONG).show()

                        SessionLib.setPref("KomponistChangeCounter", "0")
                        TableUpdate.checkforTableupdateKomponisten()
                        binding.KomponistSearchEditText.setText("")
                        readFromDB()
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