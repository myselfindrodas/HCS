package com.app.hcsassist

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.hcsassist.modelfactory.FRSModelFactory
import com.app.hcsassist.modelfactory.LoginModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.FRSViewModel
import com.app.hcsassist.viewmodel.LoginViewModel
import com.bumptech.glide.Glide
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class Snapshotcapture : AppCompatActivity() {
    private lateinit var frsViewModel: FRSViewModel
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshotcapture)
        sessionManager = SessionManager(this)

        val vm: FRSViewModel by viewModels {
            FRSModelFactory(ApiHelper(ApiClient.apiService))
        }

        frsViewModel = vm

        ImagePicker.with(this)
            .cameraOnly()	//User can only capture image using Camera
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data!!.data
            picuploadToServer(GetRealPathFromUri.getPathFromUri(this, fileUri!!)!!)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun picuploadToServer(pathFromUri: String){

        val file = File(pathFromUri)
        val bm = BitmapFactory.decodeFile(pathFromUri)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
        val base64image: ByteArray = baos.toByteArray()

        val fileReqBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), base64image)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, fileReqBody)


        frsViewModel.uploadfrs(authtoken ="Bearer "+sessionManager?.getToken(), part = part).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status==true){
                            Toast.makeText(this, resource.data.message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{

                            val builder = AlertDialog.Builder(this)
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
                        val builder = AlertDialog.Builder(this)
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