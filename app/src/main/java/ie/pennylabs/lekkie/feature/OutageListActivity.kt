package ie.pennylabs.lekkie.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import ie.pennylabs.lekkie.R
import ie.pennylabs.lekkie.api.ApiService
import ie.pennylabs.lekkie.data.model.OutageDao
import ie.pennylabs.lekkie.feature.map.OutageMapActivity
import kotlinx.android.synthetic.main.activity_outage_list.*
import javax.inject.Inject

class OutageListActivity : AppCompatActivity() {
  private val recyclerAdapter by lazy { OutageListRecyclerAdapter() }

  @Inject
  lateinit var api: ApiService
  @Inject
  lateinit var outageDao: OutageDao

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_outage_list)

    val viewModel = OutageListViewModel(api, outageDao)
    rvOutages.apply {
      adapter = recyclerAdapter
      layoutManager = LinearLayoutManager(this@OutageListActivity)
      viewModel.liveData.observe(this@OutageListActivity, Observer { recyclerAdapter.submitList(it) })
    }

    btnMap.setOnClickListener { startActivity(Intent(this@OutageListActivity, OutageMapActivity::class.java)) }
  }
}