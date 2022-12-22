package com.app.hcsassist.adapter

import android.annotation.SuppressLint
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
import com.app.hcsassist.apimodel.mss_attendance.Data
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.*
import kotlin.collections.ArrayList

class MssAttendaceAdapter(
    ctx: MainActivity,
    val mFragment : Fragment) :
    RecyclerView.Adapter<MssAttendaceAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private var mssAttandanceModelArrayList= ArrayList<Data>()
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.mss_attendance_adapter, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvType.text = mssAttandanceModelArrayList[position].empTypeName
        holder.tvStatus.text = mssAttandanceModelArrayList[position].attendance?.replaceFirstChar {
            it.uppercase()
        }
        holder.tvName.text = "${mssAttandanceModelArrayList[position].name} ${mssAttandanceModelArrayList[position].lastName}"

        Glide.with(ctx)
            .load(mssAttandanceModelArrayList[position].profileImage)
            .error(R.drawable.user)
            .circleCrop()
            .into(holder.PrfImg)

    }

    fun getList():ArrayList<Data>{
        return mssAttandanceModelArrayList
    }

    fun updateData(list: List<Data?>?){
        mssAttandanceModelArrayList.clear()
        list?.forEach {
            mssAttandanceModelArrayList.add(it!!)
        }
        notifyDataSetChanged()
    }

    fun addData(list: List<Data?>?){
        val lastIndex: Int = mssAttandanceModelArrayList.size
        list?.forEach {
        mssAttandanceModelArrayList.add(it!!)
    }
        notifyItemRangeInserted(lastIndex, mssAttandanceModelArrayList.size)
    }
    override fun getItemCount(): Int {
        return mssAttandanceModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var PrfImg: ImageView
        var tvType: TextView
        var tvStatus: TextView
        var tvName: TextView

        init {
            tvType = itemView.findViewById(R.id.tvType)
            PrfImg = itemView.findViewById(R.id.ivProfile)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            tvName = itemView.findViewById(R.id.tvName)
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.ctx = ctx
    }
}