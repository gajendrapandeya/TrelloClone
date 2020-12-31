package com.codermonkeys.trelloclone.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.models.Board
import org.w3c.dom.Text

open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_board, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model.image, model.name, model.createdBy)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, model)
        }
    }

    override fun getItemCount() = list.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val boardImage = itemView.findViewById<ImageView>(R.id.iv_board_image)
        private val title = itemView.findViewById<TextView>(R.id.tv_name)
        private val createdBy = itemView.findViewById<TextView>(R.id.tv_created_by)

        @SuppressLint("SetTextI18n")
        fun bind(imageUrl: String, boardTitle: String, boardCreator: String) {
            Glide.with(context).load(imageUrl).centerCrop()
                .placeholder(R.drawable.ic_board_place_holder).into(boardImage)

            title.text = boardTitle
            createdBy.text = "Created by: $boardCreator"
        }
    }

}