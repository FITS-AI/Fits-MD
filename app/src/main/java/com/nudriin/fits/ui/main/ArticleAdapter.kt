package com.nudriin.fits.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.databinding.ArticleHomeCardBinding

class ArticleAdapter : ListAdapter<ArticleItem, ArticleAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ArticleHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(article.id)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(articleId: String)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleItem>() {
            override fun areItemsTheSame(
                oldItem: ArticleItem,
                newItem: ArticleItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticleItem,
                newItem: ArticleItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(private val binding: ArticleHomeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(article.imgUrl)
                    .into(ivArticleHomeThumbnail)

                tvArticleHomeTitle.text = article.title
                tvArticleHomeDescription.text = article.content
            }
        }

    }
}