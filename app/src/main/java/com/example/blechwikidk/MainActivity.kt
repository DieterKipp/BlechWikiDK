package com.example.blechwikidk

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.example.blechwikidk.databinding.ActivityMainBinding
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.Fundstellen
import com.example.blechwikidk.util.SessionLib
import com.example.blechwikidk.util.SessionLib.mainScope
import com.example.blechwikidk.util.TableUpdate
import com.example.blechwikidk.util.UpdateHelper
import com.example.blechwikidk.util.ViewpagerStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private var orientation = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        SessionLib.setInsets(view)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.mipmap.ic_launcher_round)
            title = "BlechWiki"
        }
        SessionLib.sharedPref = getPreferences(MODE_PRIVATE)
        orientation = SessionLib.orientation(this)

        //-------------------------------
        if(savedInstanceState!=null){
            initViewpager()
        }else{
            DBLib.getDBInstance(this, "true")
            SessionLib.getOnlineStatus(this)
            UpdateHelper().checkforNewVersion(this)

            //-------------------------------------

            val fundstellenfilter = SessionLib.getPref("FundstellenFilter")
            if (fundstellenfilter=="noname")  {
                SessionLib.setPref("FundstellenFilter", "alle")
                Fundstellen.fundstellenFilter = "alle"
            }else{
                Fundstellen.fundstellenFilter = fundstellenfilter
            }
            binding.VersionTextview.text = "Version ${SessionLib.VERSIONNAME}"

            checkforTableupdates()
        }
    }
    //-------------------------------
    fun checkforTableupdates():String {

        //--------------------------------------------
        //erst wenn getRESTDataTitel fertig ist, darf der viewpager initialisiert werden
        //
        if (SessionLib.isOnline == "true") {
            val resultAsync = mainScope.async(Dispatchers.IO) {
                TableUpdate.getRESTDataTitel()
            }
            mainScope.launch(Dispatchers.Main) {
                if(resultAsync.await()=="fertig"){
                    initViewpager()
                }
            }
        }
        //--------------------------------------------

        TableUpdate.checkforTableupdateKomponisten()
        TableUpdate.checkforTableupdateBuecher()
        val vEGLiedergelesen = SessionLib.getPref("EGLiedergelesen")
        if (vEGLiedergelesen != "true") {
            Toast.makeText(this, "!ich lade jetzt die aktuelle EG-Lieder Liste vom Server!", Toast.LENGTH_LONG).show()
            TableUpdate.checkforTableupdateEG()
        }
        return "fertig"
    }

    private fun initViewpager() {
        viewPager = binding.viewPager
        val adapter = ViewpagerStateAdapter(supportFragmentManager,lifecycle)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = binding.tabLayout
        val names: ArrayList<String> = arrayListOf("Titel", "EG", "Komponisten", "Bücher")

        if(orientation=="landscape"){
            //keine Icons
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = names[position]
                //tab.tabLabelVisibility = TAB_LABEL_VISIBILITY_UNLABELED
            }.attach()
        }else{
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = names[0]
                        tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_book)
                    }
                    1 -> {
                        tab.text = names[1]
                        tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_eg)
                    }
                    2 -> {
                        tab.text = names[2]
                        tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_person)
                    }
                    3 -> {
                        tab.text = names[3]
                        tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_book)
                    }
                }
            }.attach()
        }
    }
    //----------------------------------------------------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionSettings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        //das erzwingt die icons in den dropdownmenüs
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val method = menu.javaClass.getDeclaredMethod(
                    "setOptionalIconsVisible",
                    Boolean::class.javaPrimitiveType
                )
                method.isAccessible = true
                method.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onMenuOpened(featureId, menu)
    }
}