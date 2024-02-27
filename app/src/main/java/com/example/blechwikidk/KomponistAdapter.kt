package com.example.blechwikidk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.database.KomponistFromDB

class KomponistAdapter(private val komponistList:List<KomponistFromDB>) : RecyclerView.Adapter<KomponistAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.komponist_row, parent, false)
        val vholder = ViewHolder(view)


        view.setOnClickListener {
            val intent = Intent(parent.context, KomponistFundstellen::class.java)
            intent.putExtra("id", komponistList[vholder.adapterPosition].id.toString())
            intent.putExtra("Komponist", komponistList[vholder.adapterPosition].komponist)

            parent.context.startActivity(intent)
        }
        return vholder
    }

    override fun getItemCount(): Int = komponistList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vKomponistPos = komponistList[position]
        holder.vKomponist.text = vKomponistPos.komponist

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vKomponist: TextView = itemView.findViewById(R.id.komponist_TextView)
    }
}