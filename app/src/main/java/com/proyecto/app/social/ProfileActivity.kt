package com.proyecto.app.social

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnCloseSesion = findViewById(R.id.btnCodigo5) as Button

        var arraylist = ArrayList<Publicacion>();
        arraylist.add(Publicacion("Jose Manuel Velazques","Hace 5 horas","94285","Bache","Más de una semana sin agua en las unidad CTM Culhuacan Zona VI Coyoacan, Calle Manuela Cañizares  y mas texto de pruebas...","",true))
        var mListView = findViewById<ListView>(R.id.listView)
        val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(applicationContext,arraylist)
        mListView.adapter = publicacionAdapter


        btnCloseSesion.setOnClickListener{
            cerrarSesion(getToken())
        }
    }

    private fun cerrarSesion(token: String) {

    }

    private fun getToken(): String {
        val sharedPreference =  getSharedPreferences("CREDENCIALES", Context.MODE_PRIVATE)
        var token: String = sharedPreference.getString("token","error").toString()
        return token
    }
}