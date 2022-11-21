package com.proyecto.app.social

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        var arraylist = ArrayList<Publicacion>();
        arraylist.add(Publicacion("Jose Manuel Velazques","Hace 5 horas","94285","Bache","Más de una semana sin agua en las unidad CTM Culhuacan Zona VI Coyoacan, Calle Manuela Cañizares  y mas texto de pruebas...","",true))
        var mListView = findViewById<ListView>(R.id.listView)
        val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(applicationContext,arraylist)
        mListView.adapter = publicacionAdapter


    }
}