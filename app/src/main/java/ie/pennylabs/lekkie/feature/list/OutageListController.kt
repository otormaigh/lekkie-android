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

import android.location.Geocoder
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.christianbahl.conductor.ConductorInjection
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.api.ApiService
import ie.pennylabs.lekkie.arch.BaseController
import ie.pennylabs.lekkie.data.model.OutageDao
import ie.pennylabs.lekkie.toolbox.extension.hideKeyboard
import ie.pennylabs.lekkie.toolbox.extension.showKeyboard
import ie.pennylabs.lekkie.toolbox.extension.viewModelProvider
import kotlinx.android.synthetic.main.controller_outage_list.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class OutageListController : BaseController(), TextWatcher {
  private val recyclerAdapter by lazy { OutageListRecyclerAdapter() }
  private val viewModel by viewModelProvider { OutageListViewModel(api, outageDao, geocoder) }
  private val constraintSetShowSearch by lazy {
    ConstraintSet().apply {
      clone(view?.parentViewGroup)
      connect(R.id.ivSearch, START, PARENT_ID, START)
      connect(R.id.ivSearch, END, R.id.etQuery, START)
      connect(R.id.etQuery, START, R.id.ivSearch, END)
      connect(R.id.etQuery, END, R.id.ivClear, START)
      connect(R.id.ivClear, START, R.id.etQuery, END)
      connect(R.id.ivClear, END, PARENT_ID, END)
    }
  }
  private val constraintSetHideSearch by lazy {
    ConstraintSet().apply {
      clone(view?.parentViewGroup)
      clear(R.id.ivSearch, START)
      connect(R.id.ivSearch, END, PARENT_ID, END)
      clear(R.id.etQuery, END)
      connect(R.id.etQuery, START, PARENT_ID, END)
      clear(R.id.ivClear, END)
      connect(R.id.ivClear, START, PARENT_ID, END)
    }
  }

  @Inject
  lateinit var api: ApiService
  @Inject
  lateinit var outageDao: OutageDao
  @Inject
  lateinit var geocoder: Geocoder

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View =
    inflater.inflate(R.layout.controller_outage_list, container, false)

  override fun onAttach(view: View) {
    ConductorInjection.inject(this)
    super.onAttach(view)

    view.swipeRefresh.setOnRefreshListener {
      view.swipeRefresh.isRefreshing = false
      viewModel.fetchOutages()
      hideSearch(view)
    }

    view.rvOutages.apply {
      adapter = recyclerAdapter
      layoutManager = LinearLayoutManager(view.context)
      viewModel.outages.observe(this@OutageListController, Observer { liveData ->
        liveData.observe(this@OutageListController, Observer { outages ->
          recyclerAdapter.submitList(outages)

          launch(onDetachJob) { outages.forEach { outage -> viewModel.updateOutageCounty(outage) } }
        })
      })
    }

    view.ivSearch.setOnClickListener {
      TransitionManager.beginDelayedTransition(view.parentViewGroup)
      constraintSetShowSearch.applyTo(view.parentViewGroup)

      launch(UI, parent = onDetachJob) {
        delay(300)
        view.etQuery.apply {
          requestFocus()
          showKeyboard()
          addTextChangedListener(this@OutageListController)
          setOnKeyListener { _, _, keyEvent ->
            when (keyEvent.keyCode) {
              KeyEvent.KEYCODE_ENTER -> {
                viewModel.queryQao(text.toString())
                hideKeyboard()
                true
              }
              else -> false
            }
          }
        }
      }
    }

    view.ivClear.setOnClickListener { hideSearch(view) }
  }

  override fun afterTextChanged(text: Editable?) {
    if (text.isNullOrEmpty()) {
      viewModel.queryQao(null)
      view?.ivClear?.visibility = View.INVISIBLE
    } else {
      view?.ivClear?.visibility = View.VISIBLE
    }
  }

  override fun beforeTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
    // IGNORE
  }

  override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
    launch(onDetachJob) {
      delay(300)
      viewModel.queryQao(text.toString())
    }
  }

  private fun hideSearch(view: View) {
    TransitionManager.beginDelayedTransition(view.parentViewGroup)
    constraintSetHideSearch.applyTo(view.parentViewGroup)

    launch(UI, parent = onDetachJob) {
      afterTextChanged(null)
      view.etQuery.apply {
        text.clear()
        hideKeyboard()
      }
    }
  }
}
