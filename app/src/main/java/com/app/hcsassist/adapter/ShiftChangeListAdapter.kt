package com.app.hcsassist.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.RequestedLeaveModel
import com.app.hcsassist.fragment.MssFragment
import com.app.hcsassist.model.ShiftChangeListModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ShiftChangeListAdapter(
    ctx: MainActivity,
    val mFragment : Fragment) :
    RecyclerView.Adapter<ShiftChangeListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private var shiftchangelistModelArrayList= ArrayList<ShiftChangeListModel>()
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.rv_shiftchangelist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvUsername.text = shiftchangelistModelArrayList[position].name?:""
        holder.tvCurrentShift.text = shiftchangelistModelArrayList[position].current_shift?:"".replaceFirstChar {
            it.uppercase()
        }
        holder.tvRequestedShift.text = shiftchangelistModelArrayList[position].shift_title?:"".replaceFirstChar {
            it.uppercase()
        }
        holder.CbCheck.isChecked = shiftchangelistModelArrayList[position].isChecked == true
        val count=ArrayList<Boolean>()
//        val params =
//            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//        if (shiftchangelistModelArrayList.size-1==position) {
//            params.setMargins(0, 0, 0, 250)
//        }else{
//            params.setMargins(0, 0, 0, 0)
//        }
//        holder.llMain.layoutParams=params

        holder.CbCheck.setOnCheckedChangeListener { compoundButton, b ->
            shiftchangelistModelArrayList[position].isChecked = b


            try {
                count.clear()
                shiftchangelistModelArrayList.forEach {
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
            .load(shiftchangelistModelArrayList[position].full_profile_image)
            .circleCrop()
            .error(R.drawable.user)
            .into(holder.imgDp)

        holder.btnAccepet.setOnClickListener {


            val builder = AlertDialog.Builder(ctx)
            builder.setMessage("Do you really want to approved shift?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->

                (mFragment as MssFragment).acceptshiftchange(shiftchangelistModelArrayList.get(position).id.toString())

            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()


        }

        holder.btnReject.setOnClickListener {

            val builder = AlertDialog.Builder(ctx)
            builder.setMessage("Do you really want to reject shift?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->
                (mFragment as MssFragment).rejectshiftchange(shiftchangelistModelArrayList.get(position).id.toString())
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()


        }


        holder.llMain.setOnClickListener {

            (mFragment as MssFragment).shiftChangereasonpopup(shiftchangelistModelArrayList[position])


        }
    }

    override fun getItemCount(): Int {
        return shiftchangelistModelArrayList.size
    }

    fun getList():ArrayList<ShiftChangeListModel>{
        return shiftchangelistModelArrayList
    }

    fun updateData(list : ArrayList<ShiftChangeListModel>){
//        shiftchangelistModelArrayList.clear()
        shiftchangelistModelArrayList=list
        notifyDataSetChanged()
    }
    fun addData(list : ArrayList<ShiftChangeListModel>){
        val lastIndex: Int = shiftchangelistModelArrayList.size
        shiftchangelistModelArrayList.addAll(list)
        notifyItemRangeInserted(lastIndex, shiftchangelistModelArrayList.size)
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llMain: LinearLayout
        var CbCheck: CheckBox
        var imgDp: ImageView
        var tvUsername: TextView
        var tvCurrentShift: TextView
        var tvRequestedShift: TextView
        var btnAccepet: ImageView
        var btnReject: ImageView

        init {
            llMain = itemView.findViewById(R.id.llMain)
            CbCheck = itemView.findViewById(R.id.CbCheck)
            imgDp = itemView.findViewById(R.id.imgDp)
            tvUsername = itemView.findViewById(R.id.tvUsername)
            tvCurrentShift = itemView.findViewById(R.id.tvCurrentShift)
            tvRequestedShift = itemView.findViewById(R.id.tvRequestedShift)
            btnAccepet = itemView.findViewById(R.id.btnAccepet)
            btnReject = itemView.findViewById(R.id.btnReject)
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
       // this.shiftchangelistModelArrayList = shiftchangelistModelArrayList
        this.ctx = ctx
    }
}