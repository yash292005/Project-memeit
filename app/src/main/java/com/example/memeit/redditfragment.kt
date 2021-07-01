@file:Suppress("ClassName", "LocalVariableName", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.example.memeit


import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.fragment_redditfragment.*


class redditfragment : Fragment(), MyAdapter.theItemClicked {
    private lateinit var mAdapter: MyAdapter
    private lateinit var mAdView: AdView
    private var isScrolling: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val manager = LinearLayoutManager(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_redditfragment, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.MemeView)
        val adRequest = AdRequest.Builder().build()
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = manager
        mAdapter = MyAdapter(this)
        recyclerView?.adapter = mAdapter

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = manager.childCount
                val totalItems = manager.itemCount
                val scrollOutItems = manager.findFirstVisibleItemPosition()
                if (isScrolling and (currentItems + scrollOutItems >= totalItems - 4)) {
                    isScrolling = false
                    val randomItems = arrayOf("IndianMeyMeys", "indiameme", "memes", "india")
                    val randomMemes = randomItems.random()
                    fetchData(randomMemes)


                }
            }
        })

        GifProgressBar?.visibility = View.VISIBLE
        val randomItems = arrayOf(
            "AdviceAnimals",
            "MemeEconomy",
            "IndianDankMemes",
            "terriblefacebookmemes",
            "me_irl"
        )
        val randomMemes2 = randomItems.random()
        fetchData(randomMemes2)
        GifProgressBar?.visibility = View.GONE
        MobileAds.initialize(context)
        val adView = AdView(context)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-7741160545292161/2281954659"
        mAdView = rootView.findViewById(R.id.adView)
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {

            override fun onAdFailedToLoad(adError: LoadAdError) {
                mAdView.loadAd(adRequest)
            }
        }
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

                    Toast.makeText(context, "Searching for $query", Toast.LENGTH_SHORT).show()
                    dismissKeyboard(context)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun dismissKeyboard(context: Context?) {
        val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun fetchData(Memes: String) {

        val urll = "https://memeit-api.herokuapp.com/$Memes/70/"

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
                if (memeJsonArray.length() <= 6){
                    Toast.makeText(context, "no value found", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val response: NetworkResponse = error.networkResponse
                when {
                    error is NetworkError -> {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        GifProgressBar?.visibility = View.GONE
                        val intent = Intent(context, errorActivity::class.java)
                        startActivity(intent)
                    }
                    error is ParseError -> {
                        Toast.makeText(context, "No Results Found", Toast.LENGTH_SHORT).show()
                        GifProgressBar?.visibility = View.GONE
                    }
                    error is ServerError -> {
                        Toast.makeText(context, "No Results Found", Toast.LENGTH_SHORT).show()
                    }
                    response.statusCode == 400 -> {
                        Toast.makeText(context, "No results Found", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        )
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

}


