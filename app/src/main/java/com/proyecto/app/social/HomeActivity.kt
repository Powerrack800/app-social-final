package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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

        getPublicaciones(getToken(),0)

        val menuFiltro = findViewById(R.id.spinner) as Spinner
        menuFiltro.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem.equals("Codigo postal")) {
                    val inputEditTextField = EditText(this@HomeActivity)
                    val dialog = AlertDialog.Builder(this@HomeActivity)
                        .setTitle("Filtro")
                        .setMessage("Ingresa CP")
                        .setView(inputEditTextField)
                        .setPositiveButton("OK") { _, _ ->
                            val editTextInput = inputEditTextField.text.toString()
                            if (!isNumeric(editTextInput) || editTextInput.isNullOrEmpty()) {
                                Toast.makeText(
                                    this@HomeActivity,
                                    "Codigo postal no valido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                getPublicaciones(getToken(), editTextInput.toInt());
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .create()
                    dialog.show()
                }
                if (selectedItem.equals("Quitar filtro"))
                    getPublicaciones(getToken(),0)
                menuFiltro.setSelection(0)
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
    }


    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    private fun getPublicaciones(token:String, cp: Int){
        val queue = Volley.newRequestQueue(this@HomeActivity)
        val json = JSONObject()
        var codigoPostal = 0
        if(cp!=0)
            codigoPostal = cp
        json.put("idCodigoPostal",codigoPostal)
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
                    if(publicaciones.length() !=0){
                    for (i in 0 until publicaciones.length()) {
                        val item = publicaciones.getJSONObject(i)
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = java.util.Date(item.getLong("fechaPublicacion"))
                        publicacionList.add(Publicacion(item.getString("usuario"),sdf.format(date),item.getString("codigoPostal"),item.getString("tipoIncidente"),item.getString("descripcion"),item.getJSONObject("imagen").getString("contenido"),true, item.getLong("id")))
                    }
                    var mListView = findViewById<ListView>(R.id.homeviewer)
                    val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(this@HomeActivity,publicacionList,getToken())
                    mListView.adapter = publicacionAdapter
                    }else{
                        Toast.makeText(this,"No se encontraron publicaciones disponible", Toast.LENGTH_LONG).show()
                    }
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