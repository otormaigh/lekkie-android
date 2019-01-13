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

package ie.pennylabs.lekkie.feature.info

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.arch.BaseFragment
import ie.pennylabs.lekkie.data.room.LekkieDatabase
import ie.pennylabs.lekkie.feature.gdpr.GdprBottomSheet
import ie.pennylabs.lekkie.toolbox.extension.intervalOfUniqueWork
import ie.pennylabs.lekkie.toolbox.extension.isPermissiontGranted
import ie.pennylabs.lekkie.toolbox.extension.requireApplicationContext
import ie.pennylabs.lekkie.toolbox.extension.toast
import ie.pennylabs.lekkie.toolbox.extension.viewModelProvider
import ie.pennylabs.lekkie.toolbox.prefs
import ie.pennylabs.lekkie.toolbox.syncEnabled
import ie.pennylabs.lekkie.worker.ApiWorker
import kotlinx.android.synthetic.main.controller_info.*
import java.util.concurrent.TimeUnit

class InfoFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnLongClickListener {
  private val viewModel by viewModelProvider { InfoViewModel() }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
    inflater.inflate(R.layout.controller_info, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    tvDataCollection.setOnClickListener { GdprBottomSheet.show(view.context, true) }
    tvExportDatabase.setOnClickListener {
      if (isPermissiontGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        viewModel.exportDatabase(requireApplicationContext().getDatabasePath(LekkieDatabase.NAME))
      } else {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RC_WRITE_EXTERNAL_STORAGE)
      }
    }

    enableBackgroundSync(requireContext().prefs.syncEnabled)

    viewModel.showToast.observe(this, Observer { toast(message = it) })
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == RC_WRITE_EXTERNAL_STORAGE && isPermissiontGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      viewModel.exportDatabase(requireApplicationContext().getDatabasePath(LekkieDatabase.NAME))
    }
  }

  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    if (seekBar.progress < SYNC_INTERVAL_MIN) return
    tvSyncMins.text = getString(R.string.sync_hours, seekBar.progress)
  }

  override fun onStartTrackingTouch(seekBar: SeekBar?) {
    // Ignore
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    val interval = if (seekBar.progress < SYNC_INTERVAL_MIN) 1
    else seekBar.progress

    ApiWorker.updateRepeatInterval(interval.toLong(), TimeUnit.HOURS)
    tvSyncMins.text = getString(R.string.sync_hours, seekBar.progress)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.tvSyncInterval -> sbInterval.isInvisible = !sbInterval.isInvisible
    }
  }

  override fun onLongClick(v: View): Boolean {
    when (v.id) {
      R.id.tvSyncInterval -> {
        toast("Background sync cleared")
        enableBackgroundSync(false)
      }
    }
    return true
  }

  private fun enableBackgroundSync(isEnabled: Boolean) {
    if (isEnabled) {
      tvSyncInterval.setOnClickListener(this)
      tvSyncInterval.setOnLongClickListener(this)
      sbInterval.setOnSeekBarChangeListener(this)
      switchSync.setOnCheckedChangeListener(null)
      tvSyncMins.visibility = View.VISIBLE
      switchSync.visibility = View.INVISIBLE

      sbInterval.max = SYNC_INTERVAL_MAX
      WorkManager.getInstance().intervalOfUniqueWork(ApiWorker.RECURRING_TAG).observe(this, Observer { interval ->
        val hours = TimeUnit.HOURS.convert(interval, TimeUnit.MILLISECONDS)
        sbInterval.progress = hours.toInt()
        tvSyncMins.text = getString(R.string.sync_hours, hours)
      })

    } else {
      WorkManager.getInstance().cancelAllWork()

      tvSyncInterval.setOnClickListener(null)
      tvSyncInterval.setOnLongClickListener(null)
      tvSyncMins.visibility = View.INVISIBLE
      sbInterval.visibility = View.INVISIBLE
      switchSync.visibility = View.VISIBLE

      switchSync.isChecked = false
      switchSync.setOnCheckedChangeListener { _, _ ->
        enableBackgroundSync(true)
      }
    }

    // Reset flag once all is setup.
    requireContext().prefs.syncEnabled = isEnabled
  }

  companion object {
    const val RC_WRITE_EXTERNAL_STORAGE = 42
    const val SYNC_INTERVAL_MIN = 1
    const val SYNC_INTERVAL_MAX = 48
  }
}