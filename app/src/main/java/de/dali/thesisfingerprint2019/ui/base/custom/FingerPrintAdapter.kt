package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.ImageEntity
import java.io.File


class FingerPrintAdapter(context: Context?) : RecyclerView.Adapter<FingerPrintAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    var listOfFingerPrints = listOf<ImageEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.item_single_fingerprint, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imgFileOrig = File("${Environment.getExternalStorageDirectory()}/" + listOfFingerPrints[position].pathRGB)
        holder.ivFingerprintOrig.setImageURI(Uri.fromFile(imgFileOrig))

        val imgFileGray = File("${Environment.getExternalStorageDirectory()}/" + listOfFingerPrints[position].pathGray)
        holder.ivFingerprintGray.setImageURI(Uri.fromFile(imgFileGray))

        val imgFileEnhanced =
            File("${Environment.getExternalStorageDirectory()}/" + listOfFingerPrints[position].pathEnhanced)
        holder.ivFingerprintEnhanced.setImageURI(Uri.fromFile(imgFileEnhanced))

        holder.textID.text = listOfFingerPrints[position].biometricalID.toString()
        holder.textDegree.text = listOfFingerPrints[position].correctionDegree.toString()
    }

    override fun getItemCount(): Int {
        return listOfFingerPrints.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFingerprintEnhanced: ImageView = itemView.findViewById(R.id.ivFingerprintEnhanced)
        var ivFingerprintGray: ImageView = itemView.findViewById(R.id.ivFingerprintGray)
        var ivFingerprintOrig: ImageView = itemView.findViewById(R.id.ivFingerprintOrig)
        var textID: TextView = itemView.findViewById(R.id.tvID)
        var textDegree: TextView = itemView.findViewById(R.id.tvDegree)
    }
}