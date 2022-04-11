package com.example.sevenminuteworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sevenminuteworkoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class History : AppCompatActivity() {
    private var binding : ActivityHistoryBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarHistoryActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "History"
        }
        binding?.toolbarHistoryActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

        val historyDao = (application as WorkOutApp).db.historyDao()
        getAllWorkouts(historyDao)
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun getAllWorkouts(historyDao: HistoryDao){
        lifecycleScope.launch{
            historyDao.fetchAllDates().collect { allDates ->
                if(allDates.isNotEmpty()){
                    binding?.tvHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoDataAvailable?.visibility = View.INVISIBLE

                    //Setup layoutManager
                    //the context has to be specific
                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@History)
                    //Set up adapter
                    val dates = ArrayList<String>()
                    //Create an ArrayList of strings that the adapter will use since allDates is a list of HistoryEntities
                    for(date in allDates){
                        //Date is the String value
                        dates.add(date.date)
                    }
                    val historyAdapter = HistoryAdapter(dates)
                    binding?.rvHistory?.adapter = historyAdapter

                }else{
                    binding?.tvHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}