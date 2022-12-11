package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    var logoutUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/sesion/cerrar"

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
        Log.e("Token a eliminar" , token)
        val queue = Volley.newRequestQueue(this@ProfileActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.DELETE, logoutUrl,
                Response.Listener { response ->
                    Toast.makeText(this,"Se cerro sesion correctamente", Toast.LENGTH_LONG).show()
                    val changePage = Intent(this, LoginActivity::class.java)
                    startActivity(changePage)
                    deleteToken()
                    Log.e("CLOSE_SESSION","Paso1")
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,"Se cerro sesion correctamente", Toast.LENGTH_LONG).show()
                    val changePage = Intent(this, LoginActivity::class.java)
                    startActivity(changePage)
                    deleteToken()
                    Log.e("CLOSE_SESSION","Paso2")
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["SEATY-APP-TOKEN"] = token
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
        queue.add(stringReq)
    }

    private fun getToken(): String {
        val sharedPreference =  getSharedPreferences("CREDENCIALES", Context.MODE_PRIVATE)
        var token: String = sharedPreference.getString("token","error").toString()
        return token
    }

    private fun deleteToken(){
        val sharedPreference =  getSharedPreferences("CREDENCIALES", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.clear()
    }

}