package com.example.memeit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_error.*

class errorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        Glide.with(this).asGif().load(R.drawable.networkerror).into(gifImageView4!!)
        retryButton?.setOnClickListener { val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }
    }

    override fun onBackPressed() {
        finish()
    }
}