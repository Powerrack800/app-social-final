package com.proyecto.app.social

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class HomeActivity : AppCompatActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val btnNotification = findViewById(R.id.button3) as Button
        btnNotification.setOnClickListener{
            val changePage = Intent(this, NotificationsActivity::class.java)
            startActivity(changePage)
        }

        val btnProfile = findViewById(R.id.button2) as Button
        btnProfile.setOnClickListener{
            val changePage = Intent(this, ProfileActivity::class.java)
            startActivity(changePage)
        }

        val btnPublish = findViewById(R.id.btnChange) as Button
        btnPublish.setOnClickListener{
            val changePage = Intent(this, PublicarActivity::class.java)
            startActivity(changePage)
        }

        var arraylist = ArrayList<Publicacion>();
        arraylist.add(Publicacion("Jose Manuel Velazques","Hace 5 horas","94285","Bache","Más de una semana sin agua en las unidad CTM Culhuacan Zona VI Coyoacan, Calle Manuela Cañizares  y mas texto de pruebas...","",false))
        var mListView = findViewById<ListView>(R.id.homeviewer)
        val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(applicationContext,arraylist)
        mListView.adapter = publicacionAdapter

    }
}