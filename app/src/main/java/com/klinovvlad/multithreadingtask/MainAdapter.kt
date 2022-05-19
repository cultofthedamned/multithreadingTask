package com.klinovvlad.multithreadingtask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.klinovvlad.multithreadingtask.databinding.ItemBinding

class MainAdapter : RecyclerView.Adapter<MainAdapter.MainHolder>() {
    private val numbersList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            ItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(numbersList[position])
    }

    override fun getItemCount(): Int {
        return numbersList.size
    }

    fun addNumber(number: Int) {
        numbersList.add(number)
        notifyDataSetChanged()
    }

    class MainHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(numberInt: Int) {
            binding.textItemName.text = numberInt.toString()
        }
    }

}