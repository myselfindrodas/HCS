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
import com.app.hcsassist.model.ShiftChangeListModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class LeaveAdapter(
    ctx: MainActivity,
    shiftchangelistModelArrayList: ArrayList<ShiftChangeListModel>,
    val mFragment : Fragment) :
    RecyclerView.Adapter<LeaveAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private val shiftchangelistModelArrayList: ArrayList<ShiftChangeListModel>
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.rv_leave, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvUsername.text = shiftchangelistModelArrayList[position].name

        holder.btnAccepetleave.setOnClickListener {




        }

        holder.btnRejectleave.setOnClickListener {



        }
    }

    override fun getItemCount(): Int {
        return shiftchangelistModelArrayList.size
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
        this.shiftchangelistModelArrayList = shiftchangelistModelArrayList
        this.ctx = ctx
    }
}