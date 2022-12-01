package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListRequest
import com.app.hcsassist.databinding.FragmentAdminhrBinding
import com.app.hcsassist.modelfactory.AdminHrModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.AdminHrListViewModel
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import java.text.SimpleDateFormat
import java.util.*


class AdminhrFragment : Fragment() {

    lateinit var fragmentAdminhrBinding: FragmentAdminhrBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: AdminHrListViewModel

    var spinnerLocationArray = ArrayList<String>()
    var spinnerLocationIdArray = ArrayList<Int>()

    val c: Calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAdminhrBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_adminhr, container, false)
        val root = fragmentAdminhrBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: AdminHrListViewModel by viewModels {
            AdminHrModelFactory(ApiHelper(ApiClient.apiService))
        }

        viewModel = vm

        with(fragmentAdminhrBinding) {

            btnBack.setOnClickListener {

                mainActivity.onBackPressedDispatcher.onBackPressed()
            }
            val date =
                OnDateSetListener { view, year, month, day ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, month)
                    c.set(Calendar.DAY_OF_MONTH, day)
                    updateLabel()
                }

            tvSelectDate.setOnClickListener {
                DatePickerDialog(
                    mainActivity,
                    date,
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            getLocationList()
            tvSelectDate.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)
            calenderTXT.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time)


        }



        return root
    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        fragmentAdminhrBinding.tvSelectDate.text = dateFormat.format(c.time)
        fragmentAdminhrBinding.calenderTXT.text = dateFormat.format(c.time)
        getAdminHrList(
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c.time),
            spinnerLocationIdArray[fragmentAdminhrBinding.spLocation.selectedItemPosition]
        )

    }

    private fun getLocationList() {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {


            viewModel.getLocationList(authtoken = "Bearer " + sessionManager?.getToken()!!)
                .observe(mainActivity) {
                    it?.let { resource ->
                        if (resource.data?.status == true) {

                            spinnerLocationArray = ArrayList<String>()
                            spinnerLocationIdArray = ArrayList<Int>()

                            for (i in resource.data.data!!) {
                                spinnerLocationArray.add(i?.locationName!!)
                                spinnerLocationIdArray.add(i.id!!)
                            }
                            /*resource.data?.data?.forEach {itData->
                                spinnerLocationArray.add(itData?.locationName!!)
                                spinnerLocationIdArray.add(itData.id!!)
                            }*/
                            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                mainActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                spinnerLocationArray
                            )
                            fragmentAdminhrBinding.spLocation.adapter = spinnerArrayAdapter
                            fragmentAdminhrBinding.spLocation.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {

                                    }

                                }
                            println(spinnerLocationIdArray[0])
                            getAdminHrList(
                                SimpleDateFormat(
                                    "dd-MM-yyyy",
                                    Locale.getDefault()
                                ).format(c.time),
                                spinnerLocationIdArray[fragmentAdminhrBinding.spLocation.selectedItemPosition]
                            )

                        }


                    }
                }

        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getAdminHrList(date: String, locationId: Int) {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {


            viewModel.getAdminHr(authtoken = "Bearer " + sessionManager?.getToken()!!,
                AdminHrListRequest(date = date, location_id = locationId))
                .observe(mainActivity) {
                    it?.let { resource ->

                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                with(fragmentAdminhrBinding) {
                                    tvPermanent.text =
                                        "Permanent : ${resource.data?.data?.permanent?.totalEmployee}"
                                    tvPermanentPresent.text =
                                        "${resource.data?.data?.permanent?.present}"
                                    tvPermanentAbsent.text = "${resource.data?.data?.permanent?.absent}"


                                    tvContract.text =
                                        "Contract : ${resource.data?.data?.contract?.totalEmployee}"
                                    tvContractPresent.text = "${resource.data?.data?.contract?.present}"
                                    tvContractAbsent.text = "${resource.data?.data?.contract?.absent}"


                                    tvThirdParty.text =
                                        "Third Party : ${resource.data?.data?.thirdparty?.totalEmployee}"
                                    tvThirdPartyPresent.text =
                                        "${resource.data?.data?.thirdparty?.present}"
                                    tvThirdPartyAbsent.text =
                                        "${resource.data?.data?.thirdparty?.absent}"

                                    val totalEmployee =
                                        if (resource.data?.data?.permanent?.totalEmployee != null) resource.data?.data?.permanent?.totalEmployee!! else 0 + if (resource.data?.data?.contract?.totalEmployee != null) resource.data?.data?.contract?.totalEmployee!! else 0 + if (resource.data?.data?.thirdparty?.totalEmployee != null) resource.data?.data?.thirdparty?.totalEmployee!! else 0
                                    val totalPresentEmployee =
                                        if (resource.data?.data?.permanent?.present != null) resource.data?.data?.permanent?.present!! else 0 + if (resource.data?.data?.contract?.present != null) resource.data?.data?.contract?.present!! else 0 + if (resource.data?.data?.thirdparty?.present != null) resource.data?.data?.thirdparty?.present!! else 0
                                    val totalAbsentEmployee =
                                        if (resource.data?.data?.permanent?.absent != null) resource.data?.data?.permanent?.absent!! else 0 + if (resource.data?.data?.contract?.absent != null) resource.data?.data?.contract?.absent!! else 0 + if (resource.data?.data?.thirdparty?.absent != null) resource.data?.data?.thirdparty?.absent!! else 0

                                    tvTotal.text = "Total Employee : $totalEmployee"
                                    tvTotalPresent.text = "$totalPresentEmployee"
                                    tvTotalAbsent.text = "$totalAbsentEmployee"
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