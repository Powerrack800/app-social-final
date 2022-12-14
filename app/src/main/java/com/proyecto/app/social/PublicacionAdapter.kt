package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class PublicacionAdapter(private val mContext: Context, private val listaVideo: ArrayList<Publicacion>, private val token:String) : ArrayAdapter<Publicacion>(mContext, 0, listaVideo) {
    val eliminarPublicacionURL = "http://10.0.2.2:8080/app-api-publicaciones/api/publicacion/eliminar"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.publicacion_row, parent, false)
        val publicacion = listaVideo[position]

        val username = layout.findViewById(R.id.txtUsuario) as TextView
        val tiempo = layout.findViewById(R.id.txtTiempo) as TextView
        val datos = layout.findViewById(R.id.txtDatos) as TextView
        val descripcion = layout.findViewById(R.id.txtDescripcion) as TextView
        val image = layout.findViewById(R.id.imageProfile) as ImageView
        val menuBtn = layout.findViewById(R.id.button) as Button
        val comentarBtn = layout.findViewById(R.id.btnComentar) as Button
        val str =
            "<a>CP:<font color='blue'>"+ publicacion.cp+"</font> Tipo de incidente:<font color='blue'>"+publicacion.tipoIncidente+"</font></a>"

        username.text = publicacion.usuario
        tiempo.text = publicacion.tiempo
        datos.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
        descripcion.text = publicacion.descripcion
        if(publicacion.image.isEmpty() || publicacion.image == null){
        image.setImageDrawable(
            ContextCompat.getDrawable(
                mContext, // Context
                R.drawable.problema // Drawable
            )
        )
        }else{
            val decodedString: ByteArray = Base64.getDecoder().decode(publicacion.image)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            image.setImageBitmap(decodedByte)
        }
        if(publicacion.delete){
            comentarBtn.visibility = View.INVISIBLE
            menuBtn.setOnClickListener{
                val popupMenu = PopupMenu(mContext,it)
                popupMenu.inflate(R.menu.menu_delete)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                            val builder = AlertDialog.Builder(mContext)
                            builder.setMessage("¿Esta seguro que desea eliminar la publicacion?")
                                .setCancelable(false)
                                .setPositiveButton("SI") { dialog, id ->
                                    deletePublicacion(token,publicacion.id)
                                }
                                .setNegativeButton("No") { dialog, id ->
                                    dialog.dismiss()
                                }
                            val alert = builder.create()
                            alert.show()
                    true
                }

            }
        }else{
            comentarBtn.visibility = View.VISIBLE
            menuBtn.setOnClickListener{
                val popupMenu = PopupMenu(mContext,it)
                popupMenu.inflate(R.menu.report)
                popupMenu.show();
            }

            comentarBtn.setOnClickListener{
                 v ->
                    val intent = Intent(v.getContext(), ComentarioActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    v.getContext().startActivity(intent)
            }
        }

        return layout
    }

    private fun deletePublicacion(token:String,id:Long){
        val queue = Volley.newRequestQueue(mContext)
        val json = JSONObject()
        json.put("id",id)
        val requestBody = json.toString()
        val stringReq : StringRequest =
            object : StringRequest(
                Method.DELETE, eliminarPublicacionURL,
                Response.Listener { response ->
                    Toast.makeText(mContext,"Se elimino la publicacion", Toast.LENGTH_LONG).show()
                    val changePage = Intent(mContext, HomeActivity::class.java)
                    mContext.startActivity(changePage)
                                  },
                Response.ErrorListener { error ->
                    Log.e("ERROR", requestBody)
                    Log.e("UPDATE", error.networkResponse.toString())
                    Toast.makeText(mContext,"No se pudo eliminar publicacion", Toast.LENGTH_LONG).show()

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