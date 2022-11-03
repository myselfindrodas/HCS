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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.adapter.ShiftChangeListAdapter
import com.app.hcsassist.apimodel.ShiftApprovalRequest
import com.app.hcsassist.databinding.FragmentMssBinding
import com.app.hcsassist.model.ShiftChangeListModel
import com.app.hcsassist.modelfactory.ShiftChangeListModelFactory
import com.app.hcsassist.modelfactory.ShiftchangeApprovalModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.ShiftChangeListViewModel
import com.app.hcsassist.viewmodel.ShiftchangeApprovalViewModel
import com.example.wemu.session.SessionManager

class MssFragment : Fragment() {

    lateinit var fragmentMssBinding: FragmentMssBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    lateinit var shiftChangeListAdapter: ShiftChangeListAdapter
    private var list: ArrayList<ShiftChangeListModel> = ArrayList()
    lateinit var shiftChangeListViewModel: ShiftChangeListViewModel
    lateinit var shiftchangeApprovalViewModel: ShiftchangeApprovalViewModel


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
        shiftChangeListViewModel = vm
        shiftchangeApprovalViewModel = shiftchangeapprovalvm

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
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.GONE

        }


        fragmentMssBinding.tvLeave.setOnClickListener {

            fragmentMssBinding.tvAttendance.setTextColor(getResources().getColor(R.color.diselectedtextcolor))
            fragmentMssBinding.tvLeave.setTextColor(getResources().getColor(R.color.selectedtextcolor))
            fragmentMssBinding.tvShiftchange.setTextColor(getResources().getColor(R.color.diselectedtextcolor))
            fragmentMssBinding.viewattendance.visibility = View.INVISIBLE
            fragmentMssBinding.viewleave.visibility = View.VISIBLE
            fragmentMssBinding.viewshiftchange.visibility = View.INVISIBLE
            fragmentMssBinding.includeAttendance.llAttendance.visibility = View.GONE
            fragmentMssBinding.includeLeave.llLeave.visibility = View.VISIBLE
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.GONE

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
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.VISIBLE
            shiftchangelist()

        }

        return root
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