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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.ViewModelController
import com.christianbahl.conductor.ConductorInjection
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.api.ApiService
import ie.pennylabs.lekkie.data.model.OutageDao
import ie.pennylabs.lekkie.toolbox.extension.hideKeyboard
import ie.pennylabs.lekkie.toolbox.extension.showKeyboard
import ie.pennylabs.lekkie.toolbox.extension.viewModelProvider
import kotlinx.android.synthetic.main.controller_outage_list.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class OutageListController : ViewModelController(), TextWatcher {
  private val recyclerAdapter by lazy { OutageListRecyclerAdapter() }
  private val viewModel by viewModelProvider { OutageListViewModel(api, outageDao, geocoder) }
  private val queryJob = Job()

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

    view.rvOutages.apply {
      adapter = recyclerAdapter
      layoutManager = LinearLayoutManager(view.context)
      viewModel.liveData.observe(this@OutageListController, Observer {
        recyclerAdapter.submitList(it)
      })
    }

    view.swipeRefresh.setOnRefreshListener {
      view.etQuery.isEnabled = false
      view.etQuery.hideKeyboard()
      view.swipeRefresh.isRefreshing = false
      viewModel.fetchOutages()
    }

    view.ivSearch.setOnClickListener {
      view.etQuery.requestFocus()
      view.etQuery.isEnabled = true
      view.etQuery.showKeyboard()
      afterTextChanged(null)
    }

    view.ivClear.setOnClickListener {
      afterTextChanged(null)
      view.etQuery.apply {
        text.clear()
        isEnabled = false
        hideKeyboard()
      }
    }

    view.etQuery.apply {
      addTextChangedListener(this@OutageListController)
      setOnKeyListener { view, _, keyEvent ->
        when (keyEvent.keyCode) {
          KeyEvent.KEYCODE_ENTER -> {
            view.isEnabled = false
            view.hideKeyboard()
            viewModel.queryQao(view.etQuery.text.toString())
            true
          }
          else -> false
        }
      }
    }
  }

  override fun onDetach(view: View) {
    super.onDetach(view)
    queryJob.cancel()
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
  }

  override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
    launch(queryJob) {
      delay(200)
      viewModel.queryQao(text.toString())
    }
  }
}
