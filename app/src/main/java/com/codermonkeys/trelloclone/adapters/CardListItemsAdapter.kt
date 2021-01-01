package com.codermonkeys.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codermonkeys.trelloclone.databinding.ItemCardBinding
import com.codermonkeys.trelloclone.models.Card

class CardListItemsAdapter(
    private val context: Context,
    private val lists: ArrayList<Card>
): RecyclerView.Adapter<CardListItemsAdapter.MyViewHolder>() {

    companion object {
        private const val TAG = "CardListItemsAdapter"
    }

    private var onClickListener: OnClickListner? = null

    interface OnClickListner {
        fun onClick(position: Int, card: Card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = lists[position]
        with(holder) {
            binding.tvCardName.text = model.name
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    inner class MyViewHolder(val binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root)
}