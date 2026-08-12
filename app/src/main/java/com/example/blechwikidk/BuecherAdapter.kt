package com.example.blechwikidk

import android.content.Intent
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.blechwikidk.model.BuchRCMod
import com.example.blechwikidk.util.DBLib
import com.example.blechwikidk.util.SessionLib

class BuecherAdapter(private val buecherList: List<BuchRCMod>) :
    RecyclerView.Adapter<BuecherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.frag_buecher_row, parent, false)
        val vholder = ViewHolder(view)



        view.setOnClickListener {
            val intent = Intent(parent.context, BuecherFundstellen::class.java)
            intent.putExtra("BuchId", buecherList[vholder.bindingAdapterPosition].buchId.toString())
            intent.putExtra("Buch", buecherList[vholder.bindingAdapterPosition].buch)

            parent.context.startActivity(intent)
        }

        view.setOnLongClickListener {
            val intent = Intent(parent.context, BuchDetails::class.java)
            intent.putExtra("BuchId", buecherList[vholder.bindingAdapterPosition].buchId.toString())

            parent.context.startActivity(intent)
            true
        }

        vholder.vmeineBuecher.setOnClickListener {
            val vorhandenDB = buecherList[vholder.bindingAdapterPosition].vorhanden
            val vcheckbox: CheckBox = it.findViewById(R.id.LiteraBuecherBuchCheckBox)
            //val checkstate = vcheckbox.isChecked
            val checkstate = if (vcheckbox.isChecked) {
                "true"
            } else {
                ""
            }
//            d("Dieter", "vorhandenDB: $vorhandenDB")
//            d("Dieter", "checkboxstate: $checkstate")
            if (vorhandenDB !== checkstate) {
                buecherList[vholder.bindingAdapterPosition].vorhanden = checkstate
                DBLib.DatabaseInstance.buecherDAO()
                    .updatebuchvorhanden(buecherList[vholder.bindingAdapterPosition].buchId, checkstate)
                d("Dieter", "checkboxstategeändert: $checkstate")
            }
        }
        return vholder

    }

    override fun getItemCount(): Int = buecherList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vbuch = buecherList[position]
        holder.vBuch.text = vbuch.buch
        holder.vUntertitel.text = vbuch.untertitel
        when(vbuch.vorhanden){
            "true" -> holder.vmeineBuecher.isChecked = true
            "truetrue" -> holder.vmeineBuecher.isChecked = true
            else -> holder.vmeineBuecher.isChecked = false
        }

        val vpath = SessionLib.BASISURLPICBUCH + "mini240/" + vbuch.buchkurz + ".jpg"
        holder.vBild.load(vpath)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vBuch: TextView = itemView.findViewById(R.id.LiteraBuecherBuch)
        val vUntertitel: TextView = itemView.findViewById(R.id.LiteraBuecherBuchUntertitel)
        val vBild: ImageView = itemView.findViewById(R.id.LiteraBuecherBild)
        val vmeineBuecher: CheckBox = itemView.findViewById(R.id.LiteraBuecherBuchCheckBox)

    }


}

