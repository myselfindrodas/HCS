package com.example.hllapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.R

class TypesOfLeavesAdapter(
    var context: Context,
    var modelList: List<String>,

    ) :
    RecyclerView.Adapter<TypesOfLeavesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.types_of_leaveslayout, parent, false)
        return MyViewHolder(v)
    }

    companion object {

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