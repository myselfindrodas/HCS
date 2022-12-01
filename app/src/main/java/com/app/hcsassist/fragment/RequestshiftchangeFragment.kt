package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.app.hcsassist.Login
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.ShiftChangeRequest
import com.app.hcsassist.databinding.FragmentRequestshiftchangeBinding
import com.app.hcsassist.model.ShiftlistModel
import com.app.hcsassist.modelfactory.ShiftListModelFactory
import com.app.hcsassist.modelfactory.ShiftchangeRequestModelFactory
import com.app.hcsassist.modelfactory.UserdetailsModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.ShiftListViewModel
import com.app.hcsassist.viewmodel.ShiftchangeRequestViewModel
import com.app.hcsassist.viewmodel.UserdetailsViewModel
import com.bumptech.glide.Glide
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class RequestshiftchangeFragment : Fragment() {

    lateinit var fragmentRequestshiftchangeBinding: FragmentRequestshiftchangeBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    var shiftid:String?=""
    private lateinit var shiftlistviewmodel: ShiftListViewModel
    private lateinit var shiftchangeRequestViewModel: ShiftchangeRequestViewModel
    var shiftlistModelArrayList: ArrayList<ShiftlistModel> = ArrayList<ShiftlistModel>()
    var selectedshiftname: String = ""
    val shiftname = ArrayList<String>()
    val myCalendar = Calendar.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRequestshiftchangeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_requestshiftchange,container,false)
        val root = fragmentRequestshiftchangeBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: ShiftListViewModel by viewModels {
            ShiftListModelFactory(ApiHelper(ApiClient.apiService))
        }

        val shiftrequestvm: ShiftchangeRequestViewModel by viewModels {
            ShiftchangeRequestModelFactory(ApiHelper(ApiClient.apiService))
        }
        shiftlistviewmodel = vm
        shiftchangeRequestViewModel = shiftrequestvm


        fragmentRequestshiftchangeBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        val eventdate = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            eventdateupdateLabel()
        }

        fragmentRequestshiftchangeBinding.tvDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 0)
            val datePickerDialog = DatePickerDialog(
                mainActivity, eventdate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()

        }

        spShifttime()


        fragmentRequestshiftchangeBinding.spShiftchange.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                if (position > 0) {
                    shiftid = shiftlistModelArrayList.get(position - 1).id
                    selectedshiftname = shiftlistModelArrayList.get(position-1).shift_title
                    Log.d(ContentValues.TAG, "shiftid --->" + shiftid)
                    Log.d(ContentValues.TAG, "selectedshiftname --->" + selectedshiftname)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        fragmentRequestshiftchangeBinding.btnSendrequest.setOnClickListener {

            if (fragmentRequestshiftchangeBinding.tvDate.text.toString().length == 0){
                Toast.makeText(context, "Select Date!", Toast.LENGTH_SHORT).show()
            }else if (shiftid!!.length == 0){
                Toast.makeText(context, "Select Shift!", Toast.LENGTH_SHORT).show()
            }else if (fragmentRequestshiftchangeBinding.etComment.text.toString().length==0){
                Toast.makeText(context, "Enter Comments", Toast.LENGTH_SHORT).show()
            }else{

                sendRequest(fragmentRequestshiftchangeBinding.tvDate.text.toString(),
                    shiftid.toString(),
                    fragmentRequestshiftchangeBinding.etComment.text.toString())
            }

        }


        return root
    }


    private fun sendRequest(date:String, shiftid:String, comment:String){

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            shiftchangeRequestViewModel.shiftchangerequest(authtoken ="Bearer "+sessionManager?.getToken(),
                ShiftChangeRequest(date_from = date, shift_id = shiftid, comment = comment)).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status==true){
                                Toast.makeText(mainActivity, resource.data.message, Toast.LENGTH_SHORT).show()
                                val navController = Navigation.findNavController(requireView())
                                navController.popBackStack(R.id.nav_home, false)

                            }else{

                                Toast.makeText(mainActivity, resource.data?.message, Toast.LENGTH_SHORT).show()


                            }


                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            val builder = AlertDialog.Builder(mainActivity)
                            builder.setMessage(it.message)
                            builder.setPositiveButton(
                                "Ok"
                            ) { dialog, which ->

                                dialog.cancel()

                            }
                            val alert = builder.create()
                            alert.show()
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



    private fun eventdateupdateLabel() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        fragmentRequestshiftchangeBinding.tvDate.setText(sdf.format(myCalendar.time))
    }


    private fun spShifttime(){

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            shiftlistviewmodel.shiftlist(authtoken ="Bearer "+sessionManager?.getToken()).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status==true){
                                shiftname.clear()
                                shiftlistModelArrayList.clear()
                                shiftname.add("Select Shift")
                                shiftlistModelArrayList = ArrayList<ShiftlistModel>()
                                for (i in it.data?.result!!) {
                                    shiftname.add("${i?.shift_title} - (${i?.start_time}-${i?.end_time})")
                                    val ShiftlistModel = ShiftlistModel(i?.id!!, i.shift_title!!)
                                    shiftlistModelArrayList.add(ShiftlistModel)
                                }

                                if (isAdded){
                                    val spinnerArrayAdapter: ArrayAdapter<String> =
                                        ArrayAdapter<String>(
                                            requireContext(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            shiftname
                                        )
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    fragmentRequestshiftchangeBinding.spShiftchange.setAdapter(spinnerArrayAdapter)
                                }


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
                            val builder = AlertDialog.Builder(mainActivity)
                            builder.setMessage(it.message)
                            builder.setPositiveButton(
                                "Ok"
                            ) { dialog, which ->

                                dialog.cancel()

                            }
                            val alert = builder.create()
                            alert.show()
//                        Toast.makeText(mainActivity, it.message, Toast.LENGTH_SHORT).show()

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