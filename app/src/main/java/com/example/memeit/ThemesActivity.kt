package com.example.memeit

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.memeit.databinding.ActivityThemesBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.android.synthetic.main.activity_themes.*
import kotlin.math.log10

class ThemesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themes)
        toggleGroup1?.addOnButtonCheckedListener(object : MaterialButtonToggleGroup.OnButtonCheckedListener{
            override fun onButtonChecked(
                group: MaterialButtonToggleGroup?,
                checkedId: Int,
                isChecked: Boolean
            ) {
               if (isChecked == true){
                   val theme1 = when(checkedId){
                       R.id.setLightTheme -> AppCompatDelegate.MODE_NIGHT_NO
                       else -> AppCompatDelegate.MODE_NIGHT_NO
                   }
                   Toast.makeText(applicationContext, "selected theme is $theme1", Toast.LENGTH_SHORT).show()
                   AppCompatDelegate.setDefaultNightMode(theme1)
               }

            }

        })
        toggleGroup2?.addOnButtonCheckedListener(object : MaterialButtonToggleGroup.OnButtonCheckedListener{
            override fun onButtonChecked(
                group: MaterialButtonToggleGroup?,
                checkedId: Int,
                isChecked: Boolean
            ) {
               if (isChecked == true){
                   val theme2 = when(checkedId){
                       R.id.setDarkTheme -> AppCompatDelegate.MODE_NIGHT_YES
                       else -> AppCompatDelegate.MODE_NIGHT_YES
                   }
                   Toast.makeText(applicationContext, "selected theme is $theme2", Toast.LENGTH_SHORT).show()
                   AppCompatDelegate.setDefaultNightMode(theme2)
               }
            }

        })
    }

}