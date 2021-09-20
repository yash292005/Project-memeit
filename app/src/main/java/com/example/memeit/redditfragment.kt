@file:Suppress("ClassName", "LocalVariableName", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.example.memeit


import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_redditfragment.*
import org.json.JSONException


class redditfragment : Fragment(), MyAdapter.theItemClicked {
    private lateinit var mAdapter: MyAdapter
    private lateinit var searchView: SearchView
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
        val Fab = rootView.findViewById<FloatingActionButton>(R.id.Fab)!!
        Fab.setOnClickListener {
            searchView.isIconified = false
        }
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
                    val randomItems = arrayOf("IndianMeyMeys", "indiameme", "memes", "india", "AdviceAnimals", "MemeEconomy", "IndianDankMemes", "terriblefacebookmemes", "ComedyCentral", "me_irl", "dankmemes")
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
            "ComedyCentral",
            "me_irl",
            "dankmemes"
        )
        val randomMemes2 = randomItems.random()
        fetchData(randomMemes2)
        GifProgressBar?.visibility = View.GONE
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
        searchView = menuItem.actionView as SearchView
        searchView.isIconified = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchDataOnSearch(query)

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

        val urll = "https://memeit-api-v2.herokuapp.com/$Memes"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, urll, null,
            { response ->
                val memeJsonArray = response.getJSONArray("memes")
                val memeArray = ArrayList<Memes>()
                for (i in 0 until memeJsonArray.length()) {
                    val memeJsonObject = memeJsonArray.getJSONObject(i)
                    val meme = Memes(
                        memeJsonObject.getString("audio_url"),
                        memeJsonObject.getString("subreddit"),
                        memeJsonObject.getString("title"),
                        memeJsonObject.getString("url")
                    )
                    memeArray.add(meme)
                }
                mAdapter.updateMeme(memeArray)
                GifProgressBar?.visibility = View.GONE
                if (memeJsonArray.length() <= 6) {
                    print("null value returned")                }
            },
            {
                Toast.makeText(context, "Some Error Occured...", Toast.LENGTH_SHORT).show()
            }
        )
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

    private fun fetchDataOnSearch(Memes: String) {
        val urll = "https://memeit-api-v2.herokuapp.com/$Memes"
        try {
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, urll, null,
                { response ->
                    val memeJsonArray = response.getJSONArray("memes")
                    val memeArray = ArrayList<Memes>()
                    for (i in 0 until memeJsonArray.length()) {
                        val memeJsonObject = memeJsonArray.getJSONObject(i)
                        val meme = Memes(
                            memeJsonObject.getString("audio_url"),
                            memeJsonObject.getString("subreddit"),
                            memeJsonObject.getString("title"),
                            memeJsonObject.getString("url")

                        )
                        memeArray.add(meme)
                    }
                    mAdapter.updateMemeOnSearch(memeArray)
                    GifProgressBar?.visibility = View.GONE
                    if (memeJsonArray.length() <= 6) {
                        print("null value returned")
                    }
                },
                {
                    Toast.makeText(context, "No Results Found  ", Toast.LENGTH_SHORT).show()

                }
            )
            context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
        }
        catch (e: JSONException){
            Toast.makeText(context, "No Results Found  ", Toast.LENGTH_SHORT).show()
        }
    }


}


