package com.app.hcsassist

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.app.hcsassist.apimodel.LoginRequest
import com.app.hcsassist.databinding.ActivityLoginBinding
import com.app.hcsassist.modelfactory.LoginModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.LoginViewModel
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson

class Login : AppCompatActivity() {

    lateinit var activityLoginBinding: ActivityLoginBinding
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: LoginViewModel
    var REQUEST_CODE = 101
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationManager: LocationManager? = null
    var latitude: String? = null
    var longitude: String? = null
    var placesClient: PlacesClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sessionManager = SessionManager(this)
        val vm: LoginViewModel by viewModels {
            LoginModelFactory(ApiHelper(ApiClient.apiService))
        }

        viewModel = vm
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)



        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.api_key))
        }
        placesClient = Places.createClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (!locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            || !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || !locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {

            OnGPS()

        }else{
            getLocation()
        }


        activityLoginBinding.btnLogin.setOnClickListener {

            if (activityLoginBinding.etUsername.text.length==0){
                Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show()
            }else if (activityLoginBinding.etPassword.text.length==0){
//                Toast.makeText(this, "The password must be at least 8 characters.", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            } else {
                login(
                    activityLoginBinding.etUsername.text.toString(),
                    activityLoginBinding.etPassword.text.toString()
                )
            }

        }



        activityLoginBinding.btnForgotpass.setOnClickListener {

            Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()

        }



    }


    private fun OnGPS() {
        val builder = AlertDialog.Builder(this)
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
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        } else {
            val locationGPS = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (locationGPS != null) {
                val lat = locationGPS.latitude
                val longi = locationGPS.longitude
                latitude = lat.toString()
                longitude = longi.toString()
                sessionManager?.setcurrentLat(latitude)
                sessionManager?.setcurrentLong(longitude)
                Log.d(TAG, "latilong-->"+latitude+" , "+longitude)

            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun login(username: String, password: String){
        val deviceinfo = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

        if (CheckConnectivity.getInstance(this).isOnline) {

            viewModel.login(appKey ="Rof2yL89L0nJbte0QgDGFZoxAAuWKwbZTx7R4nMcQNo=",
                LoginRequest(username = username, password = password,
                    lat = latitude, long = longitude, device_type = "android", device_id =deviceinfo)).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status==true){

                                sessionManager?.setToken(resource.data.data?.token)
                                sessionManager?.setUsername(resource.data.data?.name)
                                sessionManager?.setid(resource.data.data?.id)
                                sessionManager?.setusercode(resource.data.data?.usercode)
                                sessionManager?.setaddress(resource.data.data?.full_address?.present?.address_line_1)
                                sessionManager?.setphone(resource.data.data?.phone)
                                sessionManager?.setemail(resource.data.data?.email)
                                sessionManager?.setuserid(resource.data.data?.user_type_id)
                                sessionManager?.setfencingLat(resource.data.data?.lat)
                                sessionManager?.setfencingLong(resource.data.data?.long)
                                sessionManager?.setisHoliday(resource.data.data?.is_holiday)
                                sessionManager?.setisonLeave(resource.data.data?.is_on_leave)
                                sessionManager?.setdefultShift(resource.data.data?.default_shift?.shift_title)
                                sessionManager?.setpunchinId(resource.data.data?.punch_in_id.toString())
                                if (resource.data.data?.attendance_status.toString().equals("1")){
                                    sessionManager?.setPunchin("punchin")
                                }else{
                                    sessionManager?.setPunchin("punchout")
                                }
//                            sessionManager?.setPunchin(resource.data.data?.attendance_status.toString())

                                if (activityLoginBinding.btncheckbox.isChecked){
                                    sessionManager?.createLoginSession(
                                        activityLoginBinding.etUsername.text.toString(),
                                        activityLoginBinding.etPassword.text.toString()
                                    )
                                }else{

                                    sessionManager?.createLoginSession(
                                        activityLoginBinding.etUsername.text.toString(),
                                        activityLoginBinding.etPassword.text.toString()
                                    )
                                }

                                val builder = AlertDialog.Builder(this@Login)
                                builder.setMessage(resource.data.message)
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->
                                    if (resource.data.data?.snapshot==null || resource.data.data?.snapshot.isEmpty()){
                                        val intent = Intent(this@Login, Snapshotcapture::class.java)
                                        startActivity(intent)
                                        finish()
                                        dialog.cancel()
                                    }else{
                                        val intent = Intent(this@Login, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                        dialog.cancel()
                                    }



                                }
                                val alert = builder.create()
                                alert.show()
                            }else{

                                val builder = AlertDialog.Builder(this@Login)
                                builder.setMessage("Invalid Username / Password")
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
                            if (it.message!!.contains("401",true)) {
                                val builder = AlertDialog.Builder(this@Login)
                                builder.setMessage("Invalid Username / Password")
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->

                                    dialog.cancel()

                                }
                                val alert = builder.create()
                                alert.show()
                            }else
                                 Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                        }

                        Status.LOADING -> {
                            showProgressDialog()
                        }

                    }

                }
            }

        }else{
            Toast.makeText(this, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }





    }






    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
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