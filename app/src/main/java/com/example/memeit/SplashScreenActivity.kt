package com.example.memeit

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.fragment_redditfragment.*

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        Glide.with(this).asGif().load(R.drawable.another_good_progressbar).into(gifImageView!!)
        Glide.with(this).asGif().load(R.drawable.poweredby_100px_badge).into(Promotion!!)
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if ((wifiCon != null && wifiCon.isConnected) || (mobileCon != null && mobileCon.isConnected) ) {
            startApp()
        }else{
            showError()
        }
    }

    private fun showError() {
        val intent = Intent(this, errorActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startApp() {
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}