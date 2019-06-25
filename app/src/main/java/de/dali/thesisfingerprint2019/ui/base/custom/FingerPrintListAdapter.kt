package de.dali.thesisfingerprint2019.ui.base.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dali.thesisfingerprint2019.R
import de.dali.thesisfingerprint2019.data.local.entity.FingerPrintEntity
import de.dali.thesisfingerprint2019.ui.base.custom.FingerPrintListAdapter.FingerPrintViewHolder
import de.dali.thesisfingerprint2019.utils.Utils

class FingerPrintListAdapter : RecyclerView.Adapter<FingerPrintViewHolder>() {

    private lateinit var onClickCallback: (FingerPrintEntity) -> Unit

    var list: List<FingerPrintEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class FingerPrintViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRecordSet: TextView = view.findViewById<View>(R.id.txtRecordSet) as TextView
        val txtCreatedOn: TextView = view.findViewById<View>(R.id.txtCreatedOn) as TextView

        init {
            view.setOnClickListener {
                list?.let { list ->
                    val entity = list[this.adapterPosition]
                    onClickCallback(entity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FingerPrintViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_fingerprint, parent, false)

        return FingerPrintViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FingerPrintViewHolder, position: Int) {
        list?.let {
            val entity = it[position]
            holder.txtRecordSet.text =
                holder.itemView.context.getString(R.string.fragment_selection_record_set, position.toString())
            holder.txtCreatedOn.text = holder.itemView.context.getString(
                R.string.fragment_selection_created_on,
                Utils.toReadableDate(entity.timestamp!!).toString()
            )
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun setCallback(onClickCallback: (FingerPrintEntity) -> Unit) {
        this.onClickCallback = onClickCallback
    }
}