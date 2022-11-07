package com.app.hcsassist

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.hcsassist.apimodel.LoginRequest
import com.app.hcsassist.databinding.ActivityLoginBinding
import com.app.hcsassist.modelfactory.LoginModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.LoginViewModel
import com.example.wemu.session.SessionManager
import com.google.gson.Gson

class Login : AppCompatActivity() {

    lateinit var activityLoginBinding: ActivityLoginBinding
    var sessionManager: SessionManager? = null
    private lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        sessionManager = SessionManager(this)
        val vm: LoginViewModel by viewModels {
            LoginModelFactory(ApiHelper(ApiClient.apiService))
        }

        viewModel = vm


        activityLoginBinding.btnLogin.setOnClickListener {

            if (activityLoginBinding.etUsername.text.length==0){
                Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show()
            }else if (activityLoginBinding.etPassword.text.length<=7){
                Toast.makeText(this, "The password must be at least 8 characters.", Toast.LENGTH_SHORT).show()
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


    private fun login(username: String, password: String){
        viewModel.login(appKey ="Rof2yL89L0nJbte0QgDGFZoxAAuWKwbZTx7R4nMcQNo=",
            LoginRequest(username = username, password = password)).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressDialog()
                        if (resource.data?.status==true){

                            sessionManager?.setToken(resource.data.data?.token)
                            sessionManager?.setUsername(resource.data.data?.name)
                            sessionManager?.setid(resource.data.data?.id)
                            sessionManager?.setusercode(resource.data.data?.usercode)
                            sessionManager?.setaddress(resource.data.data?.full_address)
                            sessionManager?.setphone(resource.data.data?.phone)
                            sessionManager?.setemail(resource.data.data?.email)
                            sessionManager?.setuserid(resource.data.data?.user_type_id)

                            if (activityLoginBinding.btncheckbox.isChecked){
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
                                if (resource.data.data?.snapshot==null){
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
                            builder.setMessage(resource.data?.errors)
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