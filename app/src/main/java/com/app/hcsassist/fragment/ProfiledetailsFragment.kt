package com.app.hcsassist.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.app.hcsassist.Changepassword
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentProfiledetailsBinding
import com.app.hcsassist.modelfactory.PicuploadModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.PicuploadViewModel
import com.bumptech.glide.Glide
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfiledetailsFragment : Fragment() {

    lateinit var fragmentProfiledetailsBinding: FragmentProfiledetailsBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var picuploadviewModel: PicuploadViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProfiledetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profiledetails, container, false)
        val root = fragmentProfiledetailsBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val piciuploadvm: PicuploadViewModel by viewModels {
            PicuploadModelFactory(ApiHelper(ApiClient.apiService))
        }

        picuploadviewModel = piciuploadvm

        Glide.with(mainActivity)
            .load(sessionManager?.getprofimage())
            .into(fragmentProfiledetailsBinding.PrfImg)

        fragmentProfiledetailsBinding.etName.setText(sessionManager?.getempname())
        fragmentProfiledetailsBinding.etEmail.setText(sessionManager?.getempemail())
        fragmentProfiledetailsBinding.etDesignation.setText("General Coordinator")
        fragmentProfiledetailsBinding.etEmpcode.setText(sessionManager?.getempcode())
        fragmentProfiledetailsBinding.etLocation.setText(sessionManager?.getempaddress())
        fragmentProfiledetailsBinding.etPhone.setText(sessionManager?.getphnumber())
        fragmentProfiledetailsBinding.etReportingOfficer.setText(sessionManager?.getmanager())


        fragmentProfiledetailsBinding.btnPasswordchange.setOnClickListener {

            val intent = Intent(mainActivity, Changepassword::class.java)
            startActivity(intent)
        }

        fragmentProfiledetailsBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentProfiledetailsBinding.PrfImg.setOnClickListener {

            ImagePicker.Companion.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }


        return root

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


    private fun picuploadToServer(pathFromUri: String) {

        val file = File(pathFromUri)
        val fileReqBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, fileReqBody)

        picuploadviewModel.picupload(authtoken ="Bearer "+sessionManager?.getToken(), part = part).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status==true){
                            Toast.makeText(mainActivity, resource.data.message, Toast.LENGTH_SHORT).show()
                            Glide.with(mainActivity)
                                .load(resource.data.data?.profile_image)
                                .into(fragmentProfiledetailsBinding.PrfImg)

                        }else{

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