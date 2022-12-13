package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.proyecto.app.social.utils.CodigoPostal
import org.json.JSONArray
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    val createProfileUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/"
    val codigosPostalesURL = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/codigopostal/obtener"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val textCheckBox = findViewById(R.id.textCheckbox) as TextView
        val textCheckBox2 = findViewById(R.id.textLogin) as TextView
        val btnRegister = findViewById(R.id.btnChange) as Button
        getCodigosPostales(getToken())


        val str =
            "<a>Acepto los <font color='#FF0F66'>Terminos de servicio</font> y las <font color='#FF0F66'>Politicas de privacidad</font> </a>"
        textCheckBox.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));

        val str2 =
            "<a>¿Tienes una cuenta? <font color='#FF0F66'>Inicia Sesion</font></a>"
        textCheckBox2.setText(Html.fromHtml(str2, Html.FROM_HTML_MODE_COMPACT));

        textCheckBox2.setOnClickListener{
            val changePage = Intent(this, LoginActivity::class.java)
            startActivity(changePage)
        }
        btnRegister.setOnClickListener{
            var checkBox = findViewById(R.id.checkBox) as CheckBox
            if(checkBox.isChecked){
                    val correoTextView = findViewById(R.id.editEmail) as TextView
                val contraTextView = findViewById(R.id.editEmail2) as TextView
                val codigoSpinner = findViewById(R.id.spinner8) as Spinner
                val usuarioTextView = findViewById(R.id.editEmail5) as TextView
                var codigoSpinnerDefault:Int = 1
                if(!codigoSpinner.isEmpty())
                    codigoSpinnerDefault = codigoSpinner.selectedItemPosition

                createUser(correoTextView.text.toString(),contraTextView.text.toString(),
                    codigoSpinnerDefault, usuarioTextView.text.toString())
                }else{

                    Toast.makeText(this,"Debe aceptar los terminos y condiciones", Toast.LENGTH_LONG).show()
                }

        }
    }

    private fun createUser(correo: String, contra: String, codigo: Int, usuario: String) {
        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val json = JSONObject()
        json.put("usuario",usuario)
        json.put("correo",correo)
        json.put("idCodigoPostal",codigo)
        json.put("contrasenia",contra)
        val requestBody = json.toString()
        Log.e("Body",requestBody)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, createProfileUrl,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val profile = json["entidad"].toString()
                    Log.e("UPDATE",profile.toString())
                    Toast.makeText(this,"Se creo el perfil correctamente", Toast.LENGTH_LONG).show()
                    val changePage = Intent(this, LoginActivity::class.java)
                    startActivity(changePage)
                                  },
                Response.ErrorListener { error ->
                    Log.e("UPDATE", error.networkResponse.toString())
                    Toast.makeText(this,"Error al crear el perfil", Toast.LENGTH_LONG).show()

                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
        queue.add(stringReq)
    }

    private fun getCodigosPostales(token:String){
        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.GET, codigosPostalesURL,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val cpArrayString = json["entidad"].toString()
                    val cpArrayJson = JSONArray(cpArrayString);
                    val codigosPostales = java.util.ArrayList<Array<String>>()
                    for (i in 0 until cpArrayJson.length()) {
                        val item = cpArrayJson.getJSONObject(i)
                        var array : Array<String>  = arrayOf(item.getInt("id").toString(),item.getString("clave") )
                       // val cp = CodigoPostal()
                        //cp.id = item.getInt("id")
                       // cp.clave = item.getString("clave")
                        codigosPostales.add(array)
                    }
                    val adapter: ArrayAdapter<Array<String>> = ArrayAdapter<Array<String>>(
                        this, android.R.layout.simple_spinner_dropdown_item, codigosPostales
                    )
                    val cpSpinner = findViewById(R.id.spinner8) as Spinner
                    cpSpinner.setAdapter(adapter)
                },
                Response.ErrorListener { error ->
                    Log.e("GET_USER", error.networkResponse.toString())
                }
            ){
                /*override fun getHeaders(): MutableMap<String, String> {
                    val headers = java.util.HashMap<String, String>()
                    headers["SEATY-APP-TOKEN"] = token
                    headers["Content-Type"] = "application/json"
                    return headers
                }*/
            }
        queue.add(stringReq)
    }

    private fun getToken(): String {
        val sharedPreference =  getSharedPreferences("CREDENCIALES", Context.MODE_PRIVATE)
        var token: String = sharedPreference.getString("token","error").toString()
        return token
    }


}