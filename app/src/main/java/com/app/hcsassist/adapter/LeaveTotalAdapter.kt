package com.app.hcsassist.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.fragment.MssFragment
import com.app.hcsassist.model.LeaveModel
import com.app.hcsassist.model.ShiftChangeListModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class LeaveTotalAdapter(
    ctx: MainActivity,
    val mFragment : Fragment) :
    RecyclerView.Adapter<LeaveTotalAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private var leaveModelArrayList: ArrayList<LeaveModel> = ArrayList()
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.types_of_leaveslayout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvLeaveName.text = leaveModelArrayList[position].leave_type?: ""
        if (leaveModelArrayList[position].no_of_leave.equals("0")){
            holder.tvTotalLeave.text = leaveModelArrayList[position].count?: ""
        }else{
            holder.tvTotalLeave.text = leaveModelArrayList[position].count+"/"+leaveModelArrayList[position].no_of_leave?: ""
        }
        val shortcode = leaveModelArrayList[position].short_code?: ""
        if (shortcode.equals("CL")){
            holder.rl_leave.background = ctx.resources.getDrawable(R.drawable.leavetype, ctx.resources.newTheme())
        }else if (shortcode.equals("SL")){
            holder.rl_leave.background = ctx.resources.getDrawable(R.drawable.leavetypegreen, ctx.resources.newTheme())
        }else if (shortcode.equals("EL")){
            holder.rl_leave.background = ctx.resources.getDrawable(R.drawable.leavetypepink, ctx.resources.newTheme())
        }else if (shortcode.equals("OOD")){
            holder.rl_leave.background = ctx.resources.getDrawable(R.drawable.leavetypeyellow, ctx.resources.newTheme())
        }else if (shortcode.equals("OOT")){
            holder.rl_leave.background = ctx.resources.getDrawable(R.drawable.leavetypegreen, ctx.resources.newTheme())
        }else {
            holder.rl_leave.background = ctx.resources.getDrawable(R.drawable.leavetype, ctx.resources.newTheme())
        }

    }

    override fun getItemCount(): Int {
        return leaveModelArrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<LeaveModel>) {
        leaveModelArrayList = list as ArrayList<LeaveModel>
        notifyDataSetChanged()
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLeaveName: TextView
        var tvTotalLeave: TextView
        var rl_leave: RelativeLayout

        init {
            tvLeaveName = itemView.findViewById(R.id.tvLeaveName)
            tvTotalLeave = itemView.findViewById(R.id.tvTotalLeave)
            rl_leave = itemView.findViewById(R.id.rl_leave)

        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.ctx = ctx
    }
}