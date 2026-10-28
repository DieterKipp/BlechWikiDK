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
import com.example.blechwikidk.database.TitelFromDB
import com.example.blechwikidk.databinding.FragTitelBinding
import com.example.blechwikidk.util.AbstrHelper
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.TableUpdate
import java.util.Locale

class FragTitel : Fragment() {
    private lateinit var binding: FragTitelBinding

    private lateinit var dataListFromDB: List<TitelFromDB>
    private lateinit var dataListfiltered: List<TitelFromDB>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {

        binding = FragTitelBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d("Dieter","FragTitel")

        IAbstrHelper().setTextWatcher(binding.TitelSearchEditText)

        binding.TisteSearchDelete.setOnClickListener {

            binding.TitelSearchEditText.setText("")


        }
        setmenu()
        readFromDB()
    }

    private fun readFromDB() {

        dataListFromDB = DBLib.DatabaseInstance.titelDAO().getAll()
        dataListfiltered = dataListFromDB.sortedBy { it.titelohneKomma.lowercase(Locale.ROOT) }
        binding.TitelProgressBar.visibility = View.GONE

        binding.TitelRecyclerView.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = TitelAdapter(dataListfiltered)
        }

    }
    private fun setmenu() {
        //-----------------------------------------
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.option_menu_titel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId)
                {
                    R.id.actionTiteltableupdate -> {
                        binding.TitelProgressBar.visibility= View.VISIBLE
                        Toast.makeText(requireContext(), "!ich lade jetzt die aktuelle Liste vom Server!", Toast.LENGTH_LONG).show()

                        SessionLib.setPref("TitelChangeCounter", "0")
                        TableUpdate.checkforTableupdateTitel()
                        binding.TitelSearchEditText.setText("")
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
            dataListfiltered =dataListFromDB.filter { it.titelohneKomma.contains(suchstring,true)}
            binding.TitelRecyclerView.adapter = TitelAdapter(dataListfiltered.sortedBy {
                it.titelohneKomma.lowercase(
                    Locale.ROOT
                )
            })
        }
    }

}