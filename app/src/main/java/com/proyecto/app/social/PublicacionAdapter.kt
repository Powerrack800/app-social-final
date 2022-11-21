package com.proyecto.app.social

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

class PublicacionAdapter(private val mContext: Context, private val listaVideo: ArrayList<Publicacion>) : ArrayAdapter<Publicacion>(mContext, 0, listaVideo) {
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
        image.setImageDrawable(
            ContextCompat.getDrawable(
                mContext, // Context
                R.drawable.problema // Drawable
            )
        )
        if(publicacion.delete){
            comentarBtn.visibility = View.INVISIBLE
            menuBtn.setOnClickListener{
                val popupMenu = PopupMenu(mContext,it)
                popupMenu.inflate(R.menu.menu_delete)
                popupMenu.show();
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
}