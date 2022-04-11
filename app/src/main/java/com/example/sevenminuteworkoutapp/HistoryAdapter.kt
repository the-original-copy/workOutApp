package com.example.sevenminuteworkoutapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sevenminuteworkoutapp.databinding.ItemDateHistoryBinding

class HistoryAdapter(private val items:ArrayList<String>):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(binding:ItemDateHistoryBinding):RecyclerView.ViewHolder(binding.root){
        val llLinearLayout = binding.llHistoryItemMain
        val tvPosition = binding.tvPosition
        val tvItem = binding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDateHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get data that will be attached to the UI component
        val date:String = items.get(position)
        //Attach data to UI component
        holder.tvPosition.text = (position + 1).toString()
        holder.tvItem.text = date

        //Different colors of list items
        if(position % 2 == 0){
            holder.llLinearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.colorLightGray))
        }else{
            holder.llLinearLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}