package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    var logoutUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/sesion/cerrar"
    var profileUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/info/obtener"
    var profileUpdateUrl ="http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/actualizar"
    val codigosPostalesURL = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/codigopostal/obtener"
    val eliminarPerfilURL = "http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/eliminar"
    val getPublicacionesUsuario = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/usuario/obtener"
    val codigosPostales = arrayOf(90084,90085,90086,90087,90088,90089,90090,90091,90092,90093,90094,90095,90096,90097 )


    lateinit var cpSpinner:Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        val btnCloseSesion = findViewById(R.id.btnCodigo5) as Button
        val btnUpdate = findViewById(R.id.btnCodigo3) as Button
        val editName = findViewById(R.id.editName) as EditText
        cpSpinner = findViewById(R.id.spinner5)
        val editEmail = findViewById(R.id.editEmailProfile) as EditText
        getCodigosPostales(getToken())
        getProfile(getToken(), editName, cpSpinner,editEmail)
        getPublicaciones(getToken())

        btnCloseSesion.setOnClickListener{
            cerrarSesion(getToken())
        }
        btnUpdate.setOnClickListener{
            updateProfile(getToken(), editName, cpSpinner,editEmail)
        }
        getPublicaciones(getToken())
        //Confirmacion de eliminicacion de cuenta
        val btnDelete = findViewById(R.id.btnDelete) as Button
        btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this@ProfileActivity)
            builder.setMessage("??Esta seguro que desea eliminar su cuenta?")
                .setCancelable(false)
                .setPositiveButton("SI") { dialog, id ->
                    deleteAccount(getToken())
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
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
                    val index = codigosPostales.indexOf(cp)
                    editName?.setText(usuario)
                    cpSpinner.setSelection(index)
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
        json.put("idCodigoPostal",codigosPostales[editCp.selectedItemId.toInt()])
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

    private fun deleteAccount(token: String){
        val queue = Volley.newRequestQueue(this@ProfileActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.DELETE, eliminarPerfilURL,
                Response.Listener { response ->
                    Toast.makeText(this,"La cuenta se elimino correctamente", Toast.LENGTH_LONG).show()
                    val changePage = Intent(this, LoginActivity::class.java)
                    startActivity(changePage)
                    deleteToken()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,"Error al eliminar la cuenta", Toast.LENGTH_LONG).show()
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

    private fun getPublicaciones(token: String){
        val queue = Volley.newRequestQueue(this@ProfileActivity)
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
                    var mListView = findViewById<ListView>(R.id.listView)
                    val publicacionAdapter: PublicacionAdapter = PublicacionAdapter(this@ProfileActivity,publicacionList,getToken())
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

}