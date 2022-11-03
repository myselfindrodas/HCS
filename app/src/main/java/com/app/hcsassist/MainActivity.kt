package com.app.hcsassist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    var mNavController: NavController?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNavController = findNavController(R.id.flFragment)
        mNavController?.navigate(R.id.nav_home)
    }
}