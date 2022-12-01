package com.example.hllapplication.Adapter


import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.myattendance.Data


class AttandanceAdapter(
    var context: Context
) :
    RecyclerView.Adapter<AttandanceAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(context).inflate(R.layout.attandancelayout, parent, false)
        return MyViewHolder(v)
    }

    companion object {

    }

    var modelList: ArrayList<Data> = ArrayList()
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){

            modelList[position].let { modelItem->

                if (modelItem.day.isNullOrEmpty()){

                    date_txt.visibility=View.INVISIBLE
                    tvWeek.visibility=View.INVISIBLE
                }else {
                    val dayStr = modelItem.day

                    val splitStr = dayStr!!.split("\\s".toRegex())
                    val day = splitStr[0]
                    val week = splitStr[1]

                    date_txt.visibility=View.VISIBLE
                    tvWeek.visibility=View.VISIBLE
                    date_txt.text = day
                    tvWeek.text = week
                }
                if (modelItem.punchInTime.isNullOrEmpty()){
                    ivPunchIn.setImageResource(R.drawable.ic_close)
                    ImageViewCompat.setImageTintList(ivPunchIn, ColorStateList.valueOf(context.resources.getColor(R.color.red,context.resources.newTheme())))
                }else{
                    ivPunchIn.setImageResource(R.drawable.ic_check)
                    ImageViewCompat.setImageTintList(ivPunchIn, ColorStateList.valueOf(context.resources.getColor(R.color.green,context.resources.newTheme())))
                }
                if (modelItem.punchOutTime.isNullOrEmpty()){
                    ivPunchOut.setImageResource(R.drawable.ic_close)
                    ImageViewCompat.setImageTintList(ivPunchOut, ColorStateList.valueOf(context.resources.getColor(R.color.red,context.resources.newTheme())))

                }else{
                    ivPunchOut.setImageResource(R.drawable.ic_check)
                    ImageViewCompat.setImageTintList(ivPunchOut, ColorStateList.valueOf(context.resources.getColor(R.color.green,context.resources.newTheme())))

                }
                /*if (modelItem.punchOutTime.isNullOrEmpty()){
                    ivPunchOut.visibility=View.INVISIBLE
                }else{
                    ivPunchOut.visibility=View.VISIBLE
                }*/
                if (modelItem.currentShift==null ||modelItem.currentShift!!.shiftTitle==null ||modelItem.currentShift!!.shiftTitle.isNullOrEmpty()){
                    sift_time_txt.visibility=View.GONE
                }else{
                    sift_time_txt.visibility=View.VISIBLE
                    sift_time_txt.text= modelItem.currentShift!!.shiftTitle
                }
                if (modelItem.totalDutarion.isNullOrEmpty()){
                    tvWorkHour.visibility=View.INVISIBLE
                }else{
                    tvWorkHour.visibility=View.VISIBLE
                    tvWorkHour.text=modelItem.totalDutarion
                }
                if (modelItem.punchInTime!!.isNullOrEmpty() && modelItem.punchOutTime!!.isNullOrEmpty()){

                    if (modelItem.type.equals("absent",true)){
                        timeRange.visibility=View.VISIBLE
                        timeRange.text= modelItem.reason
                    } else timeRange.visibility=View.INVISIBLE

                }else{
                    timeRange.visibility=View.VISIBLE
                    if (modelItem.punchOutTime==null || modelItem.punchOutTime.isEmpty()){
                        timeRange.text=if (modelItem.type.equals("absent",true)) modelItem.reason else modelItem.punchInTime
                    }else{
                        timeRange.text=if (modelItem.type.equals("absent",true)) modelItem.reason else "${modelItem.punchInTime} - ${modelItem.punchOutTime}"
                    }
                }
            }
        }

        holder.main_ll.setOnClickListener {
            /* val navController = Navigation.findNavController(it)
             navController.navigate(R.id.nav_leavefragment)*/
        }
    }


    override fun getItemCount(): Int {
        return modelList.size
    }

    fun updateList(list: List<Data?>?){
        modelList.clear()
        list!!.forEach{
            if (it!!.id!=null)
                modelList.add(it)
        }
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var main_ll: RelativeLayout
        var date_txt: TextView
        var tvWeek: TextView
        var sift_time_txt: TextView
        var timeRange: TextView
        var tvWorkHour: TextView
        var ivPunchIn: ImageView
        var ivPunchOut: ImageView

        init {
            main_ll = view.findViewById(R.id.main_ll)
            date_txt = view.findViewById(R.id.date_txt)
            tvWeek = view.findViewById(R.id.tvWeek)
            sift_time_txt = view.findViewById(R.id.sift_time_txt)
            timeRange = view.findViewById(R.id.timeRange)
            tvWorkHour = view.findViewById(R.id.tvWorkHour)
            ivPunchIn = view.findViewById(R.id.ivPunchIn)
            ivPunchOut = view.findViewById(R.id.ivPunchOut)
        }
    }


    init {
        this.context = context
        this.modelList = modelList
    }
}