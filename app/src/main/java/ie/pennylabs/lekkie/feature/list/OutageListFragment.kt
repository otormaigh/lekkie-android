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

import android.content.Context
import android.os.Bundle
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.TransitionManager
import dagger.android.support.AndroidSupportInjection
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.arch.BaseFragment
import ie.pennylabs.lekkie.toolbox.LazyViewModel
import ie.pennylabs.lekkie.toolbox.extension.hideKeyboard
import ie.pennylabs.lekkie.toolbox.extension.showKeyboard
import ie.pennylabs.lekkie.lib.data.worker.ApiWorker
import kotlinx.android.synthetic.main.controller_outage_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OutageListFragment : BaseFragment(), TextWatcher, SwipeRefreshLayout.OnRefreshListener {
  private val recyclerAdapter by lazy { OutageListRecyclerAdapter() }
  private val viewModel by LazyViewModel(OutageListViewModel::class.java)
  private val constraintSetShowSearch by lazy {
    ConstraintSet().apply {
      clone(parentViewGroup)
      connect(R.id.ivSearch, START, PARENT_ID, START)
      connect(R.id.ivSearch, END, R.id.etQuery, START)
      connect(R.id.etQuery, START, R.id.ivSearch, END)
      connect(R.id.etQuery, END, R.id.ivClear, START)
      connect(R.id.ivClear, START, R.id.etQuery, END)
      connect(R.id.ivClear, END, PARENT_ID, END)
      clear(R.id.tvOutageCount, START)
    }
  }
  private val constraintSetHideSearch by lazy {
    ConstraintSet().apply {
      clone(parentViewGroup)
      clear(R.id.ivSearch, START)
      connect(R.id.ivSearch, END, PARENT_ID, END)
      clear(R.id.etQuery, END)
      connect(R.id.etQuery, START, PARENT_ID, END)
      clear(R.id.ivClear, END)
      connect(R.id.ivClear, START, PARENT_ID, END)
      connect(R.id.tvOutageCount, START, PARENT_ID, START)
    }
  }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
    inflater.inflate(R.layout.controller_outage_list, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    swipeRefresh.setOnRefreshListener(this)
    rvOutages.apply {
      adapter = recyclerAdapter
      layoutManager = LinearLayoutManager(view.context)

      viewModel.outages.observe(this@OutageListFragment, Observer { outages ->
        recyclerAdapter.submitList(outages)
        tvOutageCount.text = getString(R.string.outage_count, outages.size)
      })
    }

    ivSearch.setOnClickListener {
      TransitionManager.beginDelayedTransition(parentViewGroup)
      constraintSetShowSearch.applyTo(parentViewGroup)

      launch(coroutineContext + Dispatchers.Main) {
        delay(300)
        etQuery.apply {
          requestFocus()
          showKeyboard()
          addTextChangedListener(this@OutageListFragment)
          setOnKeyListener { _, _, keyEvent ->
            when (keyEvent.keyCode) {
              KeyEvent.KEYCODE_ENTER -> {
                viewModel.filterOutages(text.toString())
                hideKeyboard()
              }
              else -> false
            }
          }
        }
      }
    }

    ivClear.setOnClickListener { hideSearch() }
  }

  override fun afterTextChanged(text: Editable?) {
    if (text.isNullOrEmpty()) {
      viewModel.filterOutages("")
      ivClear?.visibility = View.INVISIBLE
    } else {
      ivClear?.visibility = View.VISIBLE
    }
  }

  override fun beforeTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
    // IGNORE
  }

  override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
    launch {
      delay(300)
      viewModel.filterOutages(text.toString())
    }
  }

  override fun onRefresh() {
    ApiWorker.oneTimeRequest(requireContext()).observe(this, Observer { workInfo ->
      swipeRefresh.isRefreshing = !workInfo.state.isFinished
    })

    hideSearch()
  }

  private fun hideSearch() {
    TransitionManager.beginDelayedTransition(parentViewGroup)
    constraintSetHideSearch.applyTo(parentViewGroup)

    launch(coroutineContext + Dispatchers.Main) {
      afterTextChanged(null)
      etQuery.apply {
        text.clear()
        hideKeyboard()
        removeTextChangedListener(this@OutageListFragment)
      }
    }
  }
}
