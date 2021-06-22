package com.example.memeit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import coil.load
import com.giphy.sdk.analytics.GiphyPingbacks.context
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_avatar.*
import kotlinx.android.synthetic.main.avatar_item.view.*

class AvatarActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)
        val adapter = GroupAdapter<ViewHolder>()
        AvatarView.adapter = adapter
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
    class AvtarItem(val name: String, private val image: Int) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.avatar_item

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.AvatarName.text = name
            viewHolder.itemView.AvatarImage.load(image)
            viewHolder.itemView.AvatarImage.setOnClickListener {
                Toast.makeText(context, "Avatar set to  $name", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("User input", "$name")
                startActivity(context, intent, Bundle())
            }
        }
    }
}
