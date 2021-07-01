package com.example.memeit

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.giphy.sdk.analytics.GiphyPingbacks
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.universallist.SmartItemType
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.giphy.sdk.ui.views.GiphyGridView
import com.giphy.sdk.ui.views.GiphyGridView.Companion.VERTICAL
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.fragment_pintrest_.*
import timber.log.Timber

class Pintrest_Fragment : Fragment() {
    lateinit var mAdView : AdView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_pintrest_, container, false)
        setHasOptionsMenu(true)
        val adView = AdView(context)

        adView.adSize = AdSize.BANNER

        adView.adUnitId = "ca-app-pub-7741160545292161/2281954659"

        MobileAds.initialize(context)

        mAdView = view.findViewById(R.id.adView3)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener(){

            override fun onAdFailedToLoad(adError : LoadAdError) {
                mAdView.loadAd(adRequest)
            }
        }
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        this.context?.let { Giphy.configure(it,"47kzPjMbjORmE51J07WxzhkEQTvkQTMl" ) }
//        setTrendingQuery()
        val gridView = setTrendingQuery()


        gridView?.callback = object : GPHGridCallback{
            override fun contentDidUpdate(resultCount: Int) {
                Timber.d("updated")
            }

            override fun didSelectMedia(media: Media) {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "hii chech this out ${media.embedUrl}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share This With?"))

            }

        }
    }
    fun dismissKeyboard(context: Context?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
    private fun setTrendingQuery(): GiphyGridView?{
        val gridView = context?.let { GiphyGridView(it) }
        gridView?.direction = VERTICAL
        gridView?.spanCount = 2
        gridView?.cellPadding = 10
        gridView?.content = GPHContent.trendingGifs
        GridContainer?.removeAllViews()
        GridContainer?.addView(gridView)
        return gridView
    }
    private fun searchGifs(SearchInput:String){
        val gridView = context?.let { GiphyGridView(it) }
        gridView?.direction = VERTICAL
        gridView?.spanCount = 2
        gridView?.cellPadding = 10
        gridView?.content = GPHContent.searchQuery(SearchInput, MediaType.gif)
        GridContainer?.removeAllViews()
        GridContainer?.addView(gridView)
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.search_bar)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchGifs(query)
                    dismissKeyboard(context)
                }else{
                    setTrendingQuery()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}


