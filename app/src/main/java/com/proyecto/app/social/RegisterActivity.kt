package com.proyecto.app.social

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val textCheckBox = findViewById(R.id.textCheckbox) as TextView
        val textCheckBox2 = findViewById(R.id.textLogin) as TextView

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

    }
}