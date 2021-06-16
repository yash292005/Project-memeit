package com.example.memeit

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.giphy.sdk.analytics.GiphyPingbacks.context
import kotlinx.android.synthetic.main.fragment_redditfragment.*
import java.io.File


class redditfragment : Fragment(), MyAdapter.theItemClicked {
    private lateinit var mAdapter: MyAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val manager = LinearLayoutManager(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_redditfragment, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.MemeView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = manager
        fetchData("dankmemes")
        mAdapter = MyAdapter(this)
        recyclerView?.adapter = mAdapter
        return rootView

    }


    override fun onItemClicked(item: Memes) {
        val builder = CustomTabsIntent.Builder()
        val CustomTabsIntent = builder.build()
        CustomTabsIntent.launchUrl(this.requireContext(), Uri.parse(item.url))
    }


    override fun onLongItemClicked(item: Memes) {

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.search_bar)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchData(query)
                    Toast.makeText(context, "this is working", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun fetchData(Memes: String) {

        val urll = "https://meme-api.herokuapp.com/gimme/$Memes/50"
        GifProgressBar?.visibility = View.VISIBLE
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, urll, null,
            { response ->
                val memeJsonArray = response.getJSONArray("memes")
                val memeArray = ArrayList<Memes>()
                for (i in 0 until memeJsonArray.length()) {
                    val memeJsonObject = memeJsonArray.getJSONObject(i)
                    val meme = Memes(
                        memeJsonObject.getString("subreddit"),
                        memeJsonObject.getString("title"),
                        memeJsonObject.getString("url")
                    )
                    memeArray.add(meme)
                }
                mAdapter.updateMeme(memeArray)
                GifProgressBar?.visibility = View.GONE
            },
            { error ->
                GifProgressBar?.visibility = View.GONE
                val intent = Intent(context, errorActivity::class.java)
                startActivity(intent)
            }
        )
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }


    }

}


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
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this meme ${currentItems.url}")
                startActivity(it.context, Intent.createChooser(i, "share this meme with"), Bundle())

            }

            DownloadListener.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        it.context,
                        WRITE_EXTERNAL_STORAGE
                    ) != (PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(
                        it.context,
                        WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        it.context as Activity,
                        arrayOf(READ_EXTERNAL_STORAGE),
                        123
                    )
                }
                val currentItems = dataSet[adapterPosition]
                downloadImage("Memeit Image", currentItems.url)


            }
            poster.setOnClickListener {
                val builder = CustomTabsIntent.Builder()
                val CustomTabsIntent = builder.build()
                CustomTabsIntent.launchUrl(it.context, Uri.parse("https://www.reddit.com/"))
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
                Toast.makeText(context, "Image download started.", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show()
            }
        }


    }
//

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
        fun onLongItemClicked(item: Memes)
    }

}