package com.app.hcsassist

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.wemu.session.SessionManager
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions


class Splash : AppCompatActivity() {
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sessionManager = SessionManager(this)

    }


    private fun requestAllPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE)
        val rationale = "This app needs permission to use app feature. You need to grant them for using the application."
        val options: Permissions.Options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")

        Permissions.check(this, permissions, rationale, options,
            object : PermissionHandler() {
                override fun onGranted() {

                    Toast.makeText(applicationContext, "All permissions are granted!", Toast.LENGTH_SHORT).show()
                    val secondsDelayed = 1
                    Handler().postDelayed({
                        if (sessionManager!!.isLoggedIn) {
                            val intent = Intent(this@Splash, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@Splash, Login::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }, (secondsDelayed * 3000).toLong())
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String?>?) {

                    finishAndRemoveTask()
                }
            })

//        Dexter.withActivity(this)
//            .withPermissions(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.CAMERA,
//                Manifest.permission.CALL_PHONE,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE
//            ).withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    // check if all permissions are granted
//                    if (report.areAllPermissionsGranted()) {
//                        Toast.makeText(applicationContext, "All permissions are granted!", Toast.LENGTH_SHORT).show()
//
//                        val secondsDelayed = 1
//                        Handler().postDelayed({
//                            if (sessionManager!!.isLoggedIn) {
//                                val intent = Intent(this@Splash, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            } else {
//                                val intent = Intent(this@Splash, Login::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }, (secondsDelayed * 3000).toLong())
//
//                    }else{
//                        finishAffinity()
//                        onDestroy()
//
//                    }
//                   /* if (report.isAnyPermissionPermanentlyDenied) {
//                        finishAffinity()
//                        onDestroy()
//
//                    }*/
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest>,
//                    token: PermissionToken
//                ) {
//                    token.continuePermissionRequest()
//                }
//            }).withErrorListener {
//                Toast.makeText(
//                    applicationContext,
//                    "Error occurred! ",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            .onSameThread()
//            .check()
    }

    override fun onResume() {
        super.onResume()
        requestAllPermission()
    }


    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings and reopen the application.")
        builder.setPositiveButton(
            "Ok"
        ) { dialog, which ->
            dialog.cancel()
            finishAffinity()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()

    }
}