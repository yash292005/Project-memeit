package com.example.memeit

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.giphy.sdk.analytics.GiphyPingbacks
import com.giphy.sdk.analytics.GiphyPingbacks.context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.io.File
import java.security.AccessController.getContext

class MyAdapter(private val Listener: theItemClicked) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val dataSet: ArrayList<Memes> = ArrayList()


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val memeTitle: TextView = itemView.findViewById(R.id.MemeName)
        val poster: TextView = itemView.findViewById(R.id.Subreddit)
        val coverImage: ImageView = itemView.findViewById(R.id.MemeImage)
        val ShareListener: ImageView = itemView.findViewById(R.id.ShareButton)
        val DownloadListener: ImageView = itemView.findViewById(R.id.DownloadButton)

        init {
            ShareListener.setOnClickListener {
                val currentItems = dataSet[adapterPosition]
                val currentImageItems = dataSet[adapterPosition]
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this WallPaper ${currentImageItems.url}")
                ContextCompat.startActivity(
                    it.context,
                    Intent.createChooser(i, "share this WallPaper with"),
                    Bundle()
                )

            }

            DownloadListener.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != (PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        it.context as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        123
                    )
                    val currentItems = dataSet[adapterPosition]
                    downloadImage("${System.currentTimeMillis()}", currentItems.url)
                }
                val currentItems = dataSet[adapterPosition]
                downloadImage("${System.currentTimeMillis()}", currentItems.url)


            }
            poster.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                val CustomTabsIntent = builder.build()
                CustomTabsIntent.launchUrl(
                    it.context,
                    Uri.parse("https://www.reddit.com/")
                )
            }


        }

        private fun downloadImage(filename: String, downloadUrlOfImage: String) {
            try {
                val dm: DownloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
                Toast.makeText(
                    context,
                    "Image download started.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT)
                    .show()
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.reddit_meme_content, parent, false)
        val theViewHolder = ViewHolder(view)
        view.setOnClickListener {
            Listener.onItemClicked(dataSet[theViewHolder.adapterPosition])
        }
        return theViewHolder
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItems = dataSet[position]
        holder.memeTitle.text = currentItems.title
        holder.poster.text = currentItems.subreddit

        Glide.with(holder.itemView.context).load(currentItems.url).into(holder.coverImage)

    }


    fun updateMeme(updateMemes: ArrayList<Memes>) {
        dataSet.clear()
        dataSet.addAll(updateMemes)
        notifyDataSetChanged()
    }


    interface theItemClicked {
        fun onItemClicked(item: Memes)
    }
}