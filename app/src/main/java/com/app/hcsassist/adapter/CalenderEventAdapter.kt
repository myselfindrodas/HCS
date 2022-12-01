package com.app.hcsassist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.calender_event.Data
import com.app.hcsassist.apimodel.calender_event.Event
import com.app.hcsassist.apimodel.calender_event.Holiday

class CalenderEventAdapter(
    ctx: MainActivity,
    val mFragment: Fragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater
    private var calenderArrayList = ArrayList<Data>()

    val VIEW_TYPE_MESSAGE = 0
    val VIEW_TYPE_IMAGE = 1
    var eventList: ArrayList<Event>? = null
    var holidayList: ArrayList<Holiday>? = null

    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.calender_list_event_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       /* holder.tvEventName.text = calenderArrayList[position].events.
        holder.tvEventTime.text = calenderArrayList[position].attendance
        holder.ivEventColor.setColorFilter(
            ContextCompat.getColor(
                ctx,
                R.color.successtextcolor
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )*/
    }


    fun getList(): ArrayList<Data> {
        return calenderArrayList
    }

    fun updateData(list: List<Data?>?) {
        list?.forEach {
            calenderArrayList.add(it!!)
            it.events?.forEach {itEvent->
                eventList?.add(itEvent!!)
            }
            it.holidays?.forEach {itHoliday->
                holidayList?.add(itHoliday!!)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return calenderArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivEventColor: ImageView
        var tvEventName: TextView
        var tvEventTime: TextView

        init {
            ivEventColor = itemView.findViewById(R.id.ivEventColor)
            tvEventName = itemView.findViewById(R.id.tvEventName)
            tvEventTime = itemView.findViewById(R.id.tvEventTime)

        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
        //this.mssAttandanceModelArrayList = mssAttandanceModelArrayList
        this.ctx = ctx
    }

}