package com.example.memeit


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.fragment_redditfragment.*
import timber.log.Timber
import kotlin.collections.ArrayList


class redditfragment : Fragment(), MyAdapter.theItemClicked {
    private lateinit var mAdapter: MyAdapter
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var mAdView : AdView
//    private final var TAG = "redditfragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val manager = LinearLayoutManager(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_redditfragment, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.MemeView)
        var adRequest = AdRequest.Builder().build()
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = manager
        fetchData("dankmemes")
        mAdapter = MyAdapter(this)
        recyclerView?.adapter = mAdapter
        MobileAds.initialize(context)
        val adView = AdView(context)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        mAdView = rootView.findViewById<AdView>(R.id.adView)
        mAdView.loadAd(adRequest
        )
        return rootView

    }
    override fun onItemClicked(item: Memes) {
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
                if (error is NetworkError){
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    GifProgressBar?.visibility = View.GONE
                    val intent = Intent(context, errorActivity::class.java)
                    startActivity(intent)
                }else if (error is ParseError){
                    Toast.makeText(context, "No Results Found", Toast.LENGTH_SHORT).show()
                    GifProgressBar?.visibility = View.GONE
                }else if (error is ServerError){
                    Toast.makeText(context, "NO Results Found", Toast.LENGTH_SHORT).show()
                    GifProgressBar?.visibility = View.GONE
                }

            }
        )
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }


    }

}


