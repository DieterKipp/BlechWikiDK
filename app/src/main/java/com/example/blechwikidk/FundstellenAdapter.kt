package com.example.blechwikidk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.blechwikidk.model.FundstellenRecyclerMod

class FundstellenAdapter1(private val titelList:List<FundstellenRecyclerMod>): RecyclerView.Adapter<FundstellenAdapter1.ViewHolder>(){

    //dieser Adapter ist für die Anzeige sortiert und gruppiert nach Buch

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fundstellen_1_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = titelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vtitel = titelList[position]
        val titelpluszus = "${vtitel.titel} ${vtitel.zus}"
        holder.vTitel.text = titelpluszus
        holder.vNr.text = vtitel.nr.toString()
        holder.vBesetzung.text = vtitel.besetzung
        holder.vKomponist.text = vtitel.komponist
        holder.vVorzeichen.text = vtitel.vorzeichen
        holder.vBuch.text = vtitel.buch

        val previousItem = if(position>0){
            titelList[position-1].quellekurz
        }else{
            ""
        }

        if(vtitel.quellekurz==previousItem){
            holder.vheadline.visibility = View.GONE
        }else{
            holder.vheadline.visibility = View.VISIBLE
        }

        val vpath = "http://pcportal.ddns.net/Bilder/BilderBuch/mini240/" + vtitel.quellekurz + ".jpg"
        holder.vBild.load(vpath)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBuch: TextView = itemView.findViewById(R.id.fundstellen_1_Buch)
        val vTitel: TextView = itemView.findViewById(R.id.fundstellen_1_Titel)
        val vNr: TextView = itemView.findViewById(R.id.fundstellen_1_Nr)
        val vBesetzung: TextView = itemView.findViewById(R.id.fundstellen_1_Besetzung)
        val vKomponist: TextView = itemView.findViewById(R.id.fundstellen_1_Komponist)
        val vVorzeichen: TextView = itemView.findViewById(R.id.fundstellen_1_Vorzeichen)
        val vBild: ImageView = itemView.findViewById(R.id.fundstellen_1_BildBuch)
        val vheadline: Group = itemView.findViewById(R.id.fundstellen_1_rowheadlinegroup)

    }


}

class FundstellenAdapter2(private val titelList:List<FundstellenRecyclerMod>): RecyclerView.Adapter<FundstellenAdapter2.ViewHolder>() {

    //dieser Adapter ist für die Anzeige sortiert und gruppiert nach Titel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.fundstellen_2_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vtitel = titelList[position]
        holder.vBuch.text= vtitel.buch
        holder.vTitel.text = vtitel.titel
        holder.vTitel2.text= vtitel.zus
        holder.vNr.text = vtitel.nr.toString()
        holder.vBesetzung.text = vtitel.besetzung
        holder.vVorzeichen.text = vtitel.vorzeichen

        val previousItem = if(position>0){
            titelList[position-1].titel
        }else{
            ""
        }

        if(vtitel.titel==previousItem){
            holder.vheadline.visibility = View.GONE
        }else{
            holder.vheadline.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int = titelList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBuch: TextView = itemView.findViewById(R.id.fundstellen_2_Buch)
        val vTitel: TextView = itemView.findViewById(R.id.fundstellen_2_Titel)
        val vTitel2: TextView = itemView.findViewById(R.id.fundstellen_Komponist_Titel2)
        val vNr: TextView = itemView.findViewById(R.id.fundstellen_2_Nr)
        val vBesetzung: TextView = itemView.findViewById(R.id.fundstellen_Komponist_Besetzung)
        val vVorzeichen: TextView = itemView.findViewById(R.id.fundstellen_Komponist_Vorzeichen)
        val vheadline: Group = itemView.findViewById(R.id.komponistrow2headlinegroup)

    }
}

class FundstellenAdapter3(private val titelList:List<FundstellenRecyclerMod>): RecyclerView.Adapter<FundstellenAdapter3.ViewHolder>(){

    //dieser Adapter ist für die Anzeige sortiert nach Nummer für Bücher

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fundstellen_3_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = titelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vtitel = titelList[position]
        val titelpluszus = "${vtitel.titel} ${vtitel.zus}"
        holder.vTitel.text = titelpluszus
        holder.vNr.text = vtitel.nr.toString()
        holder.vBesetzung.text = vtitel.besetzung
        holder.vKomponist.text = vtitel.komponist
        holder.vVorzeichen.text = vtitel.vorzeichen

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vTitel: TextView = itemView.findViewById(R.id.fundstellen_3_Titel)
        val vNr: TextView = itemView.findViewById(R.id.fundstellen_3_Nr)
        val vBesetzung: TextView = itemView.findViewById(R.id.fundstellen_3_Besetzung)
        val vKomponist: TextView = itemView.findViewById(R.id.fundstellen_3_Komponist)
        val vVorzeichen: TextView = itemView.findViewById(R.id.fundstellen_3_Vorzeichen)

    }


}

class FundstellenAdapter4(private val titelList:List<FundstellenRecyclerMod>): RecyclerView.Adapter<FundstellenAdapter4.ViewHolder>(){

    //dieser Adapter ist für die Anzeige sortiert nach Titel für Bücher

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fundstellen_4_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = titelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vtitel = titelList[position]
        holder.vTitel.text = vtitel.titel
        holder.vTitel2.text = vtitel.zus
        holder.vNr.text = vtitel.nr.toString()
        holder.vBesetzung.text = vtitel.besetzung
        holder.vKomponist.text = vtitel.komponist
        holder.vVorzeichen.text = vtitel.vorzeichen

        val previousItem = if(position>0){
            titelList[position-1].titel
        }else{
            ""
        }

        if(vtitel.titel==previousItem){
            holder.vheadline.visibility = View.GONE
        }else{
            holder.vheadline.visibility = View.VISIBLE
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vTitel: TextView = itemView.findViewById(R.id.fundstellen_4_Titel)
        val vTitel2: TextView = itemView.findViewById(R.id.fundstellen_4_Titel2)
        val vNr: TextView = itemView.findViewById(R.id.fundstellen_4_Nr)
        val vBesetzung: TextView = itemView.findViewById(R.id.fundstellen_4_Besetzung)
        val vKomponist: TextView = itemView.findViewById(R.id.fundstellen_4_Komponist)
        val vVorzeichen: TextView = itemView.findViewById(R.id.fundstellen_4_Vorzeichen)
        val vheadline: Group = itemView.findViewById(R.id.komponistrow_4_headlinegroup)

    }


}