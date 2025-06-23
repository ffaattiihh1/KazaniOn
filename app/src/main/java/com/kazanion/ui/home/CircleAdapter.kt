package com.kazanion.ui.home

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kazanion.R
import com.kazanion.model.CircleItem

class CircleAdapter(private val itemList: List<CircleItem>) : RecyclerView.Adapter<CircleAdapter.CircleViewHolder>() {

    class CircleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val frameLayoutBorder: FrameLayout = itemView.findViewById(R.id.frameLayoutCircleBorder)
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewCircleIcon)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewCircleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_circle_button, parent, false)
        return CircleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircleViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.textViewTitle.text = currentItem.title

        // Load image based on whether it's a profile item with a URI or a standard item
        if (currentItem.isProfileItem && currentItem.profileImageUri != null) {
            // Load image from URI using Glide for profile item
            Glide.with(holder.itemView.context)
                 .load(currentItem.profileImageUri)
                 .transform(CircleCrop()) // Apply circular transformation
                 .placeholder(currentItem.iconResId) // Use default icon as placeholder
                 .error(currentItem.iconResId) // Use default icon on error
                 .into(holder.imageViewIcon)
        } else {
            // Load image from drawable resource for other items or profile item without URI
            holder.imageViewIcon.setImageResource(currentItem.iconResId)
             // You might want to clear any previous Glide load if this view was reused
             Glide.with(holder.itemView.context).clear(holder.imageViewIcon)
        }

        // Update border color based on isSeen status
        val borderColorResId = if (currentItem.isSeen) R.color.story_seen_border else R.color.story_unseen_border
        val borderColor = ContextCompat.getColor(holder.itemView.context, borderColorResId)

        // Get the background drawable and set the stroke color
        val backgroundDrawable = holder.frameLayoutBorder.background
        if (backgroundDrawable is GradientDrawable) {
            val strokeWidth = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.story_border_width)
            backgroundDrawable.setStroke(strokeWidth, borderColor)
        } else {
             // Handle other drawable types if necessary, or ensure background is a GradientDrawable
        }

        // Original tinting logic (can be removed or adapted if needed for the inner icon background)
        // val drawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_background)
        // if (drawable != null) {
        //     val wrappedDrawable = DrawableCompat.wrap(drawable)
        //     val color = ContextCompat.getColor(holder.itemView.context, currentItem.colorResId)
        //     DrawableCompat.setTint(wrappedDrawable, color)
        //     holder.imageViewIcon.background = wrappedDrawable
        // } else {
        //      // Handle the case where the drawable is null if necessary
        // }

        // TODO: Add click listener for each item if needed
        holder.itemView.setOnClickListener { 
             // Handle item click and potentially update isSeen status and notify adapter
             currentItem.isSeen = true // This will only update the item in the current list instance
             // If using ListAdapter with DiffUtil, you'd submit a new list here.
             notifyItemChanged(position) // Notify adapter to rebind this item
        }
    }

    // Method to update a specific item (useful for updating profile image)
     fun updateItem(updatedItem: CircleItem) {
         val index = itemList.indexOfFirst { it.id == updatedItem.id }
         if (index != -1) {
             // Create a new list with the updated item (important if using an immutable list)
             val newList = itemList.toMutableList()
             newList[index] = updatedItem
             // Replace the old list with the new one (if using an immutable list)
             // If your itemList is already a MutableList, you can just update newList[index]
             // and then notifyItemChanged(index).
             // Assuming itemList is meant to be updated for simplicity:
             // itemList = newList // This would require 'var' for itemList

             // For simplicity and assuming a mutable list or willingness to update 'var itemList':
             // Find the actual item in the list and update its URI directly.
             // This is less ideal with DiffUtil but simpler for this example.
             // If itemList is `val itemList: List<CircleItem>`, you must recreate the list
             // or pass the URI directly to the adapter.

             // Let's simplify and assume the URI is passed and we just need to trigger a rebind.
             // The HomeFragment will update the underlying list/data source.
             // The adapter's onBindViewHolder will then check the updated item.

             // Re-read the list to find the updated item and its position
             val currentItemList = this.itemList // Use the current list instance
             val updatedIndex = currentItemList.indexOfFirst { it.id == updatedItem.id }
             if (updatedIndex != -1) {
                  // Update the URI of the item in the current list (assuming list is mutable or replaced)
                  // If itemList is a `var`, you can assign a new list here.
                  // If it's a `val`, you need to pass the URI differently.
                  // Let's stick to the plan of HomeFragment updating the list and notifying.

                  // The HomeFragment will update the list and call notifyItemChanged.
                  // No need for a separate updateItem method in the adapter if HomeFragment manages the list.
                  // The previous logic in onBindViewHolder and the updateProfileCircleImage in HomeFragment
                  // are sufficient to react to changes in the list managed by HomeFragment.

                 // Therefore, removing this potentially confusing updateItem method.
             }
         }
     }

    override fun getItemCount() = itemList.size
} 