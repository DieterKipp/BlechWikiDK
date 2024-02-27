package com.example.blechwikidk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.database.TitelFromDB

class TitelAdapter(private val titelList:List<TitelFromDB>) : RecyclerView.Adapter<TitelAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.titel_row, parent, false)
        val vholder = ViewHolder(view)


        view.setOnClickListener {
            val intent = Intent(parent.context, TitelFundstellen::class.java)
            intent.putExtra("Ix", titelList[vholder.adapterPosition].ix.toString())
            intent.putExtra("Titel", titelList[vholder.adapterPosition].titel)

            parent.context.startActivity(intent)
        }

        return vholder

    }

    override fun getItemCount(): Int = titelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vTitelPos = titelList[position]
        holder.vTitel.text = vTitelPos.titel

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vTitel: TextView = itemView.findViewById(R.id.titel_TextView)

    }


}