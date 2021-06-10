package com.example.memeit

/**
 *created by Yash vardhan
 * on date 26/4/2021
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainPager.adapter = someAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(mainPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_memepng)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_gif)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_sticker)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.ic_gallery)
    }


//    override fun fetchData(memes: String) {
//        fetchData("hii")
//    }
}

class someAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> redditfragment()
            1 -> Pintrest_Fragment()
            2 -> FB_Fragment()
            3 -> Instafragment()
            else -> return redditfragment()
        }
    }

    override fun getCount(): Int {
        return 4
    }



}
