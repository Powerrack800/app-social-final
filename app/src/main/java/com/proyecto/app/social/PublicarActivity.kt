package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set

class PublicarActivity : AppCompatActivity() {
    val tiposIncidentesURL = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/tipoincidente/obtener"
    val codigosPostalesURL = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/codigopostal/obtener"
    val createPublishUrl = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/"
    val cp: String = "94285";
    val accidente:String = "Bache";
    val codigosPostales = arrayOf(90084,90085,90086,90087,90088,90089,90090,90091,90092,90093,90094,90095,90096,90097 )
    val tiposIncidentes = arrayOf(1,2,3,4,5,6,7,8,9,10,11,12)
    lateinit var cpSpinner:Spinner
    lateinit var incidenteSpinner:Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicar)

        val informacionIncidente = findViewById(R.id.txtDatos) as TextView
        val txtDescripcion = findViewById(R.id.txtDescripcion) as TextView
        val btnFoto = findViewById(R.id.btnCodigo3) as Button
        cpSpinner = findViewById(R.id.spinner4)
        incidenteSpinner = findViewById(R.id.spinner3)
        val btnPubicar = findViewById(R.id.btnCodigo4) as Button
        getCodigosPostales(getToken())
        getTipoIncidentes(getToken())
        val str = "<a>CP:<font color='blue'>"+this.cp+"</font> Tipo de incidente:<font color='blue'>"+this.accidente+"</font></a>"
        informacionIncidente.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)

        btnFoto.setOnClickListener{
            tomarFoto()
        }

        cpSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position!=0){
                    val cp = parent?.getItemAtPosition(position);
                    val str = "<a>CP:<font color='blue'>"+cp+"</font> Tipo de incidente:<font color='blue'>"+accidente+"</font></a>"
                    informacionIncidente.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
                }
            }
        }

        incidenteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position!=0){
                    val incidente = parent?.getItemAtPosition(position);
                    val str = "<a>CP:<font color='blue'>"+cp+"</font> Tipo de incidente:<font color='blue'>"+incidente+"</font></a>"
                    informacionIncidente.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
                }
            }
        }

        btnPubicar.setOnClickListener{
            val cp = codigosPostales[cpSpinner.selectedItemId.toInt()]
            val tipoIncidente = tiposIncidentes[incidenteSpinner.selectedItemId.toInt()]
            val descripcion = txtDescripcion.text.toString()
            val pictureName = getPictureName() + ".png"
            val image: ImageView = findViewById(R.id.imageProfile)
            val bitmap = (image.getDrawable() as BitmapDrawable).getBitmap()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val imageByte = stream.toByteArray()
            val encodedImage: String = Base64.getEncoder().encodeToString(imageByte)
            crearPublicacion(descripcion,tipoIncidente,cp,pictureName,encodedImage)
        }
    }

    private fun crearPublicacion(descripcion: String, tipoIncidente: Int, cp: Int, pictureName: String, imageByte: String) {
       // val tipoIncidenteNumber = tipoIncidente as Long
       // val cpNumber = cp as Long
     //   tipoIncidenteNumber.plus(1);
      //  cpNumber.plus(1);

        createPublish(descripcion, tipoIncidente, cp, pictureName,imageByte)
    }

    private fun tomarFoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val image: ImageView = findViewById(R.id.imageProfile)
        image.setImageURI(data?.data)
    }

    private fun getPictureName(): String {
        val tst = SimpleDateFormat("yyyyMMdd_HHmmss")
        return tst.format(Date())
    }

    private fun getCodigosPostales(token:String){
        val queue = Volley.newRequestQueue(this@PublicarActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.GET, codigosPostalesURL,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val cpArrayString = json["entidad"].toString()
                    val cpArrayJson = JSONArray(cpArrayString);
                    val codigosPostales = ArrayList<String>()
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
                    val headers = HashMap<String, String>()
                    headers["SEATY-APP-TOKEN"] = token
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
        queue.add(stringReq)
    }

    private fun getTipoIncidentes(token: String){
        val queue = Volley.newRequestQueue(this@PublicarActivity)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.GET, tiposIncidentesURL,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val cpArrayString = json["entidad"].toString()
                    val cpArrayJson = JSONArray(cpArrayString);
                    val codigosPostales = ArrayList<String>()
                    for (i in 0 until cpArrayJson.length()) {
                        val item = cpArrayJson.getJSONObject(i)
                        codigosPostales.add(item.getString("descripcion"))
                    }
                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_dropdown_item, codigosPostales
                    )

                    incidenteSpinner.setAdapter(adapter)
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

    private fun createPublish(descripcion: String, tipoIncidente: Int, cp: Int, pictureName: String, imageByte: String){
        val queue = Volley.newRequestQueue(this@PublicarActivity)
        val json = JSONObject()
        json.put("idCodigoPostal",cp)
        json.put("idTipoIncidente",tipoIncidente)
        json.put("descripcion",descripcion)
        val imageJson = JSONObject()
        imageJson.put("contenido",imageByte)
        json.put("imagen",imageJson)
        val requestBody = json.toString()
        Log.e("JSON",requestBody)
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, createPublishUrl,
                Response.Listener { response ->
                    val json = JSONObject(response)
                    val profile = json["entidad"].toString()
                    Log.e("UPDATE",profile.toString())
                    Toast.makeText(this,"Se creo la publicacion", Toast.LENGTH_LONG).show()
                    val changePage = Intent(this, HomeActivity::class.java)
                    startActivity(changePage)
                },
                Response.ErrorListener { error ->
                    Log.e("UPDATE", error.networkResponse.toString())
                    Log.e("EROR", json["mensaje"].toString())
                    Toast.makeText(this,"Error al crear la publicacion", Toast.LENGTH_LONG).show()

                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["SEATY-APP-TOKEN"] = getToken()
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