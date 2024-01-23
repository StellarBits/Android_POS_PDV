package com.stellarbitsapps.androidpdv.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.TokensItemBinding
import com.stellarbitsapps.androidpdv.ui.tokens.TokensFragment

class TokensAdapter(
    private val tokensFragment: TokensFragment
) : ListAdapter<Tokens, TokensAdapter.TokensViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Tokens>() {
            override fun areItemsTheSame(oldItem: Tokens, newItem: Tokens): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Tokens, newItem: Tokens): Boolean {
                return oldItem == newItem
            }
        }
    }

    class TokensViewHolder(private val binding: TokensItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Tokens, tokensFragment: TokensFragment) {
            with(binding) {
                btToken.backgroundTintList = if (item.value >= 10f)
                    btToken.resources.getColorStateList(android.R.color.holo_blue_dark, null)
                else
                    btToken.resources.getColorStateList(android.R.color.holo_orange_dark, null)

                tvCounter.text = "0"
                tvCounter.visibility = View.GONE
                btToken.text = "R$ " + String.format("%.2f", item.value)
                token = item
                executePendingBindings()
            }

            binding.tvCounter.setOnClickListener {
                var count = binding.tvCounter.text.toString().toInt()
                count -= 1

                binding.tvCounter.visibility = if (count == 0) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                binding.tvCounter.text = count.toString()

                tokensFragment.tokenCounterClicked(item)
            }

            binding.btToken.setOnClickListener {
                binding.tvCounter.visibility = View.VISIBLE

                var count = binding.tvCounter.text.toString().toInt()
                count += 1

                binding.tvCounter.text = count.toString()

                tokensFragment.tokenClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokensViewHolder {
        return TokensViewHolder(TokensItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TokensViewHolder, position: Int) {
        val item = getItem(position) as Tokens
        holder.bind(item, tokensFragment)
    }
}