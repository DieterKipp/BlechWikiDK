package com.example.blechwikidk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blechwikidk.model.EGLiedMod

class EGLiederAdapter(private val egLiederList: List<EGLiedMod>, private val sort:String) : RecyclerView.Adapter<EGLiederAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if(sort=="abc"){LayoutInflater.from(parent.context).inflate(R.layout.eglieder_row_abc, parent, false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.eglieder_row_nr, parent, false)
        }
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
        val vEGLiedPos = egLiederList[position]
        holder.vEGLied.text = vEGLiedPos.lied
        holder.vEGNr.text = vEGLiedPos.nr.toString()

    }


    //Viewholder für Recyclerview
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vEGLied: TextView = itemView.findViewById(R.id.EGLieder_Lied_textView)
        val vEGNr: TextView = itemView.findViewById(R.id.EGLieder_Nr_textView)

    }
}

