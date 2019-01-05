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

package ie.pennylabs.lekkie.feature.gdpr

import android.content.Context
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.crashlytics.android.core.CrashlyticsCore
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.FirebasePerformance
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.toolbox.enableAnalytics
import ie.pennylabs.lekkie.toolbox.enableCrashReporting
import ie.pennylabs.lekkie.toolbox.enablePerformance
import ie.pennylabs.lekkie.toolbox.hasAcceptedGdpr
import ie.pennylabs.lekkie.toolbox.prefs
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.sheet_gdpr.*

class GdprBottomSheet(context: Context) : BottomSheetDialog(context) {
  init {
    setContentView(R.layout.sheet_gdpr)
    setCancelable(false)

    (window?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let { bottomSheet ->
      bottomSheet.setBackgroundResource(R.drawable.bg_top_rounded_sheet)
      bottomSheet.doOnLayout {
        BottomSheetBehavior.from(bottomSheet).apply {
          state = BottomSheetBehavior.STATE_EXPANDED
          peekHeight = it.height
        }
      }
    }

    switchPerf.isChecked = context.prefs.enablePerformance
    switchCrash.isChecked = context.prefs.enableCrashReporting
    switchAnalytics.isChecked = context.prefs.enableAnalytics

    switchPerf.setOnCheckedChangeListener { _, isChecked -> context.prefs.enablePerformance = isChecked }
    switchCrash.setOnCheckedChangeListener { _, isChecked -> context.prefs.enableCrashReporting = isChecked }
    switchAnalytics.setOnCheckedChangeListener { _, isChecked -> context.prefs.enableAnalytics = isChecked }
    btnAccept.setOnClickListener {
      context.prefs.hasAcceptedGdpr = true
      dismiss()
    }
  }

  override fun dismiss() {
    super.dismiss()

    FirebasePerformance.getInstance().isPerformanceCollectionEnabled = context.prefs.enablePerformance
    FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(context.prefs.enableAnalytics)
    Fabric.with(context, CrashlyticsCore.Builder()
      .disabled(!context.prefs.enableCrashReporting)
      .build())
  }

  companion object {
    fun show(context: Context, forceShow: Boolean = false) {
      if (forceShow || !context.prefs.hasAcceptedGdpr) {
        GdprBottomSheet(context).show()
      }
    }
  }
}