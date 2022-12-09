package com.app.hcsassist.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.adapter.LeaveAdapter
import com.app.hcsassist.adapter.MssAttendaceAdapter
import com.app.hcsassist.adapter.ShiftChangeListAdapter
import com.app.hcsassist.apimodel.LeaveApprovalRequest
import com.app.hcsassist.apimodel.RequestedLeaveModel
import com.app.hcsassist.apimodel.ShiftApprovalRequest
import com.app.hcsassist.apimodel.myattendance.MssAttendanceRequest
import com.app.hcsassist.databinding.FragmentMssBinding
import com.app.hcsassist.model.ShiftChangeListModel
import com.app.hcsassist.modelfactory.MssAttendanceModelFactory
import com.app.hcsassist.modelfactory.RequestleavelistModelFactory
import com.app.hcsassist.modelfactory.ShiftChangeListModelFactory
import com.app.hcsassist.modelfactory.ShiftchangeApprovalModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.MssAttendanceListViewModel
import com.app.hcsassist.viewmodel.RequestedLeavelistViewModel
import com.app.hcsassist.viewmodel.ShiftChangeListViewModel
import com.app.hcsassist.viewmodel.ShiftchangeApprovalViewModel
import com.bumptech.glide.Glide
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class MssFragment : Fragment() {

    lateinit var fragmentMssBinding: FragmentMssBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    lateinit var shiftChangeListAdapter: ShiftChangeListAdapter
    lateinit var leaveAdapter: LeaveAdapter
    lateinit var attendanceAdapter: MssAttendaceAdapter
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var shiftChangeList: ArrayList<ShiftChangeListModel> = ArrayList()
    private var leavelist: ArrayList<RequestedLeaveModel> = ArrayList()
    lateinit var shiftChangeListViewModel: ShiftChangeListViewModel
    lateinit var shiftchangeApprovalViewModel: ShiftchangeApprovalViewModel
    lateinit var requestedLeavelistViewModel: RequestedLeavelistViewModel
    lateinit var mssAttendancelistViewModel: MssAttendanceListViewModel
    var myCalendarfromdate = Calendar.getInstance()


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
        val mssAttendanceViewModel: MssAttendanceListViewModel by viewModels {
            MssAttendanceModelFactory(ApiHelper(ApiClient.apiService))
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
        mssAttendancelistViewModel = mssAttendanceViewModel


        fragmentMssBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressedDispatcher.onBackPressed()
        }
        val c: Calendar = Calendar.getInstance()

        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 0)
        mssAttendanceList(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time))

        fragmentMssBinding.tvAttendance.setOnClickListener {

            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, 0)
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
            mssAttendanceList(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time))

        }

        with(fragmentMssBinding.includeAttendance) {

            val sdf = SimpleDateFormat("dd MMM yyyy E")
            val currentDate = sdf.format(Date())
            calenderTXT.text = currentDate

//            calenderTXT.text = SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)

            ivNext.setOnClickListener {

                //val next_month: Int = c.get(Calendar.MONTH) + 1
                c.add(Calendar.MONTH, 1)
                calenderTXT.text =
                    SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)
                mssAttendanceList(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(c.time)
                )
            }
            ivPrev.setOnClickListener {

                // val prev_month: Int = c.get(Calendar.MONTH) - 1
                c.add(Calendar.MONTH, -1)
                calenderTXT.text =
                    SimpleDateFormat("MMMM", Locale.getDefault()).format(c.time)
                mssAttendanceList(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(c.time)
                )
            }

        }


        val fromdate =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendarfromdate[Calendar.YEAR] = year
                myCalendarfromdate[Calendar.MONTH] = monthOfYear
                myCalendarfromdate[Calendar.DAY_OF_MONTH] = dayOfMonth
                fromdateupdateLabel()
            }

        fragmentMssBinding.includeAttendance.rlcalender.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 0)
            val datePickerDialog = DatePickerDialog(
                mainActivity, fromdate, myCalendarfromdate
                    .get(Calendar.YEAR), myCalendarfromdate.get(Calendar.MONTH),
                myCalendarfromdate.get(Calendar.DAY_OF_MONTH)
            )
            //myCalendarfromdate=calendar
//            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        fragmentMssBinding.tvLeave.setOnClickListener {

            fragmentMssBinding.tvAttendance.setTextColor(
                resources.getColor(
                    R.color.diselectedtextcolor,
                    resources.newTheme()
                )
            )
            fragmentMssBinding.tvLeave.setTextColor(
                resources.getColor(
                    R.color.selectedtextcolor,
                    resources.newTheme()
                )
            )
            fragmentMssBinding.tvShiftchange.setTextColor(
                resources.getColor(
                    R.color.diselectedtextcolor,
                    resources.newTheme()
                )
            )
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
            if (b) {
                leavelist.forEach {
                    it.isChecked = true
                }
                fragmentMssBinding.llAccept.visibility = View.VISIBLE
            } else {
                leavelist.forEach {
                    it.isChecked = false
                }
                fragmentMssBinding.llAccept.visibility = View.GONE
            }
            leaveAdapter.updateData(leavelist)
        }


        fragmentMssBinding.includeShiftchange.cbCheckAll.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                shiftChangeList.forEach {
                    it.isChecked = true
                }
                fragmentMssBinding.llAccept.visibility = View.VISIBLE
            } else {
                shiftChangeList.forEach {
                    it.isChecked = false
                }
                fragmentMssBinding.llAccept.visibility = View.GONE
            }
            shiftChangeListAdapter.updateData(shiftChangeList)
        }


        fragmentMssBinding.btnAcceptList.setOnClickListener {


            val builder = AlertDialog.Builder(mainActivity)
            if (fragmentMssBinding.includeLeave.llLeave.isVisible) {
                builder.setMessage("Do you really want to approved leave?")
            } else {
                builder.setMessage("Do you really want to approved shift?")
            }
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->
                if (fragmentMssBinding.includeLeave.llLeave.isVisible) {

                    val list = ArrayList<String>()
                    leaveAdapter.getList().forEach {
                        if (it.isChecked == true)
                            list.add(it.id!!)
                    }
                    val listString: String = TextUtils.join(", ", list)

                    acceptleave(listString)
                } else {

                    val list = ArrayList<String>()
                    shiftChangeListAdapter.getList().forEach {
                        if (it.isChecked == true)
                            list.add(it.id!!)
                    }
                    val listString: String = TextUtils.join(", ", list)

                    acceptshiftchange(listString)
                }
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
            if (fragmentMssBinding.includeLeave.llLeave.isVisible) {
                builder.setMessage("Do you really want to reject leave?")
            } else {
                builder.setMessage("Do you really want to reject shift?")
            }
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->
                dialog.dismiss()
                val dialoglayout = layoutInflater.inflate(R.layout.reject_leave_dialog, null)
                val edittext = dialoglayout.findViewById<EditText>(R.id.etComment)

                val builder1 = AlertDialog.Builder(mainActivity)
                builder1.setView(dialoglayout)
                builder1.setPositiveButton("Submit") { dialog1, which1 ->

                    if (fragmentMssBinding.includeLeave.llLeave.isVisible) {
                        val list = ArrayList<String>()
                        leaveAdapter.getList().forEach {
                            if (it.isChecked == true)
                                list.add(it.id!!)
                        }
                        val listString: String = TextUtils.join(", ", list)

                        rejectleave(listString, edittext.text.toString())
                    } /*else {

                        val list = ArrayList<String>()
                        shiftChangeListAdapter.getList().forEach {
                            if (it.isChecked == true)
                                list.add(it.id!!)
                        }
                        val listString: String = TextUtils.join(", ", list)

                        rejectshiftchange(listString)
                    }*/

                }
                val alert1 = builder1.create()

                if (fragmentMssBinding.includeLeave.llLeave.isVisible) {
                    alert1.show()
                } else {
                    val list = ArrayList<String>()
                    shiftChangeListAdapter.getList().forEach {
                        if (it.isChecked == true)
                            list.add(it.id!!)
                    }
                    val listString: String = TextUtils.join(", ", list)

                    rejectshiftchange(listString)
                }

                // (mFragment as MssFragment).rejectleave(requestedleaveModelArrayList[position].id!!)
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which ->

                dialog.cancel()
            }

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
            //fragmentMssBinding.llAccept.visibility = View.GONE
            fragmentMssBinding.includeShiftchange.llShiftchange.visibility = View.VISIBLE
            shiftchangelist()

        }

        return root
    }


    private fun fromdateupdateLabel() {
        val dateFormat3 = SimpleDateFormat("dd MMM yyyy E")
        val selecteddate2 = dateFormat3.format(myCalendarfromdate.time)
        fragmentMssBinding.includeAttendance.calenderTXT.text = selecteddate2

        val selecteddate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(myCalendarfromdate.time)
        Log.d(TAG, "selecteddate-->" + selecteddate)

        mssAttendanceList(selecteddate)

    }


    fun showMultiSelect(isVisible: Boolean) {
        fragmentMssBinding.llAccept.visibility = if (isVisible) View.VISIBLE else View.GONE

    }

    private fun shiftchangelist() {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            showMultiSelect(false)
            fragmentMssBinding.includeShiftchange.cbCheckAll.isChecked = false

            shiftChangeListViewModel.shiftchangelist(authtoken = "Bearer " + sessionManager?.getToken())
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                shiftChangeList = ArrayList<ShiftChangeListModel>()
                                for (i in it.data?.result!!) {
                                    val shiftChangeListModel = ShiftChangeListModel()
                                    shiftChangeListModel.name = i?.user?.name + " " + i?.user?.last_name
                                    shiftChangeListModel.shift_title = i?.shift?.shift_title
                                    shiftChangeListModel.full_profile_image = i?.user?.full_profile_image
                                    shiftChangeListModel.current_shift = i?.currentShift?.shift_title
                                    shiftChangeListModel.comment = i?.comment
                                    shiftChangeListModel.id = i?.id
                                    shiftChangeList.add(shiftChangeListModel)
                                }
                                shiftChangeListAdapter =
                                    ShiftChangeListAdapter(mainActivity, this)
                                shiftChangeListAdapter.updateData(shiftChangeList)
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

                                if (shiftChangeList.size > 0) {
                                    fragmentMssBinding.includeShiftchange.cbCheckAll.visibility =
                                        View.VISIBLE
                                } else {
                                    fragmentMssBinding.includeShiftchange.cbCheckAll.visibility =
                                        View.GONE

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

        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }

    private fun leavelist() {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            showMultiSelect(false)
            fragmentMssBinding.includeLeave.cbCheckAll.isChecked = false

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
                                    RequestedLeaveModel.name =
                                        i?.data?.name + " " + i?.data?.last_name
                                    RequestedLeaveModel.leave_date_from = i?.leave_date_from
                                    RequestedLeaveModel.leave_date_to = i?.leave_date_to
                                    RequestedLeaveModel.image = i?.data?.full_profile_image
                                    RequestedLeaveModel.comment = i?.comment
                                    RequestedLeaveModel.comment_cancel_reject =
                                        i?.comment_cancel_reject
                                    RequestedLeaveModel.attachment = i?.attachment
                                    RequestedLeaveModel.id = i?.id
                                    leavelist.add(RequestedLeaveModel)
                                }
                                leaveAdapter.updateData(leavelist)

                                if (leavelist.size > 0) {
                                    fragmentMssBinding.includeLeave.cbCheckAll.visibility =
                                        View.VISIBLE
                                } else {
                                    fragmentMssBinding.includeLeave.cbCheckAll.visibility =
                                        View.GONE

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

        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    private fun mssAttendanceList(date: String) {


        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            mssAttendancelistViewModel.getMSSAttendanceList(
                authtoken = "Bearer " + sessionManager?.getToken(),
                MssAttendanceRequest(date)
            )
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                leavelist = ArrayList<RequestedLeaveModel>()
                                attendanceAdapter = MssAttendaceAdapter(mainActivity, this)
                                fragmentMssBinding.includeAttendance.rvAttendance.layoutManager =
                                    LinearLayoutManager(
                                        mainActivity,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                fragmentMssBinding.includeAttendance.rvAttendance.adapter =
                                    attendanceAdapter
                                /* for (i in it.data?.data!!) {
                                     val RequestedLeaveModel = Data()
                                     RequestedLeaveModel.name = i?.name + " " + i?.data?.last_name
                                     RequestedLeaveModel.leave_date_from = i?.leave_date_from
                                     RequestedLeaveModel.leave_date_to = i?.leave_date_to
                                     RequestedLeaveModel.image = i?.data?.full_profile_image
                                     RequestedLeaveModel.id = i?.id
                                     leavelist.add(RequestedLeaveModel)
                                 }*/
                                attendanceAdapter.updateData(it.data?.data)

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


        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    fun acceptshiftchange(requestedShiftId: String) {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            shiftchangeApprovalViewModel.shiftchangeapproval(
                authtoken = "Bearer " + sessionManager?.getToken(),
                ShiftApprovalRequest(request_id = requestedShiftId, status = "2")
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (fragmentMssBinding.llAccept.isVisible)
                                fragmentMssBinding.llAccept.visibility = View.GONE
                            fragmentMssBinding.includeShiftchange.cbCheckAll.isChecked = false
                            shiftchangelist()
                            Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT)
                                .show()

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

        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    fun rejectshiftchange(requestedShiftId: String) {

        shiftchangeApprovalViewModel.shiftchangeapproval(
            authtoken = "Bearer " + sessionManager?.getToken(),
            ShiftApprovalRequest(request_id = requestedShiftId, status = "3")
        ).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (fragmentMssBinding.llAccept.isVisible)
                            fragmentMssBinding.llAccept.visibility = View.GONE

                        fragmentMssBinding.includeShiftchange.cbCheckAll.isChecked = false
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


    fun acceptleave(requestedLeaveId: String) {


        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            requestedLeavelistViewModel.approveleave(
                authtoken = "Bearer " + sessionManager?.getToken(),
                LeaveApprovalRequest(request_id = requestedLeaveId, status = "2")
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            leavelist()
                            if (fragmentMssBinding.llAccept.isVisible)
                                fragmentMssBinding.llAccept.visibility = View.GONE

                            fragmentMssBinding.includeLeave.cbCheckAll.isChecked = false
                            Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT)
                                .show()

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

        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    fun rejectleave(requestedLeaveId: String, comment: String = "") {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            requestedLeavelistViewModel.approveleave(
                authtoken = "Bearer " + sessionManager?.getToken(),
                LeaveApprovalRequest(
                    request_id = requestedLeaveId,
                    status = "3",
                    commentCancelReject = comment
                )
            ).observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            leavelist()
                            Toast.makeText(mainActivity, resource.message, Toast.LENGTH_SHORT)
                                .show()

                            if (fragmentMssBinding.llAccept.isVisible)
                                fragmentMssBinding.llAccept.visibility = View.GONE

                            fragmentMssBinding.includeLeave.cbCheckAll.isChecked = false
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

        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    fun leaveDetailspopup(requestedLeaveModel: RequestedLeaveModel) {

        val btnPopupclose: TextView
        val tvComment: TextView
        val tvdoc: TextView
        val lldoc: LinearLayout
        val imgDoc: ImageView
        val dialog = Dialog(mainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val params = WindowManager.LayoutParams()
        dialog.setContentView(R.layout.layout_leavecomment)
        params.copyFrom(dialog.getWindow()?.getAttributes())
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.CENTER
        dialog.getWindow()?.setAttributes(params)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        btnPopupclose = dialog.findViewById(R.id.btnPopupclose)
        tvComment = dialog.findViewById(R.id.tvComment)
        imgDoc = dialog.findViewById(R.id.imgDoc)
        lldoc = dialog.findViewById(R.id.lldoc)
        tvdoc = dialog.findViewById(R.id.tvdoc)

        if (requestedLeaveModel.attachment.equals("")) {
            lldoc.visibility = View.GONE
            tvdoc.visibility = View.GONE

        } else {
            lldoc.visibility = View.VISIBLE
            tvdoc.visibility = View.VISIBLE
            Glide.with(mainActivity)
                .load(requestedLeaveModel.attachment)
                .into(imgDoc)
        }

        tvComment.text = requestedLeaveModel.comment


        btnPopupclose.setOnClickListener {

            dialog.dismiss()
        }


    }

    fun shiftChangereasonpopup(shiftChangeListModel: ShiftChangeListModel){


        val btnPopupclose: TextView
        val tvReason: TextView
        val dialog = Dialog(mainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val params = WindowManager.LayoutParams()
        dialog.setContentView(R.layout.layout_shiftreason)
        params.copyFrom(dialog.getWindow()?.getAttributes())
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.CENTER
        dialog.getWindow()?.setAttributes(params)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        btnPopupclose = dialog.findViewById(R.id.btnPopupclose)
        tvReason = dialog.findViewById(R.id.tvReason)

        tvReason.text = shiftChangeListModel.comment


        btnPopupclose.setOnClickListener {

            dialog.dismiss()
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