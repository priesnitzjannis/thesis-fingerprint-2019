package de.dali.demonstrator.ui.base.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dali.demonstrator.R
import de.dali.demonstrator.data.local.entity.TestPersonEntity
import de.dali.demonstrator.ui.base.custom.TestPersonOverviewAdapter.TestPersonOverviewViewHolder
import de.dali.demonstrator.utils.Utils.toReadableDate

class TestPersonOverviewAdapter : RecyclerView.Adapter<TestPersonOverviewViewHolder>() {

    private lateinit var onClickCallback: (TestPersonEntity) -> Unit

    var list: List<TestPersonEntity>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class TestPersonOverviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUser: TextView = view.findViewById<View>(R.id.txtUser) as TextView
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestPersonOverviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_testperson, parent, false)

        return TestPersonOverviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestPersonOverviewViewHolder, position: Int) {
        list?.let {
            val entity = it[position]
            holder.txtUser.text = entity.name
            //holder.itemView.context.getString(R.string.fragment_test_person_overview, entity.personID.toString())
            holder.txtCreatedOn.text = holder.itemView.context.getString(
                R.string.fragment_selection_created_on,
                toReadableDate(entity.timestamp).toString()
            )
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun setCallback(onClickCallback: (TestPersonEntity) -> Unit) {
        this.onClickCallback = onClickCallback
    }
}