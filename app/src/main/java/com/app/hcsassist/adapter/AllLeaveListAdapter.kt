package com.app.hcsassist.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.fragment.LeaveFragment
import com.app.hcsassist.model.LeaveModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AllLeaveListAdapter(
    ctx: MainActivity,
    val mFragment: Fragment
) :
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
        holder.tvtotlaLeave.text = leaveModelArrayList[position].noofdays


        if (leaveModelArrayList[position].approved_status.equals("0")) {
            holder.tvStatus.text = "Cancelled"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.red))
        } else if (leaveModelArrayList[position].approved_status.equals("1")) {
            holder.tvStatus.text = "Pending"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.yellow))
        } else if (leaveModelArrayList[position].approved_status.equals("2")) {
            holder.tvStatus.text = "Approved"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.teal_200))
        } else if (leaveModelArrayList[position].approved_status.equals("3")) {
            holder.tvStatus.text = "Rejected"
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.red))

        }

        holder.attandance_ll.setOnClickListener { itAttendance ->

            val btnPopupclose: TextView
            val tvLeaveDates: TextView
            val tvNoofdays: TextView
            val tvLeavetype: TextView
            val tvStatus: TextView
            val btnChange: LinearLayout
            val btnCancel: LinearLayout
            val tvApprovedby: TextView
            val tvLeaveComments: TextView
            val tvdoc: TextView
            val lldoc: LinearLayout
            val imgDoc: ImageView
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
            tvLeaveDates = dialog.findViewById(R.id.tvLeaveDates)
            tvNoofdays = dialog.findViewById(R.id.tvNoofdays)
            tvLeavetype = dialog.findViewById(R.id.tvLeavetype)
            tvStatus = dialog.findViewById(R.id.tvStatus)
            btnChange = dialog.findViewById(R.id.btnChange)
            btnCancel = dialog.findViewById(R.id.btnCancel)
            tvApprovedby = dialog.findViewById(R.id.tvApprovedby)
            tvLeaveComments = dialog.findViewById(R.id.tvLeaveComments)
            imgDoc = dialog.findViewById(R.id.imgDoc)
            lldoc = dialog.findViewById(R.id.lldoc)
            tvdoc = dialog.findViewById(R.id.tvdoc)
            Glide.with(ctx)
                .load(leaveModelArrayList[position].attachment)
                .circleCrop()
                .into(imgDoc)

            imgDoc.setOnClickListener {


                val btnzoomPopupclose: TextView
                val imgfullDoc: ImageView
                val dialog = Dialog(ctx)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val params = WindowManager.LayoutParams()
                dialog.setContentView(R.layout.layout_doczoom)
                params.copyFrom(dialog.getWindow()?.getAttributes())
                params.height = WindowManager.LayoutParams.MATCH_PARENT
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                params.gravity = Gravity.CENTER
                dialog.getWindow()?.setAttributes(params)
                dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                btnzoomPopupclose = dialog.findViewById(R.id.btnzoomPopupclose)
                imgfullDoc = dialog.findViewById(R.id.imgfullDoc)
                Glide.with(ctx)
                    .load(leaveModelArrayList[position].attachment)
                    .fitCenter()
                    .error(R.drawable.selecteddoc)
                    .into(imgfullDoc)

                btnzoomPopupclose.setOnClickListener {

                    dialog.dismiss()
                }

            }

            if (leaveModelArrayList[position].attachment.equals("")){
                lldoc.visibility = View.GONE
                tvdoc.visibility = View.GONE

            }else{
                lldoc.visibility = View.VISIBLE
                tvdoc.visibility = View.VISIBLE
            }


            tvLeaveDates.text =
                leaveModelArrayList[position].leave_date_from + " to " + leaveModelArrayList[position].leave_date_to
            tvNoofdays.text = leaveModelArrayList[position].noofdays
            tvLeavetype.text = leaveModelArrayList[position].leave_type

            if (leaveModelArrayList[position].approved_status.equals("0")) {
                tvStatus.text = "Cancelled"
                tvStatus.setTextColor(ctx.getResources().getColor(R.color.red))
                btnCancel.visibility= View.GONE
                btnChange.visibility= View.GONE
            } else if (leaveModelArrayList[position].approved_status.equals("1")) {
                tvStatus.text = "Pending"
                tvStatus.setTextColor(ctx.getResources().getColor(R.color.yellow))
                btnCancel.visibility= View.VISIBLE
                btnChange.visibility= View.VISIBLE
            } else if (leaveModelArrayList[position].approved_status.equals("2")) {
                tvStatus.text = "Approved"
                tvStatus.setTextColor(ctx.getResources().getColor(R.color.teal_200))
                btnCancel.visibility= View.VISIBLE
                btnChange.visibility= View.VISIBLE
            } else if (leaveModelArrayList[position].approved_status.equals("3")) {
                tvStatus.text = "Rejected"
                tvStatus.setTextColor(ctx.getResources().getColor(R.color.red))

                btnCancel.visibility= View.GONE
                btnChange.visibility= View.GONE
            }

            tvApprovedby.text = leaveModelArrayList[position].approvedname + " " + leaveModelArrayList[position].approvedlastname
            if (leaveModelArrayList[position].commnent.equals("null")){
                tvLeaveComments.text = ""
            }else{
                tvLeaveComments.text = leaveModelArrayList[position].commnent
            }

            btnChange.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("leavefromdate", leaveModelArrayList[position].leave_date_from)
                bundle.putString("leavetodate", leaveModelArrayList[position].leave_date_to)
                bundle.putString("comment", if (leaveModelArrayList[position].commnent.isNullOrEmpty()) "" else leaveModelArrayList[position].commnent)
                bundle.putString("noofdays", leaveModelArrayList[position].noofdays)
                bundle.putString("id", leaveModelArrayList[position].id)
                bundle.putString("shortcode", leaveModelArrayList[position].short_code)
                bundle.putString("attachment", leaveModelArrayList[position].attachment)
                val navController = Navigation.findNavController(itAttendance)
                navController.navigate(R.id.nav_applyleavefragment, bundle)
                dialog.dismiss()


            }


            btnCancel.setOnClickListener {
                dialog.dismiss()
                val builder = AlertDialog.Builder(ctx)
                builder.setMessage("Do you really want to cancel leave?")
                builder.setPositiveButton(
                    "yes"
                ) { dialog, which ->

                    (mFragment as LeaveFragment).leavecancel(leaveModelArrayList.get(position))


                }
                builder.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.cancel() }

                val alert = builder.create()
                alert.show()


            }


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