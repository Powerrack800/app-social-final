package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    var logoutUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/sesion/cerrar"
    var profileUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/info/obtener"
    var profileUpdateUrl ="http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/"
    val codigosPostalesURL = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/codigopostal/obtener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val btnCloseSesion = findViewById(R.id.btnCodigo5) as Button
        val btnUpdate = findViewById(R.id.btnCodigo3) as Button
        val editName = findViewById(R.id.editName) as EditText
        val editCp = findViewById(R.id.spinner5) as Spinner
        val editEmail = findViewById(R.id.editEmailProfile) as EditText
        getProfile(getToken(), editName, editCp,editEmail)
        getCodigosPostales(getToken())

        var arraylist = ArrayList<Publicacion>();
        arraylist.add(Publicacion("Jose Manuel Velazques","Hace 5 horas","94285","Bache","Más de una semana sin agua en las unidad CTM Culhuacan Zona VI Coyoacan, Calle Manuela Cañizares  y mas texto de pruebas...","",true))
        var mListView = findViewById<ListView>(R.id.listView)
        val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(applicationContext,arraylist)
        mListView.adapter = publicacionAdapter

        btnCloseSesion.setOnClickListener{
            cerrarSesion(getToken())
        }
        btnUpdate.setOnClickListener{
            updateProfile(getToken(), editName, editCp,editEmail)
        }
    }

    private fun getProfile(token: String, editName: EditText, editCp: Spinner, editEmail: EditText){
        val queue = Volley.newRequestQueue(this@ProfileActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.GET, profileUrl,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val profile = json["entidad"].toString()
                    val profileJSON = JSONObject(profile);
                    val usuario: String = profileJSON["usuario"].toString()
                    val email: String = profileJSON["correo"].toString()
                    val cp: Int = profileJSON["idCodigoPostal"] as Int

                    editName?.setText(usuario)
                    editCp?.setSelection(cp)
                    editEmail?.setText(email)
                    Log.e("GET_USER",response.toString())
                },
                Response.ErrorListener { error ->
                    Log.e("GET_USER", error.networkResponse.toString())
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

    private fun updateProfile(token: String, editName: EditText, editCp: Spinner, editEmail: EditText){
        val queue = Volley.newRequestQueue(this@ProfileActivity)
        val json = JSONObject()
        json.put("usuario",editName.text.toString())
        json.put("correo",editEmail.text.toString())
        json.put("idCodigoPostal",editCp.selectedItemId)
        val requestBody = json.toString()
        val stringReq : StringRequest =
            object : StringRequest(
                Method.PUT, profileUpdateUrl,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val profile = json["entidad"].toString()
                    Log.e("UPDATE",profile.toString())
                    Toast.makeText(this,"Se actualizo el perfil", Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener { error ->
                    Log.e("UPDATE", error.networkResponse.toString())
                    Toast.makeText(this,"Error al actualizar el perfil", Toast.LENGTH_LONG).show()

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

    private fun getCodigosPostales(token:String){
        val queue = Volley.newRequestQueue(this@ProfileActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.GET, codigosPostalesURL,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val cpArrayString = json["entidad"].toString()
                    val cpArrayJson = JSONArray(cpArrayString);
                    val codigosPostales = java.util.ArrayList<String>()
                    for (i in 0 until cpArrayJson.length()) {
                        val item = cpArrayJson.getJSONObject(i)
                        codigosPostales.add(item.getString("clave"))
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_dropdown_item, codigosPostales
                    )
                    val cpSpinner = findViewById(R.id.spinner5) as Spinner
                    cpSpinner.setAdapter(adapter)
                },
                Response.ErrorListener { error ->
                    Log.e("GET_USER", error.networkResponse.toString())
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = java.util.HashMap<String, String>()
                    headers["SEATY-APP-TOKEN"] = token
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
        queue.add(stringReq)
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