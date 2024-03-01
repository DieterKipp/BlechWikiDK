package com.example.blechwikidk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.model.EGLiedMod

class EGLiederAdapterABC(private val egLiederList: List<EGLiedMod>, private val sort:String) : RecyclerView.Adapter<EGLiederAdapterABC.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eglieder_row_abc, parent, false)

        val vholder = ViewHolder(view)

        view.setOnClickListener {
            //

            val intent = Intent(parent.context, EGLiederFundstellen::class.java)
            intent.putExtra("IxUr",egLiederList[vholder.adapterPosition].ixUr.toString())
            intent.putExtra("Ix",egLiederList[vholder.adapterPosition].ix.toString())
            intent.putExtra("Lied",egLiederList[vholder.adapterPosition].lied)
            parent.context.startActivity(intent)
        }


        return vholder

    }

    override fun getItemCount(): Int = egLiederList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val egitem = egLiederList[position]
        holder.egLied.text = egitem.lied
        holder.egNr.text = egitem.nr.toString()
    }


    //Viewholder für Recyclerview
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val egLied: TextView = itemView.findViewById(R.id.eglieder_tv_lied)
        val egNr: TextView = itemView.findViewById(R.id.eglieder_tv_Nr)

    }
}


class EGLiederAdapterNr(private val egLiederList: List<EGLiedMod>, private val sort:String) : RecyclerView.Adapter<EGLiederAdapterNr.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eglieder_row_nr, parent, false)

        val vholder = ViewHolder(view)

        view.setOnClickListener {
            //

            val intent = Intent(parent.context, EGLiederFundstellen::class.java)
            intent.putExtra("IxUr",egLiederList[vholder.adapterPosition].ixUr.toString())
            intent.putExtra("Ix",egLiederList[vholder.adapterPosition].ix.toString())
            intent.putExtra("Lied",egLiederList[vholder.adapterPosition].lied)
            parent.context.startActivity(intent)
        }


        return vholder

    }

    override fun getItemCount(): Int = egLiederList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val egitem = egLiederList[position]
        holder.egLied.text = egitem.lied
        holder.egNr.text = egitem.nr.toString()
        holder.anlass.text = egitem.anlass

        val previousItem = if(position>0){
            egLiederList[position-1].anlass
        }else{
            ""
        }

        if(egitem.anlass == previousItem){
            holder.headlinegroup.visibility = View.GONE
        }else{
            holder.headlinegroup.visibility = View.VISIBLE
        }
    }


    //Viewholder für Recyclerview
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val egLied: TextView = itemView.findViewById(R.id.eglieder_tv_lied)
        val egNr: TextView = itemView.findViewById(R.id.eglieder_tv_Nr)
        val anlass: TextView = itemView.findViewById(R.id.eglieder_tv_anlass)
        val headlinegroup: Group = itemView.findViewById(R.id.eglieder_headlinegroup)

    }
}

