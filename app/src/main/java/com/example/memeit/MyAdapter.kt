package com.example.memeit

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.giphy.sdk.analytics.GiphyPingbacks.context
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.reddit_meme_content.view.*
import kotlinx.android.synthetic.main.reddit_video_meme_content.view.*
import java.io.File
import java.io.IOException

@Suppress("PropertyName", "LocalVariableName", "ClassName", "PrivatePropertyName")
class MyAdapter(private val Listener: theItemClicked) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val IS_VIDEO = 0
    private val IS_IMAGE = 1
    private val dataSet: ArrayList<Memes> = ArrayList()


    @SuppressLint("NotifyDataSetChanged")
    inner class VideoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

        val ShareListener_Video: ImageView? = itemView?.findViewById(R.id.imageView2)
        val DownloadListener_Video: ImageView? = itemView?.findViewById(R.id.imageView3)
        val DeleteListener_Video: ImageView? = itemView?.findViewById(R.id.imageView3)
        val PauseListener: ImageView? = itemView?.findViewById(R.id.Pause)
        val PlayListener: ImageView? = itemView?.findViewById(R.id.Play)
        val videoview: VideoView? = itemView?.findViewById(R.id.Meme_Video)
        init {
            val currentVideoItems = dataSet[position]
            val vidUrl: Uri? = Uri.parse(currentVideoItems.url)
            val audioUrl = currentVideoItems.audio_url
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(audioUrl)

            videoview?.setVideoURI(vidUrl)
            PauseListener?.visibility = View.GONE
            PlayListener?.setOnClickListener {
                try {
                    videoview?.start()
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    PlayListener.visibility = View.GONE
                    PauseListener?.visibility = View.VISIBLE
                }catch (e: IOException){
                    Toast.makeText(it.context, "Can't Play The File...", Toast.LENGTH_SHORT).show()
                }
            }
            PauseListener?.setOnClickListener {
                if (mediaPlayer.isPlaying){
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    PauseListener.visibility = View.GONE
                    PlayListener?.visibility = View.VISIBLE
                }
            }
            ShareListener_Video?.setOnClickListener {
                val currentVideoItems = dataSet[adapterPosition]
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this Video Meme ${currentVideoItems.url}")
                ContextCompat.startActivity(
                    it.context,
                    Intent.createChooser(i, "share this Meme with"),
                    Bundle()
                )
            }
            DownloadListener_Video?.setOnClickListener {
                val currentItems = dataSet[adapterPosition]
                downloadVideo("${System.currentTimeMillis()}", currentItems.url)
            }
            DeleteListener_Video?.setOnClickListener {
                dataSet.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
        }

        private fun downloadVideo(filename: String, downloadUrlOfImage: String){
            try {
                val dm: DownloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadUri = Uri.parse(downloadUrlOfImage)
                val request = DownloadManager.Request(downloadUri)
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("video/mp4") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        File.separator + filename + ".mp4"
                    )
                dm.enqueue(request)
                Toast.makeText(
                    context,
                    "Image download started.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    inner class ImageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val poster: TextView? = itemView?.findViewById(R.id.Subreddit)
        val ShareListener: ImageView? = itemView?.findViewById(R.id.ShareButton)
        val DownloadListener: ImageView? = itemView?.findViewById(R.id.DownloadButton)
        val DeleteListener: ImageView? = itemView?.findViewById(R.id.DeleteMemeCard)
        init {
            ShareListener?.setOnClickListener {
                val currentImageItems = dataSet[adapterPosition]
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this Meme ${currentImageItems.url}")
                ContextCompat.startActivity(
                    it.context,
                    Intent.createChooser(i, "share this Meme with"),
                    Bundle()
                )

            }
            DeleteListener?.setOnClickListener{
                dataSet.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
            DownloadListener?.setOnClickListener {

                val currentItems = dataSet[adapterPosition]
                downloadImage("${System.currentTimeMillis()}", currentItems.url)


            }
            poster?.setOnClickListener {
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
                Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT)
                    .show()
            }

        }


    }

    override fun getItemViewType(position: Int): Int {
        val currentItems = dataSet[position]
        return if (currentItems.url.takeLast(3) == "mp4"){
            IS_VIDEO
        }else{
            IS_IMAGE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            IS_IMAGE -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.reddit_meme_content, parent, false)
                val theViewHolder = ImageViewHolder(view)
                view.setOnClickListener {
                    Listener.onItemClicked(dataSet[theViewHolder.adapterPosition])
                }
                return theViewHolder
            }
            else -> {
                val view2 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.reddit_video_meme_content, parent, false)
                return ImageViewHolder(view2)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMeme(updateMemes: ArrayList<Memes>) {
        dataSet.addAll(updateMemes)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateMemeOnSearch(updateMemesOnSearch: ArrayList<Memes>){
        dataSet.clear()
        dataSet.addAll(updateMemesOnSearch)
        notifyDataSetChanged()
    }
    interface theItemClicked {
        fun onItemClicked(item: Memes)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder){
            val currentItems = dataSet[position]
            holder.itemView.MemeName?.text = currentItems.title
            holder.itemView.Subreddit?.text = currentItems.subreddit
            holder.itemView.adView?.visibility = View.GONE
            val memeImage: ImageView? = holder.itemView.findViewById(R.id.MemeImage)
            val adRequest = AdRequest.Builder().build()
            MobileAds.initialize(context)
            val adView = AdView(context)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
            holder.itemView.adView?.loadAd(adRequest)
            holder.itemView.adView?.adListener = object : AdListener() {

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    holder.itemView.adView?.loadAd(adRequest)
                }
            }
            if (position%3 == 0){
                holder.itemView.adView?.visibility = View.VISIBLE
            }
            if (memeImage != null) {
                Glide
                    .with(holder.itemView.context)
                    .load(currentItems.url)
                    .thumbnail(Glide.with(context).load(R.drawable.placeholder))
                    .into(memeImage)
            }
        }else if (holder is VideoViewHolder){
            val currentVideoItems = dataSet[position]
            holder.itemView.text1.text = currentVideoItems.title
            holder.itemView.text2.text = currentVideoItems.subreddit
            val vidUrl: Uri? = Uri.parse(currentVideoItems.url)
            val audioUrl = currentVideoItems.audio_url
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(audioUrl)
            val videoview: VideoView = holder.itemView.Meme_Video
            videoview.setVideoURI(vidUrl)


        }

    }
}


