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

class AllLeaveListAdapter(
    ctx: MainActivity,
    val mFragment : Fragment) :
    RecyclerView.Adapter<AllLeaveListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private var leaveModelArrayList: ArrayList<LeaveModel> = ArrayList()
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.leaves_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvfromDate.text = leaveModelArrayList[position].leave_date_from
        holder.tvtoDate.text = leaveModelArrayList[position].leave_date_to
        holder.tvtotlaLeave.text = leaveModelArrayList[position].no_of_leave


        if (leaveModelArrayList[position].status.equals("0")){
            holder.tvStatus.text = "Cancel"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.red))
        }else if (leaveModelArrayList[position].status.equals("1")){
            holder.tvStatus.text = "Pending"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.yellow))
        }else if (leaveModelArrayList[position].status.equals("2")){
            holder.tvStatus.text = "approved"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.teal_200))
        }else if (leaveModelArrayList[position].status.equals("3")){
            holder.tvStatus.text = "rejected"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.red))

        }

        holder.attandance_ll.setOnClickListener {

            val btnPopupclose: TextView
            val dialog = Dialog(ctx)
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<LeaveModel>) {
        leaveModelArrayList = list as ArrayList<LeaveModel>
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return leaveModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvfromDate: TextView
        var tvtoDate: TextView
        var tvtotlaLeave: TextView
        var tvStatus: TextView
        var attandance_ll: RelativeLayout

        init {
            tvfromDate = itemView.findViewById(R.id.tvfromDate)
            tvtoDate = itemView.findViewById(R.id.tvtoDate)
            tvtotlaLeave = itemView.findViewById(R.id.tvtotlaLeave)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            attandance_ll = itemView.findViewById(R.id.attandance_ll)

        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.ctx = ctx
    }
}