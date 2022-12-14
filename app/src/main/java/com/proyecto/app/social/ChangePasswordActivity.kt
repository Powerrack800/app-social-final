package com.proyecto.app.social

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {
    var loginUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/sesion/iniciar"
    var passwordUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/usuario/contrasenia"
    var logoutUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/sesion/cerrar"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val userName = findViewById(R.id.editEmail) as EditText
        val passwordActual = findViewById(R.id.password) as EditText
        val passwordNew = findViewById(R.id.passwordNew) as EditText
        val btnChange = findViewById(R.id.btnChange) as Button

        btnChange.setOnClickListener{
            if(userName.text.isEmpty() || userName.text == null || passwordActual.text.isEmpty() || passwordActual.text == null
                || passwordNew.text.isEmpty() || passwordNew.text == null)
                Toast.makeText(this,"Error: Debes rellenar todos los campos", Toast.LENGTH_LONG).show()
                else
                changePassword(userName.text.toString(),passwordActual.text.toString(),
                    passwordNew.text.toString())
        }
    }

    private fun changePassword(username: String, password: String, passwordNew: String) {
        var token: String = ""
        val queue = Volley.newRequestQueue(this@ChangePasswordActivity)

        val json = JSONObject()
        json.put("usuario",username)
        json.put("contrasenia",password)
        val requestBody = json.toString()
        Log.e("Contrasenia",requestBody.toString())
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, loginUrl,
                Response.Listener { response ->
                    val json: JSONObject = JSONObject(response)
                    val entidad = json["entidad"].toString()
                    token = entidad
                    Log.e("Primera iteracion*",token)
                    //Actualizar contraseña
                    updatePassword(token, passwordNew)
                },
                Response.ErrorListener { error ->
                    Log.e("Segunda iteracion*",token)
                    Toast.makeText(this,"No se pudo actualizar contraseña", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getBodyContentType(): String {
                    return "application/json;";
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }
        queue.add(stringReq)
    }

    private fun closeSession(token: String) {
        val queue = Volley.newRequestQueue(this@ChangePasswordActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.DELETE, logoutUrl,
                Response.Listener { response ->
                    val changePage = Intent(this, LoginActivity::class.java)
                    startActivity(changePage)
                },
                Response.ErrorListener { error ->
                    val changePage = Intent(this, LoginActivity::class.java)
                    startActivity(changePage)
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

    private fun updatePassword(token: String, passwordNew: String) {
        val queue = Volley.newRequestQueue(this@ChangePasswordActivity)

        val json = JSONObject()
        json.put("contrasenia",passwordNew)
        val requestBody = json.toString()
        val stringReq : StringRequest =
            object : StringRequest(Method.PUT, passwordUrl,
                Response.Listener { response ->
                    Log.e("Contraseña actualizada", response.toString())
                    Toast.makeText(this,"Contraseña actualizada",Toast.LENGTH_SHORT).show()
                    closeSession(token)
                },
                Response.ErrorListener { error ->
                    val json = JSONObject(String(error.networkResponse.data))
                    Log.e("PASSWORD",String(error.networkResponse.data))
                    Toast.makeText(this,"Error al cambiar la contraseña : " ,Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getBodyContentType(): String {
                    return "application/json;";
                }
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
                override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["SEATY-APP-TOKEN"] = token
                        headers["Content-Type"] = "application/json"
                        return headers
                }
            }
        queue.add(stringReq)
    }
}