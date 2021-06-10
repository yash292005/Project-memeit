package com.example.memeit

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_instafragment.*

class Instafragment : Fragment(), WallPaperAdapter.WallPaperClicked {
    private lateinit var mWallpaperAdapter: WallPaperAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)
        val manager = LinearLayoutManager(requireContext())
        val someview = inflater.inflate(R.layout.fragment_instafragment, container, false)
        val recyclerView = someview.findViewById<RecyclerView>(R.id.WallpaperView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = manager
        mWallpaperAdapter = WallPaperAdapter(this)
        recyclerView?.adapter = mWallpaperAdapter
        defaultWallpaper()
        return someview
    }
    private fun fetchWallpaper(SearchQuery: String) {
        val url = "https://api.pexels.com/v1/search?query=$SearchQuery&per_page=80"
        wallpaperLoader?.visibility = View.VISIBLE
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            { response ->
                val WallPaperJsonArray = response.getJSONArray("photos")
                val WallpaperArray = ArrayList<WallPaper>()
                for (i in 0 until WallPaperJsonArray.length()) {
                    val wallpaperJsonObject = WallPaperJsonArray.getJSONObject(i)
                    val photographerImage = wallpaperJsonObject.getString("photographer")
                    val imageResource = wallpaperJsonObject.getJSONObject("src")
                    val mainImage = imageResource.getString("original")
                    val wallpapers = WallPaper(mainImage, photographerImage)
                    WallpaperArray.add(wallpapers)
                }
                mWallpaperAdapter.updateWallpaper(WallpaperArray)
                wallpaperLoader?.visibility = View.GONE
            },
            {
                Toast.makeText(context, "some error occured", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "563492ad6f91700001000001b210d9c3015442cc813aa0233b3abc7a"
                return headers
            }
        }
        context?.let {
            MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest)
        }
    }
    override fun onItemClicked(item: WallPaper) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this Wallpaper ${item.url}")
        startActivity(Intent.createChooser(i, "Share this Wallpaper with"))
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.search_bar)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(context, "Searching for $query", Toast.LENGTH_SHORT).show()
                if (query != null) {
                    fetchWallpaper(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }
    private fun defaultWallpaper() {
        fetchWallpaper("Trending")
    }
}