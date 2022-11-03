package com.example.hllapplication.Adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.R

class LeavesListAdapter(
    var context: Context,
    var modelList: List<String>,

    ) :
    RecyclerView.Adapter<LeavesListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.leaves_layout, parent, false)
        return MyViewHolder(v)
    }

    companion object {

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.attandance_ll.setOnClickListener {

            val btnPopupclose: TextView
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val params = WindowManager.LayoutParams()
            dialog.setContentView(R.layout.layout_approvedleave)
            params.copyFrom(dialog.getWindow()?.getAttributes())
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.CENTER
            dialog.getWindow()?.setAttributes(params)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            btnPopupclose = dialog.findViewById(R.id.btnPopupclose)

            btnPopupclose.setOnClickListener {

                dialog.dismiss()
            }


        }

    }





    override fun getItemCount(): Int {
        return 10
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var attandance_ll: RelativeLayout

        init {
            attandance_ll = view.findViewById(R.id.attandance_ll)
        }
    }




    init {
        this.context = context
        this.modelList = modelList
    }
}