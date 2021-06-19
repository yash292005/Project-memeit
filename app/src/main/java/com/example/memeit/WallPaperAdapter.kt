package com.example.memeit

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.giphy.sdk.analytics.GiphyPingbacks
import java.io.File
import java.sql.Time

class WallPaperAdapter(private val Listener: WallPaperClicked):RecyclerView.Adapter<WallPaperAdapter.ViewHolder>() {
    private val WallpaperSet: ArrayList<WallPaper> = ArrayList()
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val WallPaperImage:ImageView = itemView.findViewById(R.id.WallPaperCover)
        val ArtistName:TextView = itemView.findViewById(R.id.ArtistName)
        val DownloadButton: ImageView = itemView.findViewById(R.id.DownloadButtonWallpaper)
        val ShareButton: ImageView = itemView.findViewById(R.id.ShareButtonWallpaper)
        init {
            ShareButton.setOnClickListener {
                val currentImageItems = WallpaperSet[adapterPosition]
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this WallPaper ${currentImageItems.url}")
                startActivity(it.context, Intent.createChooser(i, "share this WallPaper with"), Bundle())

            }
            DownloadButton.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != (PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(
                        it.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        it.context as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        123
                    )
                    val currentImageItems = WallpaperSet[adapterPosition]
                    downloadImage("${System.currentTimeMillis()}", currentImageItems.url)
                }
                val currentImageItems = WallpaperSet[adapterPosition]
                downloadImage("${System.currentTimeMillis()}", currentImageItems.url)
            }
            ArtistName.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                val CustomTabsIntent = builder.build()
                CustomTabsIntent.launchUrl(it.context, Uri.parse("https://www.pexels.com/"))
            }
        }
        private fun downloadImage(filename: String, downloadUrlOfImage: String) {
            try {
                val dm: DownloadManager =
                    GiphyPingbacks.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadUri = Uri.parse(downloadUrlOfImage)
                val request = DownloadManager.Request(downloadUri)
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        File.separator + filename + ".jpg"
                    )
                dm.enqueue(request)
                Toast.makeText(GiphyPingbacks.context, "Image download started.", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(GiphyPingbacks.context, "Some Error Occured", Toast.LENGTH_SHORT).show()
            }
        }
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