package com.akangcupez.atask.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akangcupez.atask.databinding.ItemCalculationBinding
import com.akangcupez.atask.domain.model.Calculation

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var mList: MutableList<Calculation> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(calculations: List<Calculation>?) {
        mList.clear()
        calculations?.let {
            if (it.isNotEmpty()) mList.addAll(it)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCalculationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(calculation: Calculation) = with(binding) {
            tvItemCalculationInput.text = calculation.input
            val result = "Result ${calculation.result}"
            tvItemCalculationResult.text = result
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCalculationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }
}