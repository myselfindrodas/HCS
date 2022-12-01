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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.myattendance.Data
import com.app.hcsassist.model.LeaveModel
import com.app.hcsassist.model.ShiftChangeAllListModel


class ShiftChangeAllListAdapter(
    ctx: MainActivity,
    val mFragment: Fragment,
    val shiftChangeAllListModel: ArrayList<ShiftChangeAllListModel>
) :
    RecyclerView.Adapter<ShiftChangeAllListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
//    private var shiftChangeAllListModel: ArrayList<ShiftChangeAllListModel> = ArrayList()
    var ctx: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: View =
            LayoutInflater.from(ctx).inflate(R.layout.rv_shiftchangealllist, parent, false)
        return MyViewHolder(v)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvShift.text = shiftChangeAllListModel[position].shift_title
        holder.tvCommnet.text = shiftChangeAllListModel[position].comment?.capitalize()
        holder.tvCompany.text = shiftChangeAllListModel[position].company_name
        holder.tvManagername.text = shiftChangeAllListModel[position].reporting_manager_name

    }


    override fun getItemCount(): Int {
        return shiftChangeAllListModel.size
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvManagername: TextView
        var tvCompany: TextView
        var tvShift: TextView
        var tvCommnet: TextView

        init {
            tvManagername = view.findViewById(R.id.tvManagername)
            tvCompany = view.findViewById(R.id.tvCompany)
            tvShift = view.findViewById(R.id.tvShift)
            tvCommnet = view.findViewById(R.id.tvCommnet)
        }
    }


    init {
        inflater = LayoutInflater.from(ctx)
        this.ctx = ctx
    }
}