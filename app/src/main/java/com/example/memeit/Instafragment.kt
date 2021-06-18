package com.example.memeit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkError
import com.android.volley.ParseError
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.fragment_instafragment.*
import kotlinx.android.synthetic.main.fragment_redditfragment.*

class Instafragment : Fragment(), WallPaperAdapter.WallPaperClicked {
    lateinit var mAdView : AdView
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
        MobileAds.initialize(context)

        mAdView = someview.findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

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
                if (it is NetworkError){
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    wallpaperLoader?.visibility = View.GONE
                    val intent = Intent(context, errorActivity::class.java)
                    startActivity(intent)
                }else if (it is ParseError){
                    Toast.makeText(context, "No Results Found", Toast.LENGTH_SHORT).show()
                    wallpaperLoader?.visibility = View.GONE
                }else if (it is ServerError){
                    Toast.makeText(context, "NO Results Found", Toast.LENGTH_SHORT).show()
                    wallpaperLoader?.visibility = View.GONE
                }


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
        val builder = CustomTabsIntent.Builder()
        val CustomTabsIntent = builder.build()
        CustomTabsIntent.launchUrl(this.requireContext(), Uri.parse(item.url))
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