package com.proyecto.app.social

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class ComentarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentario)

        var arraylist = ArrayList<Notification>();
        arraylist.add(Notification("Es increible que nuestra ciudad este asi.","Carlos Cruz"))
        arraylist.add(Notification("Para eso pagamos tantos impuestos","Edit Jimnez"))

        var mListView = findViewById<ListView>(R.id.listView)
        val notificationAdapter: NotificationAdapter = NotificationAdapter(applicationContext,arraylist)
        mListView.adapter = notificationAdapter

    }
}