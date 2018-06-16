package ie.pennylabs.lekkie.feature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.data.model.Outage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_outage.*

class OutageListRecyclerAdapter : ListAdapter<Outage, OutageListRecyclerAdapter.ViewHolder>(diffCallback) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_outage, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(outage: Outage) {
      tvId.text = outage.id
      tvType.text = outage.type
    }
  }
}

private val diffCallback = object : DiffUtil.ItemCallback<Outage>() {
  override fun areContentsTheSame(oldItem: Outage, newItem: Outage) = oldItem === newItem
  override fun areItemsTheSame(oldItem: Outage, newItem: Outage) = oldItem.id === newItem.id
}
