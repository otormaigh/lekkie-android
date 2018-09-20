/*
 * Copyright (C) 2018 Elliot Tormey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.pennylabs.lekkie.feature.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.data.model.Outage
import ie.pennylabs.lekkie.toolbox.extension.formatTimestamp
import ie.pennylabs.lekkie.toolbox.extension.getString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_item_outage.*

class OutageListRecyclerAdapter : ListAdapter<Outage, OutageListRecyclerAdapter.ViewHolder>(diffCallback) {
  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_outage, parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemId(position: Int): Long =
    getItem(position).id.hashCode().toLong()

  inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(outage: Outage) {
      tvLocation.text = "${outage.location}, ${outage.county ?: ""}"
      tvType.text = outage.type

      tvEstimatedRestore.text = getString(R.string.est_restore, outage.estRestoreTime.formatTimestamp())
      tvStartTime.text = getString(R.string.started, outage.startTime.formatTimestamp())

      ivType.setImageResource(when (outage.type) {
        Outage.PLANNED -> R.drawable.ic_outage_planned
        Outage.FAULT -> R.drawable.ic_outage_fault
        else -> R.drawable.ic_outage_unkonwn
      })
    }
  }
}

private val diffCallback = object : DiffUtil.ItemCallback<Outage>() {
  override fun areContentsTheSame(oldItem: Outage, newItem: Outage) = oldItem.hashCode() == newItem.hashCode()
  override fun areItemsTheSame(oldItem: Outage, newItem: Outage) = oldItem.id === newItem.id
}