package com.proyecto.app.social

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class NotificationAdapter(private val mContext: Context,private val listaVideo: ArrayList<Notification>) : ArrayAdapter<Notification>(mContext, 0, listaVideo) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.notification_row, parent, false)
        val notification = listaVideo[position]

        val username = layout.findViewById(R.id.txtName) as TextView
        val accion = layout.findViewById(R.id.txtSub) as TextView

        username.text = notification.user;
        accion.text = notification.action;
        return layout
    }
}