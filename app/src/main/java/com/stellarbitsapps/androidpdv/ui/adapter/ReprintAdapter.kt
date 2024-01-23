package com.stellarbitsapps.androidpdv.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.databinding.FragmentReprintItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReprintAdapter(
    private val reprintListener: ReprintListener
) : ListAdapter<Report, ReprintAdapter.ReprintViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Report>() {
            override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ReprintViewHolder(private val binding: FragmentReprintItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Report, reprintListener: ReprintListener) {
            with(binding) {
                report = item
                listener = reprintListener
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReprintViewHolder {
        return ReprintViewHolder(FragmentReprintItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReprintViewHolder, position: Int) {
        val item = getItem(position) as Report
        holder.bind(item, reprintListener)
    }

    fun addHeadersAndSubmitList(list: List<Report>?) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }
}