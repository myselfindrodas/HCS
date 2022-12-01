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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentShiftchangeListBinding
import com.app.hcsassist.model.ShiftChangeAllListModel
import com.app.hcsassist.modelfactory.ShiftchangeRequestAllListModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.ShiftchangeRequestAllListViewModel
import com.example.hllapplication.Adapter.ShiftChangeAllListAdapter
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager

class ShiftchangeListFragment : Fragment() {

    lateinit var fragmentShiftchangeListBinding: FragmentShiftchangeListBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var shiftchangeRequestAllListViewModel: ShiftchangeRequestAllListViewModel
    lateinit var shiftChangeAllListAdapter: ShiftChangeAllListAdapter
    private var shiftChangeAllList: ArrayList<ShiftChangeAllListModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShiftchangeListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shiftchange_list, container, false)
        val root = fragmentShiftchangeListBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: ShiftchangeRequestAllListViewModel by viewModels {
            ShiftchangeRequestAllListModelFactory(ApiHelper(ApiClient.apiService))
        }

        shiftchangeRequestAllListViewModel = vm


        fragmentShiftchangeListBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }


        fragmentShiftchangeListBinding.btnRequestShiftchange.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_shiftchange)
        }

        shiftchangeallList()

        return root

    }


    private fun shiftchangeallList(){

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            shiftchangeRequestAllListViewModel.shiftchangeallList(authtoken = "Bearer " + sessionManager?.getToken())
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                shiftChangeAllList = ArrayList<ShiftChangeAllListModel>()
                                for (i in it.data?.result!!) {
                                    val ShiftChangeAllListModel = ShiftChangeAllListModel()
                                    ShiftChangeAllListModel.shift_title = i?.shift_title
                                    ShiftChangeAllListModel.comment = i?.comment
                                    ShiftChangeAllListModel.company_name = i?.company_name
                                    ShiftChangeAllListModel.reporting_manager_name = i?.reporting_manager_name
                                    shiftChangeAllList.add(ShiftChangeAllListModel)
                                }
                                shiftChangeAllListAdapter = ShiftChangeAllListAdapter(mainActivity, this, shiftChangeAllList)

                                fragmentShiftchangeListBinding.rvShiftchange.setLayoutManager(
                                    LinearLayoutManager(
                                        mainActivity,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                )
                                fragmentShiftchangeListBinding.rvShiftchange.setAdapter(
                                    shiftChangeAllListAdapter
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