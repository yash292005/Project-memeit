@file:Suppress("PrivatePropertyName")

package com.example.memeit

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import java.io.File

@Suppress("PropertyName")
class WallPaperAdapter(private val Listener: WallPaperClicked):RecyclerView.Adapter<WallPaperAdapter.ViewHolder>() {
    private val WallpaperSet: ArrayList<WallPaper> = ArrayList()
    @SuppressLint("NotifyDataSetChanged")
    @Suppress("LocalVariableName")
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val WallPaperImage:ImageView = itemView.findViewById(R.id.WallPaperCover)
        val ArtistName:TextView = itemView.findViewById(R.id.ArtistName)
        val DownloadButton: ImageView = itemView.findViewById(R.id.DownloadButtonWallpaper)
        val ShareButton: ImageView = itemView.findViewById(R.id.ShareButtonWallpaper)
        val DeleteButton: ImageView = itemView.findViewById(R.id.DeleteCard)
        val wallpaperAd:AdView = itemView.findViewById(R.id.adView2)
        val mAdView: AdView = itemView.findViewById(R.id.adView2)
        init {
            ShareButton.setOnClickListener {
                val currentImageItems = WallpaperSet[adapterPosition]
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this WallPaper ${currentImageItems.url}")
                startActivity(it.context, Intent.createChooser(i, "share this WallPaper with"), Bundle())

            }
            DeleteButton.setOnClickListener{
                WallpaperSet.removeAt(adapterPosition)
                notifyDataSetChanged()
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
        holder.wallpaperAd.visibility = View.GONE
        val adRequest = AdRequest.Builder().build()
        holder.mAdView.loadAd(adRequest)
        holder.mAdView.adListener = object : AdListener(){

            override fun onAdFailedToLoad(adError : LoadAdError) {
                holder.mAdView.loadAd(adRequest)
            }
        }
        if (position%3 == 0){
            holder.wallpaperAd.visibility = View.VISIBLE
        }
        Glide
            .with(holder.itemView.context)
            .load(currentWallPaperItems.url)
            .thumbnail(Glide.with(GiphyPingbacks.context).load(R.drawable.placeholder))
            .into(holder.WallPaperImage)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateWallpaper(updateWallpaper: ArrayList<WallPaper>) {
        WallpaperSet.addAll(updateWallpaper)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateWallpaperOnSearch(updateWallPaperOnSearch: ArrayList<WallPaper>){
        WallpaperSet.clear()
        WallpaperSet.addAll(updateWallPaperOnSearch)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return WallpaperSet.size
    }
    interface WallPaperClicked{
        fun onItemClicked(item: WallPaper)
    }
}