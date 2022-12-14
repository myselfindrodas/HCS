package com.app.hcsassist.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.fragment.MssFragment
import com.app.hcsassist.apimodel.RequestedLeaveModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class LeaveAdapter(
    ctx: MainActivity,
    val mFragment : Fragment) :
    RecyclerView.Adapter<LeaveAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private var requestedleaveModelArrayList= ArrayList<RequestedLeaveModel>()
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.rv_leave, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvUsername.text = requestedleaveModelArrayList[position].name
        holder.tvFromdate.text = requestedleaveModelArrayList[position].leave_date_from
        holder.tvTodate.text = requestedleaveModelArrayList[position].leave_date_to
        holder.CbCheck.isChecked = requestedleaveModelArrayList[position].isChecked == true
        val count=ArrayList<Boolean>()
        holder.CbCheck.setOnCheckedChangeListener { compoundButton, b ->
            requestedleaveModelArrayList[position].isChecked = b


            try {
                count.clear()
                requestedleaveModelArrayList.forEach {
                    if (it.isChecked!!)
                        count.add(it.isChecked!!)
                    /*if (it.isChecked == false){
                        count-1
                      //  (mFragment as MssFragment).showMultiSelect(false)

                        //return@forEach
                    }else{

                        count+1
                        *//*(mFragment as MssFragment).showMultiSelect(true)
                    return@forEach*//*
                }*/
                }
            }finally {
                if (count.size>1){
                    (mFragment as MssFragment).showMultiSelect(true)
                }else{
                    (mFragment as MssFragment).showMultiSelect(false)

                }
            }
        }
        Glide.with(ctx)
            .load(requestedleaveModelArrayList[position].image)
            .transform(CenterInside(),RoundedCorners(100))
            .into(holder.PrfImg)

        holder.btnAccepetleave.setOnClickListener {

            val builder = AlertDialog.Builder(ctx)
            builder.setMessage("Do you really want to approved leave?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->

                (mFragment as MssFragment).acceptleave(requestedleaveModelArrayList[position].id!!)

            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()

        }

        holder.btnRejectleave.setOnClickListener {


            val builder = AlertDialog.Builder(ctx)
            builder.setMessage("Do you really want to reject leave?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->
                (mFragment as MssFragment).rejectleave(requestedleaveModelArrayList[position].id!!)
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()


        }
    }

    fun getList():ArrayList<RequestedLeaveModel>{
        return requestedleaveModelArrayList
    }

    fun updateData(list : ArrayList<RequestedLeaveModel>){
        requestedleaveModelArrayList=list
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return requestedleaveModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var CbCheck:CheckBox
        var PrfImg: ImageView
        var tvUsername: TextView
        var tvFromdate: TextView
        var tvTodate: TextView
        var btnAccepetleave: ImageView
        var btnRejectleave: ImageView

        init {
            CbCheck = itemView.findViewById(R.id.CbCheck)
            tvUsername = itemView.findViewById(R.id.tvUsername)
            PrfImg = itemView.findViewById(R.id.PrfImg)
            tvFromdate = itemView.findViewById(R.id.tvFromdate)
            tvTodate = itemView.findViewById(R.id.tvTodate)
            btnAccepetleave = itemView.findViewById(R.id.btnAccepetleave)
            btnRejectleave = itemView.findViewById(R.id.btnRejectleave)
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.requestedleaveModelArrayList = requestedleaveModelArrayList
        this.ctx = ctx
    }
}