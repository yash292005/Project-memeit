package com.example.memeit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.giphy.sdk.analytics.GiphyPingbacks.context
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.avatar_item.view.*
import kotlinx.coroutines.tasks.await

class AvatarActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)
        val adapter = GroupAdapter<ViewHolder>()
        val avatarView = findViewById<RecyclerView>(R.id.AvatarView)
        avatarView.adapter = adapter
        adapter.add(AvtarItem("Susan", R.drawable.girl))
        adapter.add(AvtarItem("Mark", R.drawable.boy))
        adapter.add(AvtarItem("Elon", R.drawable.boy_1_))
        adapter.add(AvtarItem("Lisa", R.drawable.girl_1_))
        adapter.add(AvtarItem("Warren", R.drawable.man))
        adapter.add(AvtarItem("Sundar", R.drawable.man_1_))
        adapter.add(AvtarItem("Bill", R.drawable.man_2_))
        adapter.add(AvtarItem("Shantanu", R.drawable.man_3_))
        adapter.add(AvtarItem("Yash", R.drawable.man_4_))
    }

    override fun onBackPressed() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, intent, Bundle())
        super.onBackPressed()
    }

    class AvtarItem(val name: String, private val image: Int) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.avatar_item

        @SuppressLint("CommitPrefEdits")
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.AvatarName.text = name
            viewHolder.itemView.AvatarImage.load(image)
            viewHolder.itemView.AvatarImage.setOnClickListener {

                val sharedPref = it.context.getSharedPreferences("UserInput", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                val key = "text"
                editor.putString(key, name)
                editor.apply()
                Toast.makeText(context, "Avatar set to $name", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(context, intent, Bundle())
            }
        }
    }
}
