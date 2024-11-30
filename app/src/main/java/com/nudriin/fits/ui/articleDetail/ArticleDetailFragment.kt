package com.nudriin.fits.ui.articleDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.nudriin.fits.R
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {
    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var article: ArticleItem
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleData = ArticleDetailFragmentArgs.fromBundle(arguments as Bundle)
        article = ArticleItem(
            id = articleData.articleId,
            title = articleData.title,
            author = articleData.author,
            content = articleData.content,
            imgUrl = articleData.imgUrl,
            createdAt = articleData.date
        )

        setupView()
    }

    private fun setupView() {
        Glide.with(this).load(article.imgUrl).into(binding.ivArticleDetailThumbnail)

        with(binding) {
            tvArticleDetailTitle.text = article.title
            tvArticleDetailAuthor.text = article.author

//          TODO(Change to markdown)
            tvArticleDetailContent.text =
                HtmlCompat.fromHtml(article.content, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

}