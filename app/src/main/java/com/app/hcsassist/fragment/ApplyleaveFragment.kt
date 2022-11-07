package com.app.hcsassist.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.app.hcsassist.adapter.LeaveTypeAdapter
import com.app.hcsassist.databinding.FragmentApplyleaveBinding
import com.app.hcsassist.model.LeaveTypeModel
import com.app.hcsassist.modelfactory.LeavetypeModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.LeaveViewModel
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ApplyleaveFragment : Fragment() {

    lateinit var fragmentApplyleaveBinding: FragmentApplyleaveBinding
    lateinit var mainActivity: MainActivity
    val myCalendar = Calendar.getInstance()
    var simpleDateFormat = SimpleDateFormat("dd/M/yyyy")
    var date1: Date? = null
    var date2: Date? = null
    var sessionManager: SessionManager? = null
    private lateinit var leaveTypeViewModel: LeaveViewModel
    private var list: ArrayList<LeaveTypeModel> = ArrayList()
    lateinit var leaveTypeAdapter: LeaveTypeAdapter
    var selectedleavetypeid:Int?=null
    var pathFromUri: String?=""
    var selectedfromdate:String?=""
    var selectedtodate:String?=""
    var leavefromdate:String?=""
    var leavetodate:String?=""
    var comments:String?=""
    var noofdays:String?=""
    var leaveid:String?=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentApplyleaveBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_applyleave, container, false)
        val root = fragmentApplyleaveBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: LeaveViewModel by viewModels {
            LeavetypeModelFactory(ApiHelper(ApiClient.apiService))
        }
        leaveTypeViewModel = vm

        val bundle = this.arguments
        if (bundle != null) {
            leavefromdate = bundle.getString("leavefromdate").toString()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateFormat3 = SimpleDateFormat("MMM yyyy E")
            val date = dateFormat.parse(leavefromdate)
            val date2 = dateFormat.parse(leavefromdate)
            val dateFormat2 = SimpleDateFormat("d")
            val selecteddate = dateFormat2.format(date)
            val selecteddate2 = dateFormat3.format(date2)
            fragmentApplyleaveBinding.llMarkoutattendance.tvSelectedfromdate.text = selecteddate
            fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text = selecteddate2
        } else {
            leavefromdate = ""
            val sdf = SimpleDateFormat("d")
            val sdf2 = SimpleDateFormat("MMM yyyy E")
            val currentDate = sdf.format(Date())
            val currentDate2 = sdf2.format(Date())
            fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text = currentDate2
            fragmentApplyleaveBinding.llMarkoutattendance.tvSelectedfromdate.text = currentDate

        }

        if (bundle != null) {
            leavetodate = bundle.getString("leavetodate").toString()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val dateFormat3 = SimpleDateFormat("MMM yyyy E")
            val date = dateFormat.parse(leavetodate)
            val date2 = dateFormat.parse(leavetodate)
            val dateFormat2 = SimpleDateFormat("d")
            val selecteddate = dateFormat2.format(date)
            val selecteddate2 = dateFormat3.format(date2)
            fragmentApplyleaveBinding.llMarkoutattendance.tvselectedTodate.text = selecteddate
            fragmentApplyleaveBinding.llMarkoutattendance.tvTodate.text = selecteddate2


        } else {
            leavetodate = ""
            val sdf = SimpleDateFormat("d")
            val sdf2 = SimpleDateFormat("MMM yyyy E")
            val currentDate = sdf.format(Date())
            val currentDate2 = sdf2.format(Date())
            fragmentApplyleaveBinding.llMarkoutattendance.tvTodate.text = currentDate2
            fragmentApplyleaveBinding.llMarkoutattendance.tvselectedTodate.text = currentDate
        }

        if (bundle != null) {
            comments = bundle.getString("comment").toString()
            fragmentApplyleaveBinding.llMarkoutattendance.etComment.setText(comments)
        } else {
            comments = ""
        }

        if (bundle != null) {
            noofdays = bundle.getString("noofdays").toString()
            fragmentApplyleaveBinding.llMarkoutattendance.tvTotaldays.text = noofdays + " Days"
        } else {
            noofdays = ""
        }

        if (bundle != null) {
            leaveid = bundle.getString("id").toString()
        } else {
            leaveid = ""
        }


        fragmentApplyleaveBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentApplyleaveBinding.llMarkoutattendance.btnApplyleave.setOnClickListener {

            if (selectedfromdate.equals("")){
                Toast.makeText(mainActivity, "Select From date", Toast.LENGTH_SHORT).show()
            }else if (selectedtodate.equals("")){
                Toast.makeText(mainActivity, "Select To date", Toast.LENGTH_SHORT).show()
            }else if (selectedleavetypeid==null){
                Toast.makeText(mainActivity, "Select Leave Type", Toast.LENGTH_SHORT).show()
            }else if(fragmentApplyleaveBinding.llMarkoutattendance.etComment.text.length ==0){
                Toast.makeText(mainActivity, "Enter Comment", Toast.LENGTH_SHORT).show()
            }else{

                if (pathFromUri.equals("")){

                    applyleavewithoutdoc(fragmentApplyleaveBinding.llMarkoutattendance.etComment.text.toString(), it)


                }else{
                    applyleave(fragmentApplyleaveBinding.llMarkoutattendance.etComment.text.toString(), pathFromUri.toString(), it)

                }
            }


        }


        val fromdate =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                fromdateupdateLabel()
            }

        val todate =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                todateupdateLabel()
            }

        fragmentApplyleaveBinding.llMarkoutattendance.btnFromdate.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 0)
            val datePickerDialog = DatePickerDialog(
                mainActivity, fromdate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()

        }


        fragmentApplyleaveBinding.llMarkoutattendance.btnTodate.setOnClickListener {

            if (fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text.length > 0) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, 0)
                val datePickerDialog = DatePickerDialog(
                    mainActivity, todate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            } else {
                Toast.makeText(mainActivity, "Please Select from date first!", Toast.LENGTH_SHORT)
                    .show()
            }



            fragmentApplyleaveBinding.llMarkoutattendance.imgDoc.setOnClickListener {

                ImagePicker.Companion.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
            }

        }

        leaveType()


        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == 2404 && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            try {
                pathFromUri = GetRealPathFromUri.getPathFromUri(mainActivity, fileUri!!)!!
                fragmentApplyleaveBinding.llMarkoutattendance.imgDoc.setImageURI(fileUri)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(mainActivity, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mainActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun leaveType(){

        leaveTypeViewModel.leavetype(authtoken = "Bearer " + sessionManager?.getToken())
            .observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            list = ArrayList<LeaveTypeModel>()
                            for (i in it.data?.result!!) {
                                val leaveTypeModel = LeaveTypeModel()
                                leaveTypeModel.short_code = i?.short_code
                                leaveTypeModel.id = i?.id
                                list.add(leaveTypeModel)
                            }
                            leaveTypeAdapter =
                                LeaveTypeAdapter(mainActivity, list, this)
                            fragmentApplyleaveBinding.llMarkoutattendance.rvLeavetype.setAdapter(
                                leaveTypeAdapter
                            )
                            fragmentApplyleaveBinding.llMarkoutattendance.rvLeavetype.setLayoutManager(
                                LinearLayoutManager(
                                    mainActivity,
                                    LinearLayoutManager.HORIZONTAL,
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


    private fun fromdateupdateLabel() {
        val dateFormat = "d" //In which you need put here
        val myFormat = "MMM yyyy E" //In which you need put here
        val format = "dd/M/yyyy"
        val selecteddateformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val sdf2 = SimpleDateFormat(dateFormat, Locale.US)
        val sdf3 = SimpleDateFormat(format, Locale.US)
        val sdf4 = SimpleDateFormat(selecteddateformat, Locale.US)
        date1 = simpleDateFormat.parse(sdf3.format(myCalendar.time))
        selectedfromdate = sdf4.format(myCalendar.time)
        fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.setText(sdf.format(myCalendar.time))
        fragmentApplyleaveBinding.llMarkoutattendance.tvSelectedfromdate.setText(
            sdf2.format(
                myCalendar.time
            )
        )

    }

    private fun todateupdateLabel() {
        val dateFormat = "d" //In which you need put here
        val myFormat = "MMM yyyy E" //In which you need put here
        val format = "dd/M/yyyy"
        val selecteddateformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val sdf2 = SimpleDateFormat(dateFormat, Locale.US)
        val sdf3 = SimpleDateFormat(format, Locale.US)
        val sdf4 = SimpleDateFormat(selecteddateformat, Locale.US)
        date2 = simpleDateFormat.parse(sdf3.format(myCalendar.time))
        selectedtodate = sdf4.format(myCalendar.time)
        printDifference(date1!!, date2!!)
        fragmentApplyleaveBinding.llMarkoutattendance.tvTodate.setText(sdf.format(myCalendar.time))
        fragmentApplyleaveBinding.llMarkoutattendance.tvselectedTodate.setText(
            sdf2.format(
                myCalendar.time
            )
        )

    }

    fun selectedleavetype(leaveTypeModel: LeaveTypeModel){

        selectedleavetypeid = (leaveTypeModel.id)?.toInt()
    }

    private fun applyleave(comment:String, pathFromUri: String, view: View){

        val file = File(pathFromUri)

        var id:String? = ""
        if (leaveid?.length!! >0){
            id = leaveid
        }else {
            id = "0"
        }

        val fileReqBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData("attachment", file.name, fileReqBody)
        val leave_type_id: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedleavetypeid.toString())
        val leave_date_from: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedfromdate!!)
        val leave_date_to: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedtodate!!)
        val commentvalue: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), comment)

        leaveTypeViewModel.leaveapply(
            authtoken = "Bearer " + sessionManager?.getToken(),
            leave_type_id,
            id = id!!,
            leave_date_from,
            leave_date_to,
            commentvalue,
            part = part).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            Toast.makeText(mainActivity, resource.data?.message, Toast.LENGTH_SHORT).show()
                            val navController = Navigation.findNavController(view)
                            navController.navigate(R.id.nav_leavefragment)
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

    private fun applyleavewithoutdoc(comment:String, view: View){

        var id:String? = ""
        if (leaveid?.length!! >0){
            id = leaveid
        }else {
            id = "0"
        }
        val leave_type_id: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedleavetypeid.toString())
        val leave_date_from: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedfromdate!!)
        val leave_date_to: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedtodate!!)
        val commentvalue: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), comment)

        leaveTypeViewModel.leaveapplywithoutdoc(
            authtoken = "Bearer " + sessionManager?.getToken(),
            leave_type_id,
            id = id!!,
            leave_date_from,
            leave_date_to,
            commentvalue).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        Toast.makeText(mainActivity, resource.data?.message, Toast.LENGTH_SHORT).show()
                        val navController = Navigation.findNavController(view)
                        navController.navigate(R.id.nav_leavefragment)
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



    @SuppressLint("SetTextI18n")
    fun printDifference(startDate: Date, endDate: Date) {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        Log.d(TAG, "days-->"+elapsedDays)
        fragmentApplyleaveBinding.llMarkoutattendance.tvTotaldays.text = elapsedDays.toString() +" Days"
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