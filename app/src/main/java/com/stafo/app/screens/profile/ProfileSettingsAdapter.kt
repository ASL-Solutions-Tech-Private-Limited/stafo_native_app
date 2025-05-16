package com.stafo.app.screens.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R

class ProfileSettingsAdapter(
    private val items: List<ProfileListItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ProfileListItem.SectionHeader -> TYPE_HEADER
            is ProfileListItem.SettingItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile_row, parent, false)
            SettingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ProfileListItem.SectionHeader -> (holder as HeaderViewHolder).bind(item)
            is ProfileListItem.SettingItem -> (holder as SettingViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTitle: TextView = itemView.findViewById(R.id.sectionTitle)

        fun bind(item: ProfileListItem.SectionHeader) {
            sectionTitle.text = item.title
        }
    }

    inner class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.settingIcon)
        private val title: TextView = itemView.findViewById(R.id.settingTitle)

        fun bind(item: ProfileListItem.SettingItem) {
            title.text = item.title
            icon.setImageResource(item.iconResId)

            itemView.setOnClickListener {
                onItemClick(item.title) // Use title as ID or switch with an enum
            }
        }
    }

    sealed class ProfileListItem {
        data class SectionHeader(val title: String) : ProfileListItem()
        data class SettingItem(val title: String, val iconResId: Int) : ProfileListItem()
    }


}
