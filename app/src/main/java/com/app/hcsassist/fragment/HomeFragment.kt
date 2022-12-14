package com.app.hcsassist.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.app.hcsassist.Changepassword
import com.app.hcsassist.Login
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.LogoutRequest
import com.app.hcsassist.apimodel.SelfShiftChangeRequest
import com.app.hcsassist.databinding.FragmentHomeBinding
import com.app.hcsassist.model.ShiftlistModel
import com.app.hcsassist.modelfactory.LogoutModelFactory
import com.app.hcsassist.modelfactory.PicuploadModelFactory
import com.app.hcsassist.modelfactory.ShiftListModelFactory
import com.app.hcsassist.modelfactory.UserdetailsModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.LogoutViewModel
import com.app.hcsassist.viewmodel.PicuploadViewModel
import com.app.hcsassist.viewmodel.ShiftListViewModel
import com.app.hcsassist.viewmodel.UserdetailsViewModel
import com.bumptech.glide.Glide
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: UserdetailsViewModel
    private lateinit var picuploadviewModel: PicuploadViewModel
    private lateinit var logoutViewModel: LogoutViewModel
    private lateinit var shiftlistviewmodel: ShiftListViewModel
    var shiftlistModelArrayList: ArrayList<ShiftlistModel> = ArrayList<ShiftlistModel>()
    var selectedshiftname: String = ""
    val shiftname = ArrayList<String>()
    val shiftname1 = ArrayList<String>()
    var shiftid: String? = ""
    var phonenumber: String = ""
    var REQUEST_CODE = 101
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationManager: LocationManager? = null
    var placesClient: PlacesClient? = null
    var latitude: String? = null
    var longitude: String? = null
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 10000
    var punchoutshiftlist = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val root = fragmentHomeBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        locationManager =
            mainActivity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)

        val vm: UserdetailsViewModel by viewModels {
            UserdetailsModelFactory(ApiHelper(ApiClient.apiService))
        }

        val shiftlistvm: ShiftListViewModel by viewModels {
            ShiftListModelFactory(ApiHelper(ApiClient.apiService))
        }

        val piciuploadvm: PicuploadViewModel by viewModels {
            PicuploadModelFactory(ApiHelper(ApiClient.apiService))
        }

        val logoutdvm: LogoutViewModel by viewModels {
            LogoutModelFactory(ApiHelper(ApiClient.apiService))
        }

        viewModel = vm
        picuploadviewModel = piciuploadvm
        logoutViewModel = logoutdvm
        shiftlistviewmodel = shiftlistvm
//        if (!Places.isInitialized()) {
//            Places.initialize(mainActivity, getString(R.string.api_key))
//        }
//        placesClient = Places.createClient(mainActivity)
        locationManager =
            mainActivity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        if (!locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            || !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || !locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
        ) {

            OnGPS()

        } else {
            getLocation()
        }



        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        getDetails()


    }

    private fun init() {

        viewModel.location.observeForever(Observer {

//            Log.d(TAG, "latlong-->"+latitude + " , "+ longitude)
//            Log.d(TAG, "latlong response-->"+sessionManager?.getfencingLat()!!.toDouble() + " , "+ sessionManager?.getfencingLong()!!.toDouble())
            if (sessionManager?.getfencingLat()!!.isNotEmpty() && sessionManager?.getfencingLong()!!
                    .isNotEmpty()
            ) {

                if (distanceinmeter(
                        sessionManager?.getfencingLat()!!.toDouble(),
                        sessionManager?.getfencingLong()!!.toDouble(),
                        latitude?.toDouble()!!,
                        longitude?.toDouble()!!
                    ) < 100
                ) {

                    fragmentHomeBinding.btMarkattendance.isClickable = true
                    fragmentHomeBinding.btMarkattendance.isEnabled = true
                    fragmentHomeBinding.btMarkattendance.background =
                        mainActivity.resources.getDrawable(
                            R.drawable.button_bg,
                            mainActivity.resources.newTheme()
                        )

                } else {

                    fragmentHomeBinding.btMarkattendance.isClickable = false
                    fragmentHomeBinding.btMarkattendance.isEnabled = false
                    fragmentHomeBinding.btMarkattendance.background =
                        mainActivity.resources.getDrawable(
                            R.drawable.button_bg2,
                            mainActivity.resources.newTheme()
                        )

                }

                if (sessionManager?.getPunchin().equals("punchin")) {

                    fragmentHomeBinding.btMarkattendanceout.visibility = View.VISIBLE
                    fragmentHomeBinding.btMarkattendance.visibility = View.GONE
                    fragmentHomeBinding.imgOnOff.setColorFilter(
                        ContextCompat.getColor(mainActivity, R.color.successtextcolor),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                    fragmentHomeBinding.tvPunchinOut.text = "Punch In"

                    if (distanceinmeter(
                            sessionManager?.getfencingLat()!!.toDouble(),
                            sessionManager?.getfencingLong()!!.toDouble(),
                            latitude?.toDouble()!!,
                            longitude?.toDouble()!!
                        ) < 100
                    ) {

                        fragmentHomeBinding.btMarkattendance.isClickable = true
                        fragmentHomeBinding.btMarkattendance.isEnabled = true
                        fragmentHomeBinding.btMarkattendance.background =
                            mainActivity.resources.getDrawable(
                                R.drawable.button_bg,
                                mainActivity.resources.newTheme()
                            )


                    } else {

                        fragmentHomeBinding.btMarkattendance.isClickable = false
                        fragmentHomeBinding.btMarkattendance.isEnabled = false
                        fragmentHomeBinding.btMarkattendance.background =
                            mainActivity.resources.getDrawable(
                                R.drawable.button_bg2,
                                mainActivity.resources.newTheme()
                            )

                    }

                } else {

                    fragmentHomeBinding.btMarkattendanceout.visibility = View.GONE
                    fragmentHomeBinding.btMarkattendance.visibility = View.VISIBLE
                    fragmentHomeBinding.imgOnOff.setColorFilter(
                        ContextCompat.getColor(mainActivity, R.color.red),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                    fragmentHomeBinding.tvPunchinOut.text = "Punch Out"

                    if (distanceinmeter(
                            sessionManager?.getfencingLat()!!.toDouble(),
                            sessionManager?.getfencingLong()!!.toDouble(),
                            latitude?.toDouble()!!,
                            longitude?.toDouble()!!
                        ) < 100
                    ) {

                        fragmentHomeBinding.btMarkattendance.isClickable = true
                        fragmentHomeBinding.btMarkattendance.isEnabled = true
                        fragmentHomeBinding.btMarkattendance.background =
                            mainActivity.resources.getDrawable(
                                R.drawable.button_bg,
                                mainActivity.resources.newTheme()
                            )

                    } else {

                        fragmentHomeBinding.btMarkattendance.isClickable = false
                        fragmentHomeBinding.btMarkattendance.isEnabled = false
                        fragmentHomeBinding.btMarkattendance.background =
                            mainActivity.resources.getDrawable(
                                R.drawable.button_bg2,
                                mainActivity.resources.newTheme()
                            )

                    }
                }

            }

        })




        if (sessionManager?.getuserid().equals("3")) {

            fragmentHomeBinding.btnShiftchange.visibility = View.VISIBLE
            fragmentHomeBinding.btnAttendance.visibility = View.VISIBLE
            fragmentHomeBinding.btnEvent.visibility = View.VISIBLE
            fragmentHomeBinding.btnAdminhr.visibility = View.GONE
            fragmentHomeBinding.btnMss.visibility = View.GONE
        } else if (sessionManager?.getuserid().equals("1")) {

            fragmentHomeBinding.btnShiftchange.visibility = View.VISIBLE
            fragmentHomeBinding.btnAttendance.visibility = View.VISIBLE
            fragmentHomeBinding.btnEvent.visibility = View.VISIBLE
            fragmentHomeBinding.btnAdminhr.visibility = View.VISIBLE
            fragmentHomeBinding.btnMss.visibility = View.VISIBLE

        } else if (sessionManager?.getuserid().equals("2")) {

            fragmentHomeBinding.btnShiftchange.visibility = View.VISIBLE
            fragmentHomeBinding.btnAttendance.visibility = View.VISIBLE
            fragmentHomeBinding.btnEvent.visibility = View.VISIBLE
            fragmentHomeBinding.btnAdminhr.visibility = View.GONE
            fragmentHomeBinding.btnMss.visibility = View.VISIBLE

        }else if (sessionManager?.getuserid().equals("4")){

            fragmentHomeBinding.btnShiftchange.visibility = View.VISIBLE
            fragmentHomeBinding.btnAttendance.visibility = View.VISIBLE
            fragmentHomeBinding.btnEvent.visibility = View.VISIBLE
            fragmentHomeBinding.btnAdminhr.visibility = View.GONE
            fragmentHomeBinding.btnMss.visibility = View.VISIBLE

        }



        fragmentHomeBinding.btMarkattendance.setOnClickListener {

            if (sessionManager?.getisHoliday().equals("1")) {

                val builder = AlertDialog.Builder(mainActivity)
                builder.setMessage("Today you are on Holiday!")
                builder.setPositiveButton(
                    "Ok"
                ) { dialog, which ->
                    dialog.cancel()

                }
                val alert = builder.create()
                alert.show()

            } else if (sessionManager?.getisonLeave().equals("1")) {

                val builder = AlertDialog.Builder(mainActivity)
                builder.setMessage("Today you are on Leave!")
                builder.setPositiveButton(
                    "Ok"
                ) { dialog, which ->
                    dialog.cancel()

                }
                val alert = builder.create()
                alert.show()


            } else {

                if (fragmentHomeBinding.llDetails.spShift.selectedItemPosition == 0) {
                    Toast.makeText(
                        mainActivity, "Please select shift for attendance!", Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                punchoutshiftlist.forEach { itString ->
                    if (selectedshiftname == itString) {
                        Toast.makeText(
                            mainActivity,
                            "Today you have already given attendance for this shift",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                }
                val bundle = Bundle()
                bundle.putString("shiftid", shiftid)
                Log.d(TAG, "shiftid-->" + shiftid)
                val navController = Navigation.findNavController(it)
                navController.navigate(R.id.nav_markattendance, bundle)
            }

        }

        fragmentHomeBinding.llDetails.btnViewprofile.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_profdetails)
        }

        fragmentHomeBinding.btMarkattendanceout.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_markoutattendance)
        }

        fragmentHomeBinding.btnAttendance.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_attendancefragment)
        }

//        fragmentHomeBinding.btnMessage.setOnClickListener {
//
//            val navController = Navigation.findNavController(it)
//            navController.navigate(R.id.nav_chatlistfragment)
//        }

        fragmentHomeBinding.btnEvent.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_calendar)
        }

        fragmentHomeBinding.btnAppmanual.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_appmanualfragment)
        }

        fragmentHomeBinding.llDetails.tvReporterhead.setOnClickListener {

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phonenumber")
            startActivity(callIntent)
        }


        fragmentHomeBinding.btnFeedback.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_feedback)

        }


        fragmentHomeBinding.btnLeave.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_leavefragment)

        }


        fragmentHomeBinding.btnAdminhr.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_adminhr)
        }


        fragmentHomeBinding.btnMss.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_mss)
        }


        fragmentHomeBinding.btnChangepassword.setOnClickListener {

            val intent = Intent(mainActivity, Changepassword::class.java)
            startActivity(intent)
        }

        fragmentHomeBinding.btnShiftchange.setOnClickListener {

//            val navController = Navigation.findNavController(it)
//            navController.navigate(R.id.nav_shiftchange)

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_changeshiftlist)

        }


        fragmentHomeBinding.btnLogout.setOnClickListener {


            val builder = AlertDialog.Builder(mainActivity)
            builder.setMessage("Do you really want to logout?")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->

                logout()

            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }

            val alert = builder.create()
            alert.show()

        }


        fragmentHomeBinding.llDetails.PrfImg.setOnClickListener {

            ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }
    }


    private fun spShifttime() {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {
            shiftlistviewmodel.shiftlist(authtoken = "Bearer " + sessionManager?.getToken())
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
//                                hideProgressDialog()
                                if (resource.data?.status == true) {
                                    shiftname.clear()
                                    shiftname1.clear()
                                    shiftlistModelArrayList.clear()
                                    shiftname.add("Select Shift")
                                    shiftname1.add("Select Shift")
                                    shiftlistModelArrayList = ArrayList<ShiftlistModel>()
                                    for (i in it.data?.result!!) {
                                        shiftname.add("${i?.shift_title} - (${i?.start_time}-${i?.end_time})")
                                        shiftname1.add(i?.shift_title!!)
                                        val ShiftlistModel =
                                            ShiftlistModel(i?.id!!, i.shift_title!!)
                                        shiftlistModelArrayList.add(ShiftlistModel)
                                    }

                                    val spinnerArrayAdapter: ArrayAdapter<String> =
                                        ArrayAdapter<String>(
                                            requireContext(),
                                            android.R.layout.simple_spinner_dropdown_item,
                                            shiftname
                                        )
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    fragmentHomeBinding.llDetails.spShift.setAdapter(
                                        spinnerArrayAdapter
                                    )

                                    for (i in 0 until shiftname1.size) {

                                        if (sessionManager?.getdefultShift() == shiftname1[i]) {
                                            fragmentHomeBinding.llDetails.spShift.setSelection(
                                                i,
                                                true
                                            )
                                            shiftid = shiftlistModelArrayList[i - 1].id
                                            selectedshiftname =
                                                shiftlistModelArrayList[i - 1].shift_title
                                        }
                                    }
                                    fragmentHomeBinding.llDetails.spShift.setOnItemSelectedListener(
                                        this
                                    )


                                } else {

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
//                                showProgressDialog()
                            }

                        }

                    }
                }
        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    private fun OnGPS() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton(
            "Yes"
        ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun getLocation() {


        if (ActivityCompat.checkSelfPermission(
                mainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        } else {

            setLocationListner()
//            val locationGPS = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//            if (locationGPS != null) {
//                val lat = locationGPS.latitude
//                val longi = locationGPS.longitude
//                latitude = lat.toString()
//                longitude = longi.toString()
//
//                viewModel.location.value = locationGPS
//            } else {
//                Toast.makeText(mainActivity, "Unable to find location.", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            getLocation()

        }.also { runnable = it }, delay.toLong())
    }


    override fun onPause() {
        handler.removeCallbacks(runnable!!) //stop handler when activity not visible super.onPause();
        super.onPause()

    }


    private fun distanceinmeter(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

        val startPoint = Location("locationA")
        startPoint.setLatitude(lat1)
        startPoint.setLongitude(lon1)

        val endPoint = Location("locationA")
        endPoint.setLatitude(lat2)
        endPoint.setLongitude(lon2)

        val distance: Double = startPoint.distanceTo(endPoint).toDouble()
        return distance
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == 2404 && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            try {
                picuploadToServer(GetRealPathFromUri.getPathFromUri(mainActivity, fileUri!!)!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(mainActivity, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mainActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {


        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            logoutViewModel.logout(
                authtoken = "Bearer " + sessionManager?.getToken(),
                LogoutRequest(logout_lat = latitude, logout_long = longitude)
            )
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                if (resource.data?.status == true) {

                                    sessionManager?.logoutUser()
                                    val preferences: SharedPreferences =
                                        mainActivity.getSharedPreferences(
                                            "HashMap",
                                            Context.MODE_PRIVATE
                                        )
                                    val editor = preferences.edit()
                                    editor.clear()
                                    editor.apply()
                                    val intent = Intent(mainActivity, Login::class.java)
                                    startActivity(intent)

                                } else {

                                    Toast.makeText(
                                        mainActivity,
                                        resource.data?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    sessionManager?.logoutUser()
                                    val intent = Intent(mainActivity, Login::class.java)
                                    startActivity(intent)

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


    private fun picuploadToServer(pathFromUri: String) {

        val file = File(pathFromUri)


        val fileReqBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", file.name, fileReqBody)

        picuploadviewModel.picupload(
            authtoken = "Bearer " + sessionManager?.getToken(),
            part = part
        ).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status == true) {
                            Toast.makeText(mainActivity, resource.data.message, Toast.LENGTH_SHORT)
                                .show()
                            Glide.with(mainActivity)
                                .load(resource.data.data?.profile_image)
                                .into(fragmentHomeBinding.llDetails.PrfImg)

                        } else {

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


    }

    private fun getDetails() {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            viewModel.userdetails(authtoken = "Bearer " + sessionManager?.getToken())
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                if (resource.data?.status == true) {

                                    fragmentHomeBinding.llDetails.tvUsername.text =
                                        resource.data.data?.name + " " + resource.data.data?.last_name
                                    sessionManager?.setempname(resource.data.data?.name)
                                    sessionManager?.setsnapShot(resource.data.data?.snapshot)
                                    sessionManager?.setpunchinLocation(resource.data.data?.punch_in_loc)
                                    sessionManager?.setpunchIntime(resource.data.data?.punch_in_time)
                                    sessionManager?.setdefultShift(resource.data.data?.default_shift?.shift_title)
                                    sessionManager?.setisHoliday(resource.data.data?.is_holiday)
                                    sessionManager?.setisonLeave(resource.data.data?.is_on_leave)
                                    spShifttime()
                                    sessionManager?.setprofimage(resource.data.data?.profile_image)
                                    Glide.with(mainActivity)
                                        .load(resource.data.data?.profile_image)
                                        .error(R.drawable.user)
                                        .into(fragmentHomeBinding.llDetails.PrfImg)
                                    fragmentHomeBinding.llDetails.tvEmail.text =
                                        resource.data.data?.email
                                    sessionManager?.setempemail(resource.data.data?.email)
                                    fragmentHomeBinding.llDetails.tvEmpcode.text =
                                        resource.data.data?.usercode
                                    sessionManager?.setempcode(resource.data.data?.usercode)
                                    sessionManager?.setUsertypename(resource.data.data?.user_type?.user_type_name)
                                    sessionManager?.setpunchinId(resource.data.data?.punch_in_id.toString())
                                    if (resource.data.data?.attendance_status == 1) {
                                        sessionManager?.setPunchin("punchin")
                                    } else {
                                        sessionManager?.setPunchin("punchout")
                                    }
                                    if (sessionManager?.getPunchin().equals("punchin")) {

                                        fragmentHomeBinding.btMarkattendanceout.visibility =
                                            View.VISIBLE
                                        fragmentHomeBinding.btMarkattendance.visibility = View.GONE
                                        fragmentHomeBinding.imgOnOff.setColorFilter(
                                            ContextCompat.getColor(
                                                mainActivity,
                                                R.color.successtextcolor
                                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                                        )
                                        fragmentHomeBinding.tvPunchinOut.text = "Punch In"

                                        if (distanceinmeter(
                                                sessionManager?.getfencingLat()!!.toDouble(),
                                                sessionManager?.getfencingLong()!!.toDouble(),
                                                latitude?.toDouble()!!,
                                                longitude?.toDouble()!!
                                            ) < 100
                                        ) {

                                            fragmentHomeBinding.btMarkattendance.isClickable = true
                                            fragmentHomeBinding.btMarkattendance.isEnabled = true
                                            fragmentHomeBinding.btMarkattendance.background =
                                                mainActivity.resources.getDrawable(
                                                    R.drawable.button_bg,
                                                    mainActivity.resources.newTheme()
                                                )

                                        } else {

                                            fragmentHomeBinding.btMarkattendance.isClickable = false
                                            fragmentHomeBinding.btMarkattendance.isEnabled = false
                                            fragmentHomeBinding.btMarkattendance.background =
                                                mainActivity.resources.getDrawable(
                                                    R.drawable.button_bg2,
                                                    mainActivity.resources.newTheme()
                                                )

                                        }

                                    } else {

                                        fragmentHomeBinding.btMarkattendanceout.visibility =
                                            View.GONE
                                        fragmentHomeBinding.btMarkattendance.visibility =
                                            View.VISIBLE
                                        fragmentHomeBinding.imgOnOff.setColorFilter(
                                            ContextCompat.getColor(
                                                mainActivity,
                                                R.color.red
                                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                                        )
                                        fragmentHomeBinding.tvPunchinOut.text = "Punch Out"

                                        if (distanceinmeter(
                                                sessionManager?.getfencingLat()!!.toDouble(),
                                                sessionManager?.getfencingLong()!!.toDouble(),
                                                latitude?.toDouble()!!,
                                                longitude?.toDouble()!!
                                            ) < 100
                                        ) {

                                            fragmentHomeBinding.btMarkattendance.isClickable = true
                                            fragmentHomeBinding.btMarkattendance.isEnabled = true
                                            fragmentHomeBinding.btMarkattendance.background =
                                                mainActivity.resources.getDrawable(
                                                    R.drawable.button_bg,
                                                    mainActivity.resources.newTheme()
                                                )


                                        } else {
                                            fragmentHomeBinding.btMarkattendance.isClickable = false
                                            fragmentHomeBinding.btMarkattendance.isEnabled = false
                                            fragmentHomeBinding.btMarkattendance.background =
                                                mainActivity.resources.getDrawable(R.drawable.button_bg2,
                                                    mainActivity.resources.newTheme())
                                        }
                                    }


                                    if (resource.data.data?.reporting_manager?.user?.name == null &&
                                        resource.data.data?.reporting_manager?.user?.last_name != null){

                                        fragmentHomeBinding.llDetails.tvReporterhead.text = ""+resource.data.data?.reporting_manager?.user?.last_name
                                        sessionManager?.setmanager(""+resource.data.data.reporting_manager.user.last_name)

                                    }else if (resource.data.data?.reporting_manager?.user?.name != null &&
                                        resource.data.data.reporting_manager.user.last_name == null){

                                        fragmentHomeBinding.llDetails.tvReporterhead.text = resource.data.data.reporting_manager.user.name + ""
                                        sessionManager?.setmanager(resource.data.data.reporting_manager.user.name + "")

                                    }else if (resource.data.data?.reporting_manager?.user?.name == null &&
                                        resource.data.data?.reporting_manager?.user?.last_name == null){

                                        fragmentHomeBinding.llDetails.tvReporterhead.text = ""
                                        sessionManager?.setmanager("")

                                    }else{

                                        fragmentHomeBinding.llDetails.tvReporterhead.text = resource.data.data.reporting_manager.user.name+" "+resource.data.data.reporting_manager.user.last_name
                                        sessionManager?.setmanager(resource.data.data.reporting_manager.user.name+" "+resource.data.data.reporting_manager.user.last_name)

                                    }


//                                    if (resource.data.data?.reporting_manager?.user?.name == null &&
//                                        resource.data.data?.reporting_manager?.user?.last_name == null) {
//
//                                        fragmentHomeBinding.llDetails.tvReporterhead.text = ""
//                                        sessionManager?.setmanager("")
//
//                                    } else {
//
//                                        fragmentHomeBinding.llDetails.tvReporterhead.text =
//                                            resource.data.data.reporting_manager.user.name + " " +
//                                                    resource.data.data.reporting_manager.user.last_name
//                                        sessionManager?.setmanager(resource.data.data.reporting_manager.user.name + " " +
//                                                    resource.data.data.reporting_manager.user.last_name)
//                                    }

                                    phonenumber = resource.data.data?.phone.toString()
                                    sessionManager?.setphnumber(resource.data.data?.phone.toString())
                                    if (resource.data.data?.locations.isNullOrEmpty()) {
                                        fragmentHomeBinding.llDetails.tvLocation.text = ""
                                        sessionManager?.setempaddress("")
                                    } else {
                                        fragmentHomeBinding.llDetails.tvLocation.text =
                                            resource.data.data?.locations
                                        sessionManager?.setempaddress(resource.data.data?.locations)

                                    }

                                    if (!resource.data.data?.profile_image.isNullOrEmpty()) {
                                        fragmentHomeBinding.llDetails.tvNameinit.visibility =
                                            View.GONE
                                        fragmentHomeBinding.llDetails.PrfImg.visibility =
                                            View.VISIBLE
                                        sessionManager?.setprofimage(resource.data.data?.profile_image)
                                        Glide.with(mainActivity)
                                            .load(resource.data.data?.profile_image)
                                            .into(fragmentHomeBinding.llDetails.PrfImg)

                                    } else {
                                        sessionManager?.setprofimage("")
                                        fragmentHomeBinding.llDetails.tvNameinit.visibility =
                                            View.VISIBLE
                                        fragmentHomeBinding.llDetails.PrfImg.visibility = View.GONE
                                        fragmentHomeBinding.llDetails.tvNameinit.text =
                                            resource.data.data?.name?.substring(0, 1)?.capitalize()
                                    }

                                    punchoutshiftlist.clear()
                                    try {
                                        resource.data.data?.punched_out_shift?.forEach { itString ->
                                            punchoutshiftlist.add(itString!!)

                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }


                                } else {

                                    val builder = AlertDialog.Builder(mainActivity)
                                    builder.setMessage(resource.data?.message)
                                    builder.setPositiveButton(
                                        "Ok"
                                    ) { dialog, which ->

                                        if (resource.data?.message.equals("Read timed out")) {
                                            sessionManager?.logoutUser()
                                            val intent = Intent(mainActivity, Login::class.java)
                                            startActivity(intent)
                                            dialog.cancel()
                                        }
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


        } else {
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT)
                .show()
        }


    }


    private fun setLocationListner() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(mainActivity)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
//                    Log.d(TAG, "lat long-->"+locationResult.locations[0].latitude+","+locationResult.locations[0].longitude)
                    for (location in locationResult.locations) {
                        if (location.latitude != null && location.longitude != null) {
                            latitude = location.latitude.toString()
                            longitude = location.longitude.toString()
                            viewModel.location.value = location
                        }
                    }

                }
            },
            Looper.getMainLooper()
        )
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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mainActivity.finishAffinity()
                    if (activity != null) {
                        activity?.finish()
                    }

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }


    private fun selfshiftchange(shiftid: String) {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {
            shiftlistviewmodel.selfshiftchange(
                authtoken = "Bearer " + sessionManager?.getToken(),
                SelfShiftChangeRequest(shift_id = shiftid, comment = "self shift change")
            )
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
//                            spShifttime()
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (sessionManager?.getPunchin().equals("punchin")) {

            val builder = AlertDialog.Builder(mainActivity)
            builder.setMessage("You need to punch out from your current shift before changing the shift!")
            builder.setPositiveButton(
                "ok"
            ) { dialog, which ->
                dialog.cancel()
            }
            val alert = builder.create()
            alert.show()
            return
        }
        if (position > 0) {
            shiftid = shiftlistModelArrayList.get(position - 1).id
            selectedshiftname = shiftlistModelArrayList.get(position - 1).shift_title
            Log.d(ContentValues.TAG, "shiftid --->" + shiftid)
            Log.d(ContentValues.TAG, "selectedshiftname --->" + selectedshiftname)

            val builder = AlertDialog.Builder(mainActivity)
            builder.setMessage("Please send a shift change request as well as you are changing your shift.")
            builder.setPositiveButton(
                "yes"
            ) { dialog, which ->
                selfshiftchange(shiftid!!)
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }
            val alert = builder.create()
            alert.show()

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

}