package de.dali.thesisfingerprint2019.ui.base.custom

import android.content.Context
import android.content.Intent
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
import java.net.URI
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent.ACTION_VIEW
import androidx.core.content.FileProvider
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction




class FingerPrintAdapter(val context: Context?) : RecyclerView.Adapter<FingerPrintAdapter.ViewHolder>() {
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
        holder.ivFingerprintOrig.setOnClickListener { showImage(FileProvider.getUriForFile(context!!, context.applicationContext.packageName + ".fileprovider", imgFileOrig)) }

        val imgFileGray = File("${Environment.getExternalStorageDirectory()}/" + listOfFingerPrints[position].pathGray)
        holder.ivFingerprintGray.setImageURI(Uri.fromFile(imgFileGray))
        holder.ivFingerprintGray.setOnClickListener { showImage(FileProvider.getUriForFile(context!!, context.applicationContext.packageName + ".fileprovider", imgFileGray)) }

        val imgFileEnhanced =
            File("${Environment.getExternalStorageDirectory()}/" + listOfFingerPrints[position].pathEnhanced)
        holder.ivFingerprintEnhanced.setImageURI(Uri.fromFile(imgFileEnhanced))
        holder.ivFingerprintEnhanced.setOnClickListener { showImage(FileProvider.getUriForFile(context!!, context.applicationContext.packageName + ".fileprovider", imgFileEnhanced)) }

        holder.textID.text = listOfFingerPrints[position].biometricalID.toString()
        holder.textDegree.text = listOfFingerPrints[position].correctionDegree.toString() + "°"

        holder.textBroken.text = if (listOfFingerPrints[position].brokenDetectedByAlgorithm == true) "Yes" else "No"
    }

    override fun getItemCount(): Int {
        return listOfFingerPrints.size
    }

    private fun showImage(fileUri: Uri){
        val intent = Intent()
        intent.action = ACTION_VIEW
        intent.setDataAndType(fileUri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(context!!, intent, null)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFingerprintEnhanced: ImageView = itemView.findViewById(R.id.ivFingerprintEnhanced)
        var ivFingerprintGray: ImageView = itemView.findViewById(R.id.ivFingerprintGray)
        var ivFingerprintOrig: ImageView = itemView.findViewById(R.id.ivFingerprintOrig)
        var textID: TextView = itemView.findViewById(R.id.tvID)
        var textDegree: TextView = itemView.findViewById(R.id.tvDegree)
        var textBroken: TextView = itemView.findViewById(R.id.tvBroken)
    }
}