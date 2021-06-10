package com.example.memeit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WallPaperAdapter(private val Listener: WallPaperClicked):RecyclerView.Adapter<WallPaperAdapter.ViewHolder>() {
    private val WallpaperSet: ArrayList<WallPaper> = ArrayList()
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val WallPaperImage:ImageView = itemView.findViewById(R.id.WallPaperCover)
        val ArtistName:TextView = itemView.findViewById(R.id.ArtistName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wall_paper_item, parent, false)
        val theViewHolderforWallpaper = ViewHolder(view)
        view.setOnClickListener {
           Listener.onItemClicked(WallpaperSet[theViewHolderforWallpaper.adapterPosition])
        }
        return theViewHolderforWallpaper
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentWallPaperItems = WallpaperSet[position]
        holder.ArtistName.text = currentWallPaperItems.photographer
        Glide
            .with(holder.itemView.context)
            .load(currentWallPaperItems.url)
            .into(holder.WallPaperImage)
    }
    fun updateWallpaper(updateWallpaper: ArrayList<WallPaper>) {
        WallpaperSet.clear()
        WallpaperSet.addAll(updateWallpaper)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return WallpaperSet.size
    }
    interface WallPaperClicked{
        fun onItemClicked(item: WallPaper)
    }
}