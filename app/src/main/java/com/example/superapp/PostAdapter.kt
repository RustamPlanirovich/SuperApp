package com.example.superapp

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.link_item.view.*

private const val POST_TYPE_DESC: Int = 0
private const val POST_TYPE_IMAGE: Int = 1

class PostAdapter(
    var postListItem: List<PostModel>,
    val clickListener: (PostModel) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class DescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bing(
            postModel: PostModel,
            clickListener: (PostModel) -> Unit
        ) {

            itemView.linkName.text = postModel.linkName
            itemView.linkAddress.text = postModel.addressLink

            itemView.goLinkButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postModel.addressLink))
                itemView.context.startActivity(browserIntent)
            }


            itemView.setOnClickListener {
                clickListener(postModel)
            }

        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bing(postModel: PostModel, clickListener: (PostModel) -> Unit) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == POST_TYPE_DESC) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.link_item, parent, false)
            return DescViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.link_item, parent, false)
            return ImageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return postListItem.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (postListItem[position].id.toInt() == 0) {
            POST_TYPE_DESC
        } else {
            POST_TYPE_IMAGE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == POST_TYPE_DESC) {
            (holder as DescViewHolder).bing(postListItem[position], clickListener)
        } else {
            (holder as ImageViewHolder).bing(postListItem[position], clickListener)
        }
    }

}
