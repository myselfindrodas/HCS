package com.app.hcsassist

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.app.hcsassist.apimodel.ChangePasswordRequest
import com.app.hcsassist.databinding.ActivityChangepasswordBinding
import com.app.hcsassist.modelfactory.ChangepasswordModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.ChangepasswordViewModel
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager

class Changepassword : AppCompatActivity() {

    lateinit var changepasswordBinding: ActivityChangepasswordBinding
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: ChangepasswordViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changepasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_changepassword)
        sessionManager = SessionManager(this)
        val vm: ChangepasswordViewModel by viewModels {
            ChangepasswordModelFactory(ApiHelper(ApiClient.apiService))
        }

        viewModel = vm
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        changepasswordBinding.btnUpdatepassword.setOnClickListener {

            if (changepasswordBinding.etNewpassword.text.length==0){
                Toast.makeText(this, "Enter New Password", Toast.LENGTH_SHORT).show()
            }else if (changepasswordBinding.etConfirmpassword.text.length==0){
                Toast.makeText(this, "Enter Confirm Password", Toast.LENGTH_SHORT).show()
            }else if (!changepasswordBinding.etNewpassword.text.toString().equals(changepasswordBinding.etConfirmpassword.text.toString())){
                Toast.makeText(this, "password not matched!", Toast.LENGTH_SHORT).show()
            }else {
                changePassword(
                    changepasswordBinding.etNewpassword.text.toString(),
                    changepasswordBinding.etConfirmpassword.text.toString()
                )
            }
        }

    }


    private fun changePassword(newpassword:String, confirmpassword:String){

        if (CheckConnectivity.getInstance(this).isOnline) {

            viewModel.changepassword(authtoken ="Bearer "+sessionManager?.getToken(),
                ChangePasswordRequest(password = newpassword, password_confirmation = confirmpassword)).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status==true){

                                val builder = AlertDialog.Builder(this)
                                builder.setMessage(resource.data.message)
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    dialog.cancel()

                                }
                                val alert = builder.create()
                                alert.show()
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