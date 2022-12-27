package com.app.hcsassist.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
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
import com.bumptech.glide.Glide
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.layout_applyleave.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class ApplyleaveFragment : Fragment() {

    lateinit var fragmentApplyleaveBinding: FragmentApplyleaveBinding
    lateinit var mainActivity: MainActivity
    var myCalendarfromdate = Calendar.getInstance()
    val myCalendartodate = Calendar.getInstance()
    var simpleDateFormat = SimpleDateFormat("dd/M/yyyy")
    var date1: Date? = null
    var date3: Date? = null
    var sessionManager: SessionManager? = null
    private lateinit var leaveTypeViewModel: LeaveViewModel
    private var list: ArrayList<LeaveTypeModel> = ArrayList()
    lateinit var leaveTypeAdapter: LeaveTypeAdapter
    var selectedleavetypeid: Int? = null
    var pathFromUri: String? = ""
    var selectedfromdate: String? = ""
    var selectedtodate: String? = ""
    var leavefromdate: String? = ""
    var leavetodate: String? = ""
    var comments: String? = ""
    var noofdays: String? = ""
    var leaveid: String? = ""
    var shortcode: String? = ""
    var attachment: String? = ""
    var uri: URI? = null
    var url: URL? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("WrongThread")
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

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

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
            date1 = date
            date3 = date2
            myCalendarfromdate.time = date
            selectedfromdate = leavefromdate
            fragmentApplyleaveBinding.llMarkoutattendance.tvSelectedfromdate.text = selecteddate
            fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text = selecteddate2
        } else {
            leavefromdate = ""

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val sdf = SimpleDateFormat("d")
            val sdf2 = SimpleDateFormat("MMM yyyy E")
            val currentDate = sdf.format(Date())
            val currentDate2 = sdf2.format(Date())
            val currentDate3 = dateFormat.format(Date())
            date1 = Date()
            date3 = Date()
            selectedfromdate = currentDate3
            fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text = currentDate2
            fragmentApplyleaveBinding.llMarkoutattendance.tvSelectedfromdate.text = currentDate

        }


        if (bundle != null) {

            shortcode = bundle.getString("shortcode").toString()

        } else {

            shortcode = ""

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
            myCalendartodate.time = date
            selectedtodate = leavetodate
            fragmentApplyleaveBinding.llMarkoutattendance.tvselectedTodate.text = selecteddate?: ""
            fragmentApplyleaveBinding.llMarkoutattendance.tvTodate.text = selecteddate2?: ""


        } else {
            leavetodate = ""
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val sdf = SimpleDateFormat("d")
            val sdf2 = SimpleDateFormat("MMM yyyy E")
            val currentDate = sdf.format(Date())
            val currentDate2 = sdf2.format(Date())
            val currentDate3 = dateFormat.format(Date())

            selectedtodate = currentDate3
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


        if (bundle != null) {
            attachment = bundle.getString("attachment").toString()
            Glide.with(mainActivity)
                .load(attachment)
                .centerCrop()
                .error(R.drawable.selecteddoc)
                .into(fragmentApplyleaveBinding.llMarkoutattendance.imgDoc)

            var bitmap: Bitmap? = null
            try {
                // Download Image from URL
                val input: InputStream = URL(attachment).openStream()
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input)

                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)



            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

//            finally {
//
//                val path: String = MediaStore.Images.Media.insertImage(mainActivity.getContentResolver(), bitmap, "Title", "image")
//                val fileUri = Uri.parse(path)
//                Log.d(TAG, "fileuri-->"+fileUri)
//                pathFromUri = GetRealPathFromUri.getPathFromUri(mainActivity, fileUri!!)!!
//            }

        } else {
            attachment = ""

        }


        fragmentApplyleaveBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentApplyleaveBinding.llMarkoutattendance.btnApplyleave.setOnClickListener {

            if (selectedfromdate.equals("")) {
                Toast.makeText(mainActivity, "Select From date", Toast.LENGTH_SHORT).show()
            } else if (selectedtodate.equals("")) {
                Toast.makeText(mainActivity, "Select To date", Toast.LENGTH_SHORT).show()
            } else if (selectedleavetypeid == null) {
                Toast.makeText(mainActivity, "Select Leave Type", Toast.LENGTH_SHORT).show()
            } else {

                if (pathFromUri.equals("")) {

                    applyleavewithoutdoc(it)

                } else {
                    applyleave(pathFromUri.toString(), it)

                }
            }


        }


        val todate =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendartodate[Calendar.YEAR] = year
                myCalendartodate[Calendar.MONTH] = monthOfYear
                myCalendartodate[Calendar.DAY_OF_MONTH] = dayOfMonth
                todateupdateLabel(myCalendartodate)
            }


        val fromdate =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendarfromdate[Calendar.YEAR] = year
                myCalendarfromdate[Calendar.MONTH] = monthOfYear
                myCalendarfromdate[Calendar.DAY_OF_MONTH] = dayOfMonth
                fromdateupdateLabel()
                todateupdateLabel(myCalendarfromdate)
            }


        fragmentApplyleaveBinding.llMarkoutattendance.btnFromdate.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            //calendar.add(Calendar.DATE, 0)
            val datePickerDialog = DatePickerDialog(
                mainActivity, fromdate, myCalendarfromdate
                    .get(Calendar.YEAR), myCalendarfromdate.get(Calendar.MONTH),
                myCalendarfromdate.get(Calendar.DAY_OF_MONTH)
            )
            //myCalendarfromdate=calendar
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()

        }


        fragmentApplyleaveBinding.llMarkoutattendance.btnTodate.setOnClickListener {

            if (fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text.isNotEmpty()) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, -1)
                val datePickerDialog = DatePickerDialog(
                    mainActivity, todate, myCalendartodate
                        .get(Calendar.YEAR), myCalendartodate.get(Calendar.MONTH),
                    myCalendartodate.get(Calendar.DAY_OF_MONTH)
                )
                //myCalendarfromdate=calendar
                datePickerDialog.datePicker.minDate = myCalendarfromdate.timeInMillis
                datePickerDialog.show()
            } else {
                Toast.makeText(mainActivity, "Please Select from date first!", Toast.LENGTH_SHORT)
                    .show()
            }

        }


        fragmentApplyleaveBinding.llMarkoutattendance.imgDoc.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ImagePicker.Companion.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
            }else{

                val permissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                Permissions.check(
                    mainActivity /*context*/,
                    permissions,
                    null /*rationale*/,
                    null /*options*/,
                    object : PermissionHandler() {
                        override fun onGranted() {
                            ImagePicker.Companion.with(this@ApplyleaveFragment)
                                .crop()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start()
                        }


                        override fun onDenied(
                            context: Context?,
                            deniedPermissions: ArrayList<String?>?
                        ) {
                            Toast.makeText(mainActivity, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();

                        }
                    })

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

                Glide.with(mainActivity)
                    .load(fileUri)
                    .into(fragmentApplyleaveBinding.llMarkoutattendance.imgDoc)

//                fragmentApplyleaveBinding.llMarkoutattendance.imgDoc.setImageURI(fileUri)
                pathFromUri = GetRealPathFromUri.getPathFromUri(mainActivity, fileUri!!)!!

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(mainActivity, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mainActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun leaveType() {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

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

                                    leaveTypeModel.isShortCodeSelected = i?.short_code == shortcode
                                    if (i?.short_code == shortcode){
                                        selectedleavetypeid=i?.id?.toInt()
                                    }
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

        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
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
        date1 = simpleDateFormat.parse(sdf3.format(myCalendarfromdate.time))

        // date1 = simpleDateFormat.parse(sdf3.format(fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.text.toString()))
        selectedfromdate = sdf4.format(myCalendarfromdate.time)
        fragmentApplyleaveBinding.llMarkoutattendance.tvFromdate.setText(
            sdf.format(
                myCalendarfromdate.time
            )
        )
        fragmentApplyleaveBinding.llMarkoutattendance.tvSelectedfromdate.setText(
            sdf2.format(
                myCalendarfromdate.time
            )
        )

    }

    private fun todateupdateLabel(calendar: Calendar) {
        val dateFormat = "d" //In which you need put here
        val myFormat = "MMM yyyy E" //In which you need put here
        val format = "dd/M/yyyy"
        val selecteddateformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val sdf2 = SimpleDateFormat(dateFormat, Locale.US)
        val sdf3 = SimpleDateFormat(format, Locale.US)
        val sdf4 = SimpleDateFormat(selecteddateformat, Locale.US)
        date3 = simpleDateFormat.parse(sdf3.format(calendar.time))
        selectedtodate = sdf4.format(calendar.time)
        printDifference(date1!!, date3!!)
        fragmentApplyleaveBinding.llMarkoutattendance.tvTodate.setText(sdf.format(calendar.time))
        fragmentApplyleaveBinding.llMarkoutattendance.tvselectedTodate.setText(
            sdf2.format(calendar.time)
        )

    }

    fun selectedleavetype(leaveTypeModel: LeaveTypeModel) {

        selectedleavetypeid = (leaveTypeModel.id)?.toInt()
    }

    private fun applyleave(pathFromUri: String, view: View) {


        if (CheckConnectivity.getInstance(mainActivity).isOnline) {


            val file = File(pathFromUri)

            var id: String? = ""
            if (leaveid?.length!! > 0) {
                id = leaveid
            } else {
                id = "0"
            }

            var comments:String?=""
            if (etComment.text.toString().length==0){
                comments = ""
            }else{
                comments = etComment.text.toString()
            }


            val fileReqBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            val part: MultipartBody.Part =
                MultipartBody.Part.createFormData("attachment", file.name, fileReqBody)
            val leave_type_id: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedleavetypeid.toString())
            val leave_date_from: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedfromdate!!)
            val leave_date_to: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedtodate!!)
            val commentvalue: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), comments)

            leaveTypeViewModel.leaveapply(
                authtoken = "Bearer " + sessionManager?.getToken(),
                leave_type_id,
                id = id!!,
                leave_date_from,
                leave_date_to,
                commentvalue,
                part = part
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            Toast.makeText(mainActivity, resource.data?.message, Toast.LENGTH_SHORT)
                                .show()
                            val navController = Navigation.findNavController(view)
                            navController.popBackStack(R.id.nav_leavefragment, false)
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

    private fun applyleavewithoutdoc(view: View) {


        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            var id: String? = ""
            if (leaveid?.length!! > 0) {
                id = leaveid
            } else {
                id = "0"
            }

            var comments:String?=""
            if (fragmentApplyleaveBinding.llMarkoutattendance.etComment.text.toString().length==0){
                comments = ""
            }else{
                comments = fragmentApplyleaveBinding.llMarkoutattendance.etComment.text.toString()
            }

            val leave_type_id: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedleavetypeid.toString())
            val leave_date_from: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedfromdate!!)
            val leave_date_to: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), selectedtodate!!)
            val commentvalue: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), comments)

            leaveTypeViewModel.leaveapplywithoutdoc(
                authtoken = "Bearer " + sessionManager?.getToken(),
                leave_type_id,
                id = id!!,
                leave_date_from,
                leave_date_to,
                commentvalue
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            Toast.makeText(mainActivity, resource.data?.message, Toast.LENGTH_SHORT)
                                .show()
                            val navController = Navigation.findNavController(view)
                            navController.popBackStack(R.id.nav_leavefragment, false)

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
        val elapsedDays = (different / daysInMilli).inc()
        Log.d(TAG, "days-->" + elapsedDays)
        fragmentApplyleaveBinding.llMarkoutattendance.tvTotaldays.text = "$elapsedDays Days"
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