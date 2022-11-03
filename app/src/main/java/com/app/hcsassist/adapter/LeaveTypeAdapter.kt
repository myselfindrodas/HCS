package com.app.hcsassist.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.fragment.ApplyleaveFragment
import com.app.hcsassist.model.LeaveTypeModel

class LeaveTypeAdapter(
    ctx: MainActivity,
    leavetypeModelArrayList: ArrayList<LeaveTypeModel>,
    val mFragment : Fragment) :
    RecyclerView.Adapter<LeaveTypeAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private val leavetypeModelArrayList: ArrayList<LeaveTypeModel>
    var ctx: Context
    var row_index = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.rv_leavetype, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvLeavename.text = leavetypeModelArrayList[position].short_code
        holder.ll_leavetype.setOnClickListener {

            (mFragment as ApplyleaveFragment).selectedleavetype(leavetypeModelArrayList.get(position))

            row_index = position
            notifyDataSetChanged()

        }

        if (row_index == position) {
            holder.ll_leavetype.setBackgroundResource(R.drawable.roundedborder2)
            holder.tvLeavename.setTextColor(Color.WHITE)

        } else {
            holder.ll_leavetype.setBackgroundResource(R.drawable.roundedborder)
            holder.tvLeavename.setTextColor(Color.BLACK)
        }


    }

    override fun getItemCount(): Int {
        return leavetypeModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLeavename: TextView
        var ll_leavetype:LinearLayout


        init {
            tvLeavename = itemView.findViewById(R.id.tvLeavename)
            ll_leavetype = itemView.findViewById(R.id.ll_leavetype)
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.leavetypeModelArrayList = leavetypeModelArrayList
        this.ctx = ctx
    }
}