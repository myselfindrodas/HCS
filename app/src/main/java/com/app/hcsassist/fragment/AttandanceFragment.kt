package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import com.app.hcsassist.databinding.FragmentAttandanceBinding
import com.app.hcsassist.modelfactory.MyAttendanceModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.MyAttendanceViewModel
import com.example.hllapplication.Adapter.AttandanceAdapter
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import java.text.SimpleDateFormat
import java.util.*


class AttandanceFragment : Fragment() {

    lateinit var fragmentAttandanceBinding: FragmentAttandanceBinding
    lateinit var mainActivity: MainActivity
    lateinit var attandanceAdapter:AttandanceAdapter
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: MyAttendanceViewModel

    //var arrayList :ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAttandanceBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_attandance,container,false)
        val root = fragmentAttandanceBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: MyAttendanceViewModel by viewModels {
            MyAttendanceModelFactory(ApiHelper(ApiClient.apiService))
        }

        viewModel = vm

        attandanceAdapter = AttandanceAdapter(mainActivity)
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(requireContext(), 1)
        fragmentAttandanceBinding.recAttandance.layoutManager = mLayoutManager
        fragmentAttandanceBinding.recAttandance.itemAnimator = DefaultItemAnimator()
        fragmentAttandanceBinding.recAttandance.adapter = attandanceAdapter

        fragmentAttandanceBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }
        val c: Calendar = Calendar.getInstance()

        c.set(Calendar.DAY_OF_MONTH, 1);
        fragmentAttandanceBinding.calenderTXT.text=SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)

        fragmentAttandanceBinding.ivNext.setOnClickListener {

            //val next_month: Int = c.get(Calendar.MONTH) + 1
            c.add(Calendar.MONTH, 1)
            fragmentAttandanceBinding.calenderTXT.text=SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)
            getAttenceList(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time))
        }
        fragmentAttandanceBinding.ivPrev.setOnClickListener {

            // val prev_month: Int = c.get(Calendar.MONTH) - 1
            c.add(Calendar.MONTH, -1)
            fragmentAttandanceBinding.calenderTXT.text=SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)
            getAttenceList(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time))
        }
        getAttenceList(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time))
        //println(SimpleDateFormat("MMMM").format(c.getTime()));

        return root
    }
    private fun getAttenceList(date: String){
        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            viewModel.getMyAttendanceList(authtoken ="Bearer "+sessionManager?.getToken()!!,
                MyAttendanceRequest(date = date)
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status==true){


                                attandanceAdapter.updateList(resource.data.data)

                            }else{

                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setMessage(resource.data?.message)
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->

                                    dialog.cancel()

                                }
                                val alert = builder.create()
                                alert.show()

                            }


                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            if (it.message!!.contains("400",true)) {
                                val builder = AlertDialog.Builder(mainActivity)
                                builder.setMessage("Date can not be greater than current month!")
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->

                                    dialog.cancel()

                                }
                                val alert = builder.create()
                                alert.show()
                            }
//                            Toast.makeText(mainActivity, it.message, Toast.LENGTH_SHORT).show()

                        }

                        Status.LOADING -> {
                            showProgressDialog()
                        }

                    }

                }
            }

        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }




    }


    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(mainActivity)
            mProgressDialog!!.setMessage("Loading...")
            mProgressDialog!!.isIndeterminate = true
        }
        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }
}