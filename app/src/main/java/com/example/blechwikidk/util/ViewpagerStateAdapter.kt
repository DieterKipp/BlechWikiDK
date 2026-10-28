package com.example.blechwikidk.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.blechwikidk.FragBuecher
import com.example.blechwikidk.FragEG
import com.example.blechwikidk.FragKomponisten
import com.example.blechwikidk.FragTitel


class ViewpagerStateAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager,lifecycle) {

    private var fragments:ArrayList<Fragment> = arrayListOf(FragTitel(), FragEG(), FragKomponisten(), FragBuecher()
    )


    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}