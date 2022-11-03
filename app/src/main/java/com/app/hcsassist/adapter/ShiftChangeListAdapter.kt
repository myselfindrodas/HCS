package com.app.hcsassist.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class ShiftChangeListAdapter(
    ctx: MainActivity,
    shiftchangelistModelArrayList: ArrayList<ShiftChangeListModel>,
    val mFragment : Fragment) :
    RecyclerView.Adapter<ShiftChangeListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater
    private val shiftchangelistModelArrayList: ArrayList<ShiftChangeListModel>
    var ctx: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = inflater.inflate(R.layout.rv_shiftchangelist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvUsername.text = shiftchangelistModelArrayList[position].name
        holder.tvCurrentShift.text = shiftchangelistModelArrayList[position].current_shift
        holder.tvRequestedShift.text = shiftchangelistModelArrayList[position].shift_title
        Glide.with(ctx)
            .load(shiftchangelistModelArrayList[position].full_profile_image)
            .transform(CenterInside(),RoundedCorners(100))
            .into(holder.imgDp)

        holder.btnAccepet.setOnClickListener {


            val builder = AlertDialog.Builder(ctx)
            builder.setMessage("Do you really want to approved shift?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->

                (mFragment as MssFragment).acceptshiftchange(shiftchangelistModelArrayList.get(position))

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
                (mFragment as MssFragment).rejectshiftchange(shiftchangelistModelArrayList.get(position))
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()


        }
    }

    override fun getItemCount(): Int {
        return shiftchangelistModelArrayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgDp: ImageView
        var tvUsername: TextView
        var tvCurrentShift: TextView
        var tvRequestedShift: TextView
        var btnAccepet: ImageView
        var btnReject: ImageView

        init {
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
        this.shiftchangelistModelArrayList = shiftchangelistModelArrayList
        this.ctx = ctx
    }
}