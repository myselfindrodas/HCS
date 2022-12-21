package com.app.hcsassist

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task


class MainActivity : AppCompatActivity() {
    var mNavController: NavController? = null
    private val REQ_CODE_VERSION_UPDATE = 530
    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mNavController = findNavController(R.id.flFragment)
        mNavController?.navigate(R.id.nav_home)
        checkForAppUpdate()


    }


    override fun onResume() {
        super.onResume()
        checkNewAppVersionState()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQ_CODE_VERSION_UPDATE -> if (resultCode != Activity.RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                Log.d("tag","Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
                unregisterInstallStateUpdListener()
                finishAndRemoveTask()

            }else if (resultCode == Activity.RESULT_OK){
                Log.d("tag","Update done Result code: $resultCode")

            }
        }
    }

    override fun onDestroy() {
        unregisterInstallStateUpdListener()
        super.onDestroy()
    }


    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager!!.getAppUpdateInfo()

        installStateUpdatedListener =
            InstallStateUpdatedListener { installState ->
                if (installState.installStatus() == InstallStatus.DOWNLOADED) // After the update is downloaded, show a notification
                    popupSnackbarForCompleteUpdateAndUnregister()
            }

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

//                    appUpdateManager!!.registerListener(installStateUpdatedListener)
//                    startAppUpdateFlexible(appUpdateInfo)
//                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startAppUpdateImmediate(appUpdateInfo)
                }
            }
        }
    }


    private fun startAppUpdateImmediate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                this,  // Include a request code to later monitor this update request.
                REQ_CODE_VERSION_UPDATE
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }


//    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
//        try {
//            appUpdateManager!!.startUpdateFlowForResult(
//                appUpdateInfo,
//                AppUpdateType.FLEXIBLE,  // The current activity making the update request.
//                this,  // Include a request code to later monitor this update request.
//                REQ_CODE_VERSION_UPDATE
//            )
//        } catch (e: IntentSender.SendIntentException) {
//            e.printStackTrace()
//            unregisterInstallStateUpdListener()
//        }
//    }


    private fun popupSnackbarForCompleteUpdateAndUnregister() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Update Download")
        builder.setPositiveButton(
            "Restart"
        ) { dialog, which ->

            appUpdateManager!!.completeUpdate()

        }

        val alert = builder.create()
        alert.show()

        unregisterInstallStateUpdListener()
    }


    private fun checkNewAppVersionState() {
        appUpdateManager
            ?.getAppUpdateInfo()
            ?.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                //IMMEDIATE:
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    startAppUpdateImmediate(appUpdateInfo)
                }
            }
    }


    private fun unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null) appUpdateManager!!.unregisterListener(
            installStateUpdatedListener!!
        )
    }

}