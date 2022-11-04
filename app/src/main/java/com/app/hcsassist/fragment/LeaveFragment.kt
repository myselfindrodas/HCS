package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.adapter.AllLeaveListAdapter
import com.app.hcsassist.adapter.ShiftChangeListAdapter
import com.app.hcsassist.databinding.FragmentLeaveBinding
import com.app.hcsassist.model.LeaveModel
import com.app.hcsassist.model.ShiftChangeListModel
import com.app.hcsassist.modelfactory.LeaveModelFactory
import com.app.hcsassist.modelfactory.UserdetailsModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.LeaveListViewModel
import com.app.hcsassist.viewmodel.UserdetailsViewModel
import com.example.hllapplication.Adapter.AllLeavesAdapter
import com.example.hllapplication.Adapter.AttandanceAdapter
import com.example.hllapplication.Adapter.LeavesListAdapter
import com.example.hllapplication.Adapter.TypesOfLeavesAdapter
import com.example.wemu.session.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LeaveFragment : Fragment() {

    lateinit var fragmentLeaveBinding: FragmentLeaveBinding
    lateinit var typesOfLeavesAdapter:TypesOfLeavesAdapter
    var allLeavesAdapter: AllLeavesAdapter?=null
    lateinit var allLeaveListAdapter: AllLeaveListAdapter
    lateinit var leavesListAdapter: LeavesListAdapter
    var arrayList :ArrayList<String> = ArrayList()
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var leaveListViewModel: LeaveListViewModel
    private var list: ArrayList<LeaveModel> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLeaveBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_leave, container, false)
        val root = fragmentLeaveBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)

        val vm: LeaveListViewModel by viewModels {
            LeaveModelFactory(ApiHelper(ApiClient.apiService))
        }

        leaveListViewModel = vm

        allLeave()

        fragmentLeaveBinding.floatingBtn.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_applyleavefragment)
        }

        fragmentLeaveBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()

        }

//        leavesListAdapter = LeavesListAdapter(requireContext(), arrayList)
//        val mLayoutManager: RecyclerView.LayoutManager =
//            GridLayoutManager(requireContext(), 1)
//        fragmentLeaveBinding.recLeaves.layoutManager = mLayoutManager
//        fragmentLeaveBinding.recLeaves.itemAnimator = DefaultItemAnimator()
//        fragmentLeaveBinding.recLeaves.adapter = leavesListAdapter

        allLeavesAdapter = AllLeavesAdapter(
            requireContext(),
            arrayList
        )

        val horizontaLayoutManagaer =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        fragmentLeaveBinding.recLeftoverLeave.setLayoutManager(
            horizontaLayoutManagaer
        )
        fragmentLeaveBinding.recLeftoverLeave.setAdapter(allLeavesAdapter)



        typesOfLeavesAdapter = TypesOfLeavesAdapter(
            requireContext(),
            arrayList
        )

        val horizontaLayoutManagaer1 =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        fragmentLeaveBinding.recTypesleaves.setLayoutManager(
            horizontaLayoutManagaer1
        )
        fragmentLeaveBinding.recTypesleaves.setAdapter(typesOfLeavesAdapter)



        return root

    }



    private fun allLeave(){

        leaveListViewModel.leavelist(authtoken = "Bearer " + sessionManager?.getToken())
            .observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            list = ArrayList<LeaveModel>()
                            for (i in it.data?.result!!) {
                                val leaveModel = LeaveModel()
                                leaveModel.leave_date_from = i?.leave_date_from
                                leaveModel.leave_date_to = i?.leave_date_to
                                leaveModel.no_of_leave = i?.leave_type?.no_of_leave
                                leaveModel.status = i?.leave_type?.status
                                list.add(leaveModel)
                            }
                            allLeaveListAdapter =
                                AllLeaveListAdapter(mainActivity, list, this)
                            fragmentLeaveBinding.rvAllleave.setAdapter(
                                allLeaveListAdapter
                            )
                            fragmentLeaveBinding.rvAllleave.setLayoutManager(
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