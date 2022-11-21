package com.proyecto.app.social

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        var arraylist = ArrayList<Notification>();
        arraylist.add(Notification("ha hecho un comentario en tu publicacion","Carlos Cruz:"))
        arraylist.add(Notification("ha hecho un comentario en tu publicacion","Edit Jimnez:"))
        arraylist.add(Notification("ha hecho un comentario en tu publicacion","Teodoro Guzman:"))
        arraylist.add(Notification("ha publicado en una zona de tu interes","Carlos Cruz:"))
        arraylist.add(Notification("ha publicado en una zona de tu interes","Mauricio Dominguez:"))

        var mListView = findViewById<ListView>(R.id.listView)
        val notificationAdapter: NotificationAdapter = NotificationAdapter(applicationContext,arraylist)
        mListView.adapter = notificationAdapter

    }
}