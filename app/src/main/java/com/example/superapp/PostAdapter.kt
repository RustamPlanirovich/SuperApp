package com.example.superapp

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.link_item.view.*


private const val POST_TYPE_DESC: Int = 0
private const val POST_TYPE_IMAGE: Int = 1

class PostAdapter(
    var postListItem: List<PostModel>,
    var clickListener: (PostModel, View) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class DescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bing(
            postModel: PostModel,
            clickListener: (PostModel, View) -> Unit
        ) {

            //itemView.linkName.text = postModel.linkName
            itemView.linkAddress.text = postModel.addressLink
            itemView.commentTextView2.text = postModel.commentLink


            val handler = Handler()
            val runnable = Runnable {


                Glide
                    .with(itemView.context)
                    .load("https://www.google.com/s2/favicons?sz=64&domain_url=" + postModel.addressLink)
                    .circleCrop()
                    .into(itemView.imageView4)

            }
            handler.postDelayed(runnable, 200)


            itemView.fabMenu.setOnClickListener {
                if (itemView.fabGoLink.isVisible) {
                    itemView.fabGoLink.visibility = View.GONE
                    itemView.fabEditLink.visibility = View.GONE
                    itemView.fabDelButton.visibility = View.GONE
                } else {
                    itemView.fabGoLink.visibility = View.VISIBLE
                    itemView.fabEditLink.visibility = View.VISIBLE
                    itemView.fabDelButton.visibility = View.VISIBLE
                }
            }
            itemView.fabGoLink.setOnClickListener {
                clickListener(postModel, it)
            }
            itemView.fabEditLink.setOnClickListener {
                clickListener(postModel, it)
            }
            itemView.fabDelButton.setOnClickListener {
                clickListener(postModel, it)
            }

            itemView.setOnClickListener {
                if (itemView.commentTextView2.isVisible) {
                    itemView.commentTextView2.visibility = View.GONE
                } else {
                    itemView.commentTextView2.visibility = View.VISIBLE

                }
            }

        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bing(postModel: PostModel, clickListener: (PostModel, View) -> Unit) {

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
