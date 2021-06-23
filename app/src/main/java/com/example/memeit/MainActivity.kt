package com.example.memeit

/**
 *created by Yash vardhan
 * on date 26/4/2021
 */

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.reddit_meme_content.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var adRequest = AdRequest.Builder().build()
        mainPager.adapter = someAdapter(supportFragmentManager)
        setSupportActionBar(MainToolBar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        collapse.isTitleEnabled = false
        MainToolBar.title = "memeit"
        val drawerLayout = findViewById<DrawerLayout>(R.id.my_drawer_layout)
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,MainToolBar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val sharedPreferences = getSharedPreferences("UserInput", MODE_PRIVATE)
        val key = "text"
        val UserImage = sharedPreferences.getString(key, "")
        when (UserImage){
            "Susan" -> UserAvatar?.setImageResource(R.drawable.girl)
            "Mark" -> UserAvatar?.setImageResource(R.drawable.boy)
            "Elon" -> UserAvatar?.setImageResource(R.drawable.boy_1_)
            "Lisa" -> UserAvatar?.setImageResource(R.drawable.girl_1_)
            "Warren" -> UserAvatar?.setImageResource(R.drawable.man)
            "Sundar" -> UserAvatar?.setImageResource(R.drawable.man_1_)
            "Bill" -> UserAvatar?.setImageResource(R.drawable.man_2_)
            "Shantanu" -> UserAvatar?.setImageResource(R.drawable.man_3_)
            "Yash" -> UserAvatar?.setImageResource(R.drawable.man_4_)
        }
        val navView = findViewById<NavigationView>(R.id.NavView)
        navView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.signOut -> {
                        val sharedPref = this@MainActivity.getSharedPreferences("UserInput", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.clear()
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@MainActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                        return true
                    }
                    R.id.changeAv -> {
                        val intent = Intent(this@MainActivity, AvatarActivity::class.java)
                        startActivity(intent)
                        finish()
                        return true
                    }


                    R.id.ChangeTheTheme -> {
                       val theme = AppCompatDelegate.MODE_NIGHT_YES
                        AppCompatDelegate.setDefaultNightMode(theme)
                        mainPager.setBackgroundColor(Color.rgb(8, 8, 53))
                        return true
                    }R.id.ChangeTheme2 ->{
                    val theme2 = AppCompatDelegate.MODE_NIGHT_NO
                    AppCompatDelegate.setDefaultNightMode(theme2)
                    mainPager.setBackgroundColor(Color.rgb(185, 250, 248))
                    return true
                    }
                    R.id.share -> {
                        Toast.makeText(this@MainActivity, "Sorry The App Has Not Been Deployed To PlayStore Yet", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.rate -> {
                        Toast.makeText(this@MainActivity, "Sorry The App Has Not Been Deployed To PlayStore Yet", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.credits -> {
                        val builder = CustomTabsIntent.Builder()
                        val CustomTabsIntent = builder.build()
                        CustomTabsIntent.launchUrl(this@MainActivity, Uri.parse("https://memeitcredits.blogspot.com/2021/06/memeit-app-is-created-by-indian-amateur.html"))
                        return true
                    }
                    R.id.Privacy -> {
                        val intent = Intent(this@MainActivity, PrivacyPolicy::class.java)
                        startActivity(intent)
                        return true
                    }
                }
                my_drawer_layout.closeDrawer(GravityCompat.START)
                return true
            }

        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_main)
        supportActionBar?.title = "memeit"
        tabLayout.setupWithViewPager(mainPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_memepng)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_gif)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_sticker)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.ic_gallery)

    }

    override fun onBackPressed() {
        finish()
    }


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
