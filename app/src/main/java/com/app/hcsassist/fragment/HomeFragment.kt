package com.app.hcsassist.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.app.hcsassist.Changepassword
import com.app.hcsassist.Login
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentHomeBinding
import com.app.hcsassist.modelfactory.LogoutModelFactory
import com.app.hcsassist.modelfactory.PicuploadModelFactory
import com.app.hcsassist.modelfactory.UserdetailsModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.LogoutViewModel
import com.app.hcsassist.viewmodel.PicuploadViewModel
import com.app.hcsassist.viewmodel.UserdetailsViewModel
import com.bumptech.glide.Glide
import com.developers.imagezipper.ImageZipper
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: UserdetailsViewModel
    private lateinit var picuploadviewModel: PicuploadViewModel
    private lateinit var logoutViewModel: LogoutViewModel
    var phonenumber:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        val root = fragmentHomeBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: UserdetailsViewModel by viewModels {
            UserdetailsModelFactory(ApiHelper(ApiClient.apiService))
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


        init()

        getDetails()


        return root
    }

    private fun init(){

        if (sessionManager?.getPunchin().equals("punchin")){

            fragmentHomeBinding.btMarkattendanceout.visibility = View.VISIBLE
            fragmentHomeBinding.btMarkattendance.visibility = View.GONE
            fragmentHomeBinding.imgOnOff.setColorFilter(ContextCompat.getColor(mainActivity, R.color.successtextcolor), android.graphics.PorterDuff.Mode.MULTIPLY)
            fragmentHomeBinding.tvPunchinOut.text = "Punch In"

        }else{

            fragmentHomeBinding.btMarkattendanceout.visibility = View.GONE
            fragmentHomeBinding.btMarkattendance.visibility = View.VISIBLE
            fragmentHomeBinding.imgOnOff.setColorFilter(ContextCompat.getColor(mainActivity, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY)
            fragmentHomeBinding.tvPunchinOut.text = "Punch Out"
        }



        if (sessionManager?.getuserid().equals("1") || sessionManager?.getuserid().equals("2")){

            fragmentHomeBinding.btnAdminhr.visibility = View.VISIBLE
            fragmentHomeBinding.btnMss.visibility = View.VISIBLE
            fragmentHomeBinding.btnShiftchange.visibility = View.VISIBLE

        }else{

            fragmentHomeBinding.btnAdminhr.visibility = View.GONE
            fragmentHomeBinding.btnMss.visibility = View.GONE
            fragmentHomeBinding.btnShiftchange.visibility = View.VISIBLE
        }

        fragmentHomeBinding.btMarkattendance.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_markattendance)
        }


        fragmentHomeBinding.btMarkattendanceout.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_markoutattendance)
        }

        fragmentHomeBinding.btnAttendance.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_attendancefragment)
        }

        fragmentHomeBinding.btnMessage.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_chatlistfragment)
        }

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

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_shiftchange)

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

    private fun logout(){

        logoutViewModel.logout(authtoken ="Bearer "+sessionManager?.getToken()).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status==true){

                            sessionManager?.logoutUser()
                            val intent = Intent(mainActivity, Login::class.java)
                            startActivity(intent)

                        }else{

                            Toast.makeText(mainActivity, resource.data?.message, Toast.LENGTH_SHORT).show()
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


    }


    private fun picuploadToServer(pathFromUri: String) {

        val file = File(pathFromUri)
        val imageZipperFile: File = ImageZipper(mainActivity)
            .setQuality(50)
            .setMaxWidth(300)
            .setMaxHeight(300)
            .compressToFile(file)

        val fileReqBody = RequestBody.create("image/jpg".toMediaTypeOrNull(), imageZipperFile)
        val part: MultipartBody.Part = MultipartBody.Part.createFormData("image", imageZipperFile.name, fileReqBody)

        picuploadviewModel.picupload(authtoken ="Bearer "+sessionManager?.getToken(), part = part).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status==true){
                            Toast.makeText(mainActivity, resource.data.message, Toast.LENGTH_SHORT).show()
                            Glide.with(mainActivity)
                                .load(resource.data.data?.profile_image)
                                .into(fragmentHomeBinding.llDetails.PrfImg)

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

    private fun getDetails(){
        viewModel.userdetails(authtoken ="Bearer "+sessionManager?.getToken()).observe(mainActivity) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status==true){

                            fragmentHomeBinding.llDetails.tvUsername.text = resource.data.data?.name
                            fragmentHomeBinding.llDetails.tvEmail.text = resource.data.data?.email
                            fragmentHomeBinding.llDetails.tvEmpcode.text = resource.data.data?.usercode
                            if (resource.data.data?.reporting_manager?.user?.name==null ||
                                resource.data.data?.reporting_manager?.user?.last_name==null){

                                fragmentHomeBinding.llDetails.tvReporterhead.text = ""

                            }else{

                                fragmentHomeBinding.llDetails.tvReporterhead.text = resource.data.data?.reporting_manager?.user?.name + " "+
                                        resource.data.data?.reporting_manager?.user?.last_name
                            }

                            phonenumber = resource.data.data?.phone.toString()
                            if (resource.data.data?.reporting_manager?.user?.full_address.equals("null")){
                                fragmentHomeBinding.llDetails.tvLocation.text = ""
                            }else{
                                fragmentHomeBinding.llDetails.tvLocation.text = resource.data.data?.reporting_manager?.user?.full_address
                            }

                            if (!resource.data.data?.profile_image.equals("null")) {
                                fragmentHomeBinding.llDetails.tvNameinit.visibility = View.GONE
                                fragmentHomeBinding.llDetails.PrfImg.visibility = View.VISIBLE
                                Glide.with(mainActivity)
                                    .load(resource.data.data?.profile_image)
                                    .into(fragmentHomeBinding.llDetails.PrfImg)

                            } else {
                                fragmentHomeBinding.llDetails.tvNameinit.visibility = View.VISIBLE
                                fragmentHomeBinding.llDetails.PrfImg.visibility = View.GONE
                                fragmentHomeBinding.llDetails.tvNameinit.text = resource.data.data?.name?.substring(0,1)?.capitalize()
                            }


                        }else{

                            val builder = AlertDialog.Builder(mainActivity)
                            builder.setMessage(resource.data?.message)
                            builder.setPositiveButton(
                                "Ok"
                            ) { dialog, which ->

                                if (resource.data?.message.equals("Read timed out")){
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