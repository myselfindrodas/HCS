package com.app.hcsassist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.fragment.LeaveFragment
import com.app.hcsassist.model.LeaveModel

class ShortcodeListAdapter(
    ctx: MainActivity,
    val mFragment : Fragment) :
    RecyclerView.Adapter<ShortcodeListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private var leaveModelArrayList= ArrayList<String>()
    var ctx: Context
    var row_index = -1
    var mListSize:List<LeaveModel> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.allleaves_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvshortcode.text = leaveModelArrayList[position]
        var mListSize1:ArrayList<LeaveModel> = ArrayList()
        mListSize.forEach{

            if (leaveModelArrayList[position]==it.short_code){
                mListSize1.add(it)
            }

        }

        holder.tvCount.text = mListSize1.size.toString()
        holder.btnClick.setOnClickListener {

            (mFragment as LeaveFragment).shortcodeonClick(leaveModelArrayList.get(position))
            (mFragment as LeaveFragment).changetitle(leaveModelArrayList.get(position))
            (mFragment as LeaveFragment).allleavebuttoncolorchanges()
            row_index = position
            notifyDataSetChanged()

        }

        if (row_index == position) {
            holder.btnClick.setBackgroundResource(R.drawable.select_box)
            holder.tvCount.setTextColor(Color.WHITE)
            holder.tvshortcode.setTextColor(Color.WHITE)

        } else {
            holder.btnClick.setBackgroundResource(R.drawable.grey_box)
            holder.tvCount.setTextColor(Color.BLACK)
            holder.tvshortcode.setTextColor(Color.BLACK)
        }

    }

    fun changebuttoncolor(){

        row_index = -1
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return leaveModelArrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<String>,listSize: List<LeaveModel>) {
        leaveModelArrayList = list as ArrayList<String>
        mListSize=listSize
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCount: TextView
        var tvshortcode: TextView
        var btnClick: LinearLayout

        init {
            tvshortcode = itemView.findViewById(R.id.tvshortcode)
            tvCount = itemView.findViewById(R.id.tvCount)
            btnClick = itemView.findViewById(R.id.btnClick)

        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        this.ctx = ctx
    }
}