package com.app.hcsassist.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.RequestedLeaveModel
import com.app.hcsassist.fragment.MssFragment
import com.bumptech.glide.Glide

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
//        val params =
//            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//        if (requestedleaveModelArrayList.size-1==position) {
//            params.setMargins(0, 0, 0, 250)
//        }else{
//            params.setMargins(0, 0, 0, 0)
//        }
//
//        holder.rlMain.layoutParams=params
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
            .circleCrop()
            .error(R.drawable.user)
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

                val dialoglayout = mFragment.layoutInflater.inflate(R.layout.reject_leave_dialog, null)
                val edittext = dialoglayout.findViewById<EditText>(R.id.etComment)

                val builder1 = AlertDialog.Builder(ctx)
                builder1.setView(dialoglayout)
                builder1.setPositiveButton("Submit") { dialog1, which1 ->

                    (mFragment as MssFragment).rejectleave(requestedleaveModelArrayList[position].id!!, edittext.text.toString())


                }
                val alert1 = builder1.create()
                alert1.show()

            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()


        }



        holder.rlMain.setOnClickListener {

            (mFragment as MssFragment).leaveDetailspopup(requestedleaveModelArrayList[position])

        }
    }

    fun getList():ArrayList<RequestedLeaveModel>{
        return requestedleaveModelArrayList
    }

    fun updateData(list : ArrayList<RequestedLeaveModel>){
        //requestedleaveModelArrayList.clear()
        requestedleaveModelArrayList=list
        notifyDataSetChanged()
    }

    fun addData(list : ArrayList<RequestedLeaveModel>){
        val lastIndex: Int = requestedleaveModelArrayList.size
        requestedleaveModelArrayList.addAll(list)
        notifyItemRangeInserted(lastIndex, requestedleaveModelArrayList.size)
    }
    override fun getItemCount(): Int {
        return requestedleaveModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlMain:RelativeLayout
        var CbCheck:CheckBox
        var PrfImg: ImageView
        var tvUsername: TextView
        var tvFromdate: TextView
        var tvTodate: TextView
        var btnAccepetleave: ImageView
        var btnRejectleave: ImageView

        init {
            rlMain = itemView.findViewById(R.id.rlMain)
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