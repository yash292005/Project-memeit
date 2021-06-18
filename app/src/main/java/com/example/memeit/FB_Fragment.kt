package com.example.memeit

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.fragment_f_b_.*
import timber.log.Timber

class FB_Fragment : Fragment() {
    lateinit var mAdView : AdView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_f_b_, container, false)
        setHasOptionsMenu(true)
        val adView = AdView(context)

        adView.adSize = AdSize.BANNER

        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        MobileAds.initialize(context)

        mAdView = view.findViewById(R.id.adView4)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        this.context?.let { Giphy.configure(it,"47kzPjMbjORmE51J07WxzhkEQTvkQTMl" ) }
        val gridView = setTrendingQuery()
        gridView?.callback = object : GPHGridCallback{
            override fun contentDidUpdate(resultCount: Int) {
               Timber.d("selected")
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
    private fun setTrendingQuery(): GiphyGridView?{
        val gridView = context?.let { GiphyGridView(it) }
        gridView?.direction = GiphyGridView.VERTICAL
        gridView?.spanCount = 2
        gridView?.cellPadding = 10
        gridView?.content = GPHContent.trendingStickers
        GridContainerforFB?.removeAllViews()
        GridContainerforFB?.addView(gridView)
        return gridView
    }
    private fun searchGifs(SearchInput:String){
        val gridView = context?.let { GiphyGridView(it) }
        gridView?.direction = GiphyGridView.VERTICAL
        gridView?.spanCount = 2
        gridView?.cellPadding = 10
        gridView?.content = GPHContent.searchQuery(SearchInput, MediaType.sticker)
        GridContainerforFB?.removeAllViews()
        GridContainerforFB?.addView(gridView)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.search_bar)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchGifs(query)
                    Toast.makeText(context, "Searching for $query", Toast.LENGTH_SHORT).show()
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