package com.example.hllapplication.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.R


class AttandanceAdapter(
    var context: Context,
    var modelList: List<String>,

    ) :
    RecyclerView.Adapter<AttandanceAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.attandancelayout, parent, false)
        return MyViewHolder(v)
    }

    companion object {

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.main_ll.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_leavefragment)

//        val optionsFrag = LeaveFragment()
//        (context as MainActivity).supportFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, optionsFrag, "OptionsFragment").addToBackStack(null)
//            .commit()
        }
    }


    override fun getItemCount(): Int {
        return 10
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var main_ll: RelativeLayout

        init {
            main_ll = view.findViewById(R.id.main_ll)
        }
    }


    init {
        this.context = context
        this.modelList = modelList
    }
}