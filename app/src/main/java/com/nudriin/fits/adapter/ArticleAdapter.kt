package com.nudriin.fits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.databinding.ArticleHomeCardBinding

class ArticleAdapter(private val articleList: List<ArticleItem>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ArticleHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articleList[position]
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

    class ViewHolder(private val binding: ArticleHomeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(article.imgUrl)
                    .into(ivArticleHomeThumbnail)

                tvArticleHomeTitle.text = article.title

                // TODO(Change this to load markdown)
                tvArticleHomeDescription.text = HtmlCompat.fromHtml(
                    article.content,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
        }

    }
}