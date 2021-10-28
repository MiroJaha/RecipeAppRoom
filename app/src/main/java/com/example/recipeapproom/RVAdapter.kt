package com.example.recipeapproom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapproom.databinding.RecipesViewBinding

class RVAdapter (private val informationList: ArrayList<Information>): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {

    private lateinit var myListener: OnItemClickListener

    class ItemViewHolder(val binding: RecipesViewBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener:OnItemClickListener ){
        myListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(RecipesViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            myListener
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val title = informationList[position].title
        val author = informationList[position].author

        holder.binding.apply {
            titleTV.text = "Recipe Name: $title"
            authorTV.text = "Author Name: $author"
        }
    }

    override fun getItemCount() = informationList.size
}