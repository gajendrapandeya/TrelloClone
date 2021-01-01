package com.codermonkeys.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.databinding.ItemMemberBinding
import com.codermonkeys.trelloclone.models.User

class MemberListItemAdapter(
    private val context: Context,
    private val list: ArrayList<User>
) : RecyclerView.Adapter<MemberListItemAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        with(holder) {
            Glide.with(context).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder).into(binding.ivMemberImage)

            binding.tvMemberName.text = model.name
            binding.tvMemberEmail.text = model.email
        }
    }

    override fun getItemCount() = list.size

    inner class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
}