package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.adapter.AllLeaveListAdapter
import com.app.hcsassist.adapter.LeaveTotalAdapter
import com.app.hcsassist.adapter.ShortcodeListAdapter
import com.app.hcsassist.databinding.FragmentLeaveBinding
import com.app.hcsassist.model.LeaveModel
import com.app.hcsassist.modelfactory.LeaveModelFactory
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
    lateinit var allLeaveListAdapter: AllLeaveListAdapter
    lateinit var leaveTotalAdapter: LeaveTotalAdapter
    lateinit var shortcodeListAdapter: ShortcodeListAdapter
    var arrayList :ArrayList<String> = ArrayList()
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var leaveListViewModel: LeaveListViewModel
    private var list: ArrayList<LeaveModel> = ArrayList()
    private var allleavelist: ArrayList<LeaveModel> = ArrayList()
    private var shortcodelist: ArrayList<String> = ArrayList()



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
        shortcodeListAdapter = ShortcodeListAdapter(mainActivity, this)
        allLeaveListAdapter = AllLeaveListAdapter(mainActivity, this)
        leaveTotalAdapter = LeaveTotalAdapter(mainActivity, this)


        allLeave()

        fragmentLeaveBinding.floatingBtn.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_applyleavefragment)
        }

        fragmentLeaveBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()

        }



        fragmentLeaveBinding.recLeftoverLeave.setAdapter(
            shortcodeListAdapter
        )
        fragmentLeaveBinding.recLeftoverLeave.setLayoutManager(
            LinearLayoutManager(
                mainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )



//        typesOfLeavesAdapter = TypesOfLeavesAdapter(
//            requireContext(),
//            arrayList
//        )
//
//        val horizontaLayoutManagaer1 =
//            LinearLayoutManager(
//                requireContext(),
//                LinearLayoutManager.HORIZONTAL,
//                false
//            )
//        fragmentLeaveBinding.recTypesleaves.setLayoutManager(
//            horizontaLayoutManagaer1
//        )
//        fragmentLeaveBinding.recTypesleaves.setAdapter(typesOfLeavesAdapter)



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
                            shortcodelist = ArrayList<String>()
                            allleavelist = ArrayList<LeaveModel>()
                            for (i in it.data?.result!!) {
                                val leaveModel = LeaveModel()
                                leaveModel.leave_date_from = i?.leave_date_from
                                leaveModel.leave_date_to = i?.leave_date_to
                                leaveModel.no_of_leave = i?.leave_type?.no_of_leave
                                leaveModel.status = i?.leave_type?.status
                                leaveModel.short_code = i?.leave_type?.short_code
                                leaveModel.leave_type = i?.leave_type?.leave_type
                                if (!shortcodelist.contains(leaveModel.short_code)){
                                    shortcodelist.add(leaveModel.short_code!!)
                                }
                                list.add(leaveModel)
                                allleavelist.add(leaveModel)
                            }

                            shortcodeListAdapter.updateData(shortcodelist,list)
                            allLeaveListAdapter.updateData(list)
                            leaveTotalAdapter.updateData(allleavelist)



                            fragmentLeaveBinding.rvAllleave.setAdapter(allLeaveListAdapter)
                            fragmentLeaveBinding.rvAllleave.setLayoutManager(
                                LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
                            )

                            fragmentLeaveBinding.recTypesleaves.setAdapter(leaveTotalAdapter)
                            fragmentLeaveBinding.recTypesleaves.setLayoutManager(
                                LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
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


    fun shortcodeonClick(leaveModel: String){

        val mList=ArrayList<LeaveModel>()
            for (i in list){
                if (leaveModel==i.short_code){

                    mList.add(i)
                }

            }
        allLeaveListAdapter.updateData(mList)

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