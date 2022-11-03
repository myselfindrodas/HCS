package com.example.hllapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.R

class ChatDetailsAdapter  (
    var context: Context,
    var modelList: List<String>,

    ) :
    RecyclerView.Adapter<ChatDetailsAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.chat_details_layout, parent, false)
        return MyViewHolder(v)
    }

    companion object {

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }





    override fun getItemCount(): Int {
        return 1
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {

        }
    }




    init {
        this.context = context
        this.modelList = modelList
    }
}