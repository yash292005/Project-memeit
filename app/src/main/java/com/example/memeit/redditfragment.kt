package com.example.memeit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_redditfragment.*


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
                Toast.makeText(context, "some error occurred", Toast.LENGTH_SHORT).show()
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

        init {
           ShareListener.setOnClickListener{
               val currentItems = dataSet[adapterPosition]
               val item: Memes
               val i = Intent(Intent.ACTION_SEND)
               i.type = "text/plain"
               i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this meme ${currentItems.url}")
               startActivity(it.context, Intent.createChooser(i,"share this meme with"), Bundle())

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