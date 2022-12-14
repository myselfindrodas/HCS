package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.adapter.LeaveAdapter
import com.app.hcsassist.adapter.ShiftChangeListAdapter
import com.app.hcsassist.apimodel.LeaveApprovalRequest
import com.app.hcsassist.apimodel.RequestedLeaveModel
import com.app.hcsassist.apimodel.ShiftApprovalRequest
import com.app.hcsassist.databinding.FragmentMssBinding
import com.app.hcsassist.model.ShiftChangeListModel
import com.app.hcsassist.modelfactory.RequestleavelistModelFactory
import com.app.hcsassist.modelfactory.ShiftChangeListModelFactory
import com.app.hcsassist.modelfactory.ShiftchangeApprovalModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.RequestedLeavelistViewModel
import com.app.hcsassist.viewmodel.ShiftChangeListViewModel
import com.app.hcsassist.viewmodel.ShiftchangeApprovalViewModel
import com.example.wemu.session.SessionManager

class MssFragment : Fragment() {

    lateinit var fragmentMssBinding: FragmentMssBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    lateinit var shiftChangeListAdapter: ShiftChangeListAdapter
    lateinit var leaveAdapter: LeaveAdapter
    private var list: ArrayList<ShiftChangeListModel> = ArrayList()
    private var leavelist: ArrayList<RequestedLeaveModel> = ArrayList()
    lateinit var shiftChangeListViewModel: ShiftChangeListViewModel
    lateinit var shiftchangeApprovalViewModel: ShiftchangeApprovalViewModel
    lateinit var requestedLeavelistViewModel: RequestedLeavelistViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMssBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_mss, container, false)
        val root = fragmentMssBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: ShiftChangeListViewModel by viewModels {
            ShiftChangeListModelFactory(ApiHelper(ApiClient.apiService))
        }

        val shiftchangeapprovalvm: ShiftchangeApprovalViewModel by viewModels {
            ShiftchangeApprovalModelFactory(ApiHelper(ApiClient.apiService))
        }

        val requestedleavelistvm: RequestedLeavelistViewModel by viewModels {
            RequestleavelistModelFactory(ApiHelper(ApiClient.apiService))
        }


        shiftChangeListViewModel = vm
        shiftchangeApprovalViewModel = shiftchangeapprovalvm
        requestedLeavelistViewModel = requestedleavelistvm


        fragmentMssBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }


        fragmentMssBinding.tvAttendance.setOnClickListener {

            fragmentMssBinding.tvAttendance.setTextColor(getResources().getColor(R.color.selectedtextcolor))
            fragmentMssBinding.tvLeave.setTextColor(getResources().getColor(R.color.diselectedtextcolor))
            fragmentMssBinding.tvShiftchange.setTextColor(getResources().getColor(R.color.diselectedtextcolor))
            fragmentMssBinding.viewattendance.visibility = View.VISIBLE
            fragmentMssBinding.viewleave.visibility = View.INVISIBLE
            fragmentMssBinding.viewshiftchange.visibility = View.INVISIBLE
            fragmentMssBinding.includeAttendance.llAttendance.visibility = View.VISIBLE
            fragmentMssBinding.includeLeave.llLeave.visibility = View.GONE
            fragmentMssBinding.llAccept.visibility = View.GONE
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.GONE

        }


        fragmentMssBinding.tvLeave.setOnClickListener {

            fragmentMssBinding.tvAttendance.setTextColor(resources.getColor(R.color.diselectedtextcolor,resources.newTheme()))
            fragmentMssBinding.tvLeave.setTextColor(resources.getColor(R.color.selectedtextcolor,resources.newTheme()))
            fragmentMssBinding.tvShiftchange.setTextColor(resources.getColor(R.color.diselectedtextcolor,resources.newTheme()))
            fragmentMssBinding.viewattendance.visibility = View.INVISIBLE
            fragmentMssBinding.viewleave.visibility = View.VISIBLE
            fragmentMssBinding.viewshiftchange.visibility = View.INVISIBLE
            fragmentMssBinding.includeAttendance.llAttendance.visibility = View.GONE
            fragmentMssBinding.includeLeave.llLeave.visibility = View.VISIBLE
            //fragmentMssBinding.includeLeave.llAccept.visibility = View.GONE
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.GONE
            leavelist()

        }


        fragmentMssBinding.includeLeave.cbCheckAll.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                leavelist.forEach {
                    it.isChecked=true
                }
                fragmentMssBinding.llAccept.visibility = View.VISIBLE
            }else{
                leavelist.forEach {
                    it.isChecked=false
                }
                fragmentMssBinding.llAccept.visibility = View.GONE
            }
            leaveAdapter.updateData(leavelist)
        }


        fragmentMssBinding.btnAcceptList.setOnClickListener {


            val builder = AlertDialog.Builder(mainActivity)
            builder.setMessage("Do you really want to approved leave?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->

                val list=ArrayList<String>()
                leaveAdapter.getList().forEach {
                    if (it.isChecked==true)
                        list.add(it.id!!)
                }
                val listString: String = TextUtils.join(", ",  list)

                acceptleave(listString)
                //(mFragment as MssFragment).acceptleave(requestedleaveModelArrayList[position].id!!)

            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()

        }
        fragmentMssBinding.btnRejectList.setOnClickListener {


            val builder = AlertDialog.Builder(mainActivity)
            builder.setMessage("Do you really want to reject leave?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->
                dialog.dismiss()
                val dialoglayout = layoutInflater.inflate(R.layout.reject_leave_dialog, null)
                val edittext = dialoglayout.findViewById<EditText>(R.id.etComment)

                val builder1 = AlertDialog.Builder(mainActivity)
                builder1.setView(dialoglayout)
                builder1.setPositiveButton("Submit") { dialog1, which1 ->

                    val list=ArrayList<String>()
                    leaveAdapter.getList().forEach {
                        if (it.isChecked==true)
                            list.add(it.id!!)
                    }
                    val listString: String = TextUtils.join(", ", list  )

                    rejectleave(listString,edittext.text.toString())


                }
                val alert1 = builder1.create()
                alert1.show()

                // (mFragment as MssFragment).rejectleave(requestedleaveModelArrayList[position].id!!)
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which ->

                dialog.cancel() }

            val alert = builder.create()
            alert.show()


        }
        fragmentMssBinding.tvShiftchange.setOnClickListener {

            fragmentMssBinding.tvAttendance.setTextColor(getResources().getColor(R.color.diselectedtextcolor))
            fragmentMssBinding.tvLeave.setTextColor(getResources().getColor(R.color.diselectedtextcolor))
            fragmentMssBinding.tvShiftchange.setTextColor(getResources().getColor(R.color.selectedtextcolor))
            fragmentMssBinding.viewattendance.visibility = View.INVISIBLE
            fragmentMssBinding.viewleave.visibility = View.INVISIBLE
            fragmentMssBinding.viewshiftchange.visibility = View.VISIBLE
            fragmentMssBinding.includeAttendance.llAttendance.visibility = View.GONE
            fragmentMssBinding.includeLeave.llLeave.visibility = View.GONE
            fragmentMssBinding.llAccept.visibility = View.GONE
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.VISIBLE
            shiftchangelist()

        }

        return root
    }


    fun showMultiSelect(isVisible:Boolean){
        fragmentMssBinding.llAccept.visibility =  if (isVisible)View.VISIBLE else View.GONE

    }
    private fun shiftchangelist() {

        shiftChangeListViewModel.shiftchangelist(authtoken = "Bearer " + sessionManager?.getToken())
            .observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            list = ArrayList<ShiftChangeListModel>()
                            for (i in it.data?.result!!) {
                                val shiftChangeListModel = ShiftChangeListModel()
                                shiftChangeListModel.name = i?.user?.name + " " + i?.user?.last_name
                                shiftChangeListModel.shift_title = i?.shift?.shift_title
                                shiftChangeListModel.full_profile_image =
                                    i?.user?.full_profile_image
                                shiftChangeListModel.current_shift = i?.currentShift?.shift_title
                                shiftChangeListModel.id = i?.id
                                list.add(shiftChangeListModel)
                            }
                            shiftChangeListAdapter =
                                ShiftChangeListAdapter(mainActivity, list, this)
                            fragmentMssBinding.includeShiftchange.rvShiftchangelist.setAdapter(
                                shiftChangeListAdapter
                            )
                            fragmentMssBinding.includeShiftchange.rvShiftchangelist.setLayoutManager(
                                LinearLayoutManager(
                                    mainActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            )

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


    }

    private fun leavelist(){

        requestedLeavelistViewModel.requestedleavelist(authtoken = "Bearer " + sessionManager?.getToken())
            .observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            leavelist = ArrayList<RequestedLeaveModel>()
                            leaveAdapter = LeaveAdapter(mainActivity, this)
                            fragmentMssBinding.includeLeave.rvleave.layoutManager =
                                LinearLayoutManager(
                                    mainActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            fragmentMssBinding.includeLeave.rvleave.adapter = leaveAdapter
                            for (i in it.data?.result!!) {
                                val RequestedLeaveModel = RequestedLeaveModel()
                                RequestedLeaveModel.name = i?.data?.name + " " + i?.data?.last_name
                                RequestedLeaveModel.leave_date_from = i?.leave_date_from
                                RequestedLeaveModel.leave_date_to = i?.leave_date_to
                                RequestedLeaveModel.image = i?.data?.full_profile_image
                                RequestedLeaveModel.id = i?.id
                                leavelist.add(RequestedLeaveModel)
                            }
                            leaveAdapter.updateData(leavelist)

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

    }


    fun acceptshiftchange(shiftChangeListModel: ShiftChangeListModel) {

        shiftchangeApprovalViewModel.shiftchangeapproval(
            authtoken = "Bearer " + sessionManager?.getToken(),
            ShiftApprovalRequest(request_id = shiftChangeListModel.id, status = "2")
        ).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        shiftchangelist()
                        Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT).show()

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

    }


    fun rejectshiftchange(shiftChangeListModel: ShiftChangeListModel) {

        shiftchangeApprovalViewModel.shiftchangeapproval(
            authtoken = "Bearer " + sessionManager?.getToken(),
            ShiftApprovalRequest(request_id = shiftChangeListModel.id, status = "3")
        ).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        shiftchangelist()
                        Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT).show()

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

    }


    fun acceptleave(requestedLeaveId: String){

        requestedLeavelistViewModel.approveleave(
            authtoken = "Bearer " + sessionManager?.getToken(),
            LeaveApprovalRequest(request_id = requestedLeaveId, status = "2")
        ).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        leavelist()
                        Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT).show()

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

    }


    fun rejectleave(requestedLeaveId:String,comment:String=""){

        requestedLeavelistViewModel.approveleave(
            authtoken = "Bearer " + sessionManager?.getToken(),
            LeaveApprovalRequest(request_id = requestedLeaveId, status = "3",)
        ).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        leavelist()
                        Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT).show()

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