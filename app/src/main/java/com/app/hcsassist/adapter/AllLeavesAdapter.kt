package com.example.hllapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.R

class AllLeavesAdapter(
    var context: Context,
    var modelList: List<String>,

    ) :
    RecyclerView.Adapter<AllLeavesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.allleaves_layout, parent, false)
        return MyViewHolder(v)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }





    override fun getItemCount(): Int {
        return 10
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