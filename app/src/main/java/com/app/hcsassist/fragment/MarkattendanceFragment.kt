package com.app.hcsassist.fragment

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.PunchinRequest
import com.app.hcsassist.databinding.FragmentMarkattendanceBinding
import com.app.hcsassist.modelfactory.PunchinModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.PunchingViewModel
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MarkattendanceFragment : Fragment() {

    lateinit var fragmentMarkattendanceBinding: FragmentMarkattendanceBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    var locationRequest: LocationRequest? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var placesClient: PlacesClient? = null
    lateinit var punchingViewModel: PunchingViewModel
    var formatted_address: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMarkattendanceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_markattendance, container, false)
        val root = fragmentMarkattendanceBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: PunchingViewModel by viewModels {
            PunchinModelFactory(ApiHelper(ApiClient.apiService))
        }
        punchingViewModel = vm

        if (!Places.isInitialized()) {
            Places.initialize(mainActivity, getString(R.string.api_key))
        }
        placesClient = Places.createClient(mainActivity)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                2
            )
        }

        locationRequest = LocationRequest.create()
        locationRequest?.setInterval(100000)
        locationRequest?.setFastestInterval(50000)
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    if (locationResult == null) {
                        return
                    }
                    for (location in locationResult.locations) {
                        try {
                            reverseGeocoding(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        val dateFormat = SimpleDateFormat("dd, MMM yyyy E")
        val currentDate = dateFormat.format(Date())
        fragmentMarkattendanceBinding.llMarkattendance.tvcurrentDate.text = currentDate


        fragmentMarkattendanceBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentMarkattendanceBinding.llMarkattendance.btnPunchin.setOnClickListener {

            sessionManager?.setpunchIntime(fragmentMarkattendanceBinding.llMarkattendance.tvCurrentTime.text.toString())
            val bundle = Bundle()
            bundle.putString("punch_in_location", formatted_address)
            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_facerecognination, bundle)

        }


        return root
    }


    private fun reverseGeocoding(lat: String, long: String) {
        if (CheckConnectivity.getInstance(mainActivity).isOnline) {
            val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                "https://maps.googleapis.com/maps/api/geocode/json?" +
                        "latlng=" + lat + "," + long + "&key=AIzaSyCGRQavtVfIlnBuSkELe98R2MFjXQdnLRc",
                null,
                Response.Listener { response: JSONObject ->
                    Log.i("reverseGeoResponse-->", response.toString())
                    try {
                        val result = JSONObject(response.toString())
                        val status = result.getString("status")
                        val responseArray = result.getJSONArray("results")
                        if (status == "OK") {
                            for (i in 0 until responseArray.length()) {
                                val resultsobj = responseArray.getJSONObject(4)
                                formatted_address = resultsobj.getString("formatted_address")
                                sessionManager?.setpunchinLocation(formatted_address)
                                fragmentMarkattendanceBinding.llMarkattendance.tvCurrentAddress.setText(
                                    formatted_address
                                )
                            }

                        } else {
                            hideProgressDialog()
                            Toast.makeText(mainActivity, "invalid", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    val statusCode: Int = error.networkResponse.statusCode
                    Log.e(ContentValues.TAG, "statuscode-->" + statusCode)
                    hideProgressDialog()

                }) {
            }
            Volley.newRequestQueue(mainActivity).add(jsonRequest)
        } else {
            Toast.makeText(
                mainActivity,
                "Ooops! Internet Connection Error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun punchinattendance(view: View) {
        val deviceinfo = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID)

        punchingViewModel.punchin(
            authtoken = "Bearer " + sessionManager?.getToken(),
            PunchinRequest(punch_in_location = formatted_address, device_type = "android",
                imei_no = deviceinfo, device_info = android.os.Build.MODEL)
        )
            .observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status == true) {
                                sessionManager?.setPunchin("punchin")
                                val navController = Navigation.findNavController(view)
                                navController.navigate(R.id.nav_facerecognination)
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