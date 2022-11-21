package com.proyecto.app.social

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val recoverPassword  = findViewById(R.id.textView) as TextView
        val loginBtn = findViewById(R.id.btnCodigo) as Button
        val register = findViewById(R.id.textView2) as TextView

        recoverPassword.setOnClickListener{
            val changePage = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(changePage)
        }

        loginBtn.setOnClickListener{
            val changePage = Intent(this, HomeActivity::class.java)
            startActivity(changePage)
        }

        register.setOnClickListener{
            val changePage = Intent(this, RegisterActivity::class.java)
            startActivity(changePage)
        }
    }

}