package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    val getPublicacionesUsuario = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/obtener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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

        getPublicaciones(getToken())

    }

    private fun getPublicaciones(token:String){
        val queue = Volley.newRequestQueue(this@HomeActivity)
        val json = JSONObject()
        json.put("idCodigoPostal",0)
        json.put("idTipoIncidente",0)
        json.put("fechaDesde",null)
        json.put("fechaHasta",null)
        val requestBody = json.toString()
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, getPublicacionesUsuario,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val entidad = json["entidad"].toString()
                    val publicaciones = JSONArray(entidad)
                    val publicacionList = ArrayList<Publicacion>()
                    for (i in 0 until publicaciones.length()) {
                        val item = publicaciones.getJSONObject(i)
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = java.util.Date(item.getLong("fechaPublicacion"))
                        publicacionList.add(Publicacion(item.getString("usuario"),sdf.format(date),item.getString("codigoPostal"),item.getString("tipoIncidente"),item.getString("descripcion"),item.getJSONObject("imagen").getString("contenido"),true, item.getLong("id")))
                    }
                    var mListView = findViewById<ListView>(R.id.homeviewer)
                    val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(this@HomeActivity,publicacionList,getToken())
                    mListView.adapter = publicacionAdapter
                },
                Response.ErrorListener { error ->
                    Log.e("UPDATE", error.networkResponse.toString())
                    Toast.makeText(this,"Error al recuperar el perfil", Toast.LENGTH_LONG).show()
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["SEATY-APP-TOKEN"] = token
                    headers["Content-Type"] = "application/json"
                    return headers
                }
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }
        queue.add(stringReq)
    }


    private fun getToken(): String {
        val sharedPreference =  getSharedPreferences("CREDENCIALES", Context.MODE_PRIVATE)
        var token: String = sharedPreference.getString("token","error").toString()
        return token
    }
}