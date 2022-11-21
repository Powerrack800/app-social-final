package com.proyecto.app.social

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PublicarActivity : AppCompatActivity() {
    val cp: String = "94285";
    val accidente:String = "Accidente";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicar)

        val informacionIncidente = findViewById(R.id.txtDatos) as TextView
        val btnFoto = findViewById(R.id.btnCodigo3) as Button
        val cpMenu = findViewById(R.id.spinner4) as Spinner
        val cpIncidente = findViewById(R.id.spinner3) as Spinner

        val str = "<a>CP:<font color='blue'>"+this.cp+"</font> Tipo de incidente:<font color='blue'>"+this.accidente+"</font></a>"
        informacionIncidente.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)

        btnFoto.setOnClickListener{
            tomarFoto()
        }

        cpMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position!=0){
                    val cp = parent?.getItemAtPosition(position);
                    val str = "<a>CP:<font color='blue'>"+cp+"</font> Tipo de incidente:<font color='blue'>"+accidente+"</font></a>"
                    informacionIncidente.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
                }
            }
        }

        cpIncidente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position!=0){
                    val incidente = parent?.getItemAtPosition(position);
                    val str = "<a>CP:<font color='blue'>"+cp+"</font> Tipo de incidente:<font color='blue'>"+incidente+"</font></a>"
                    informacionIncidente.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
                }
            }
        }

    }



    private fun tomarFoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val image: ImageView = findViewById(R.id.imageProfile)
        if (data != null) {
            val imageMap: Bitmap = data.extras?.get("data") as Bitmap
            image.setImageBitmap(imageMap)
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(directory, getPictureName() + ".jpg")
            if (!file.exists()) {
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    imageMap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getPictureName(): String {
        val tst = SimpleDateFormat("yyyyMMdd_HHmmss")
        return tst.format(Date())
    }
}