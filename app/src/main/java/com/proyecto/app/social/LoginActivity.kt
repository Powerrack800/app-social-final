package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
//ip
    var loginUrl = "http://10.0.2.2:8080/app-api-identidad/api/identidad/sesion/iniciar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val recoverPassword  = findViewById(R.id.textView) as TextView
        val loginBtn = findViewById(R.id.btnCodigo) as Button
        val register = findViewById(R.id.textView2) as TextView
        val userTxt = findViewById(R.id.editEmail) as TextView
        val passwordTxt = findViewById(R.id.password) as TextView

        recoverPassword.setOnClickListener{
            val changePage = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(changePage)
        }

        loginBtn.setOnClickListener{
            login(userTxt.text.toString(), passwordTxt.text.toString())
        }

        register.setOnClickListener{
            val changePage = Intent(this, RegisterActivity::class.java)
            startActivity(changePage)
        }
    }

    fun login(username:String ,password: String){
        val queue = Volley.newRequestQueue(this@LoginActivity)

        val json = JSONObject()
        json.put("usuario",username)
        json.put("contrasenia",password)
        val requestBody = json.toString()
        val stringReq : StringRequest =
            object : StringRequest(Method.POST, loginUrl,
                Response.Listener { response ->
                    val json: JSONObject = JSONObject(response)
                    val entidad = json["entidad"].toString()
                    Log.e("Entidad", entidad)
                    saveToken(entidad)
                    Toast.makeText(this,"Inicio de sesion correcto",Toast.LENGTH_LONG).show()
                    val changePage = Intent(this, HomeActivity::class.java)
                    startActivity(changePage)
                },
                Response.ErrorListener { error ->
                    val json = JSONObject(String(error.networkResponse.data))
                    val mensaje = json["mensaje"].toString()
                    Toast.makeText(this,"Error: " + mensaje,Toast.LENGTH_SHORT).show()
                    Log.e("API2", "error => $error")
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

    fun saveToken(token: String){
        val sharedPreference =  getSharedPreferences("CREDENCIALES",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("token",token)
        editor.commit()
    }
}