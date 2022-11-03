package com.app.hcsassist.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.app.hcsassist.Login
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentMarkpunchoutBinding
import com.example.wemu.session.SessionManager

class MarkpunchoutFragment : Fragment() {

    lateinit var fragmentMarkpunchoutBinding: FragmentMarkpunchoutBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMarkpunchoutBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_markpunchout,container,false)
        val root = fragmentMarkpunchoutBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)


        fragmentMarkpunchoutBinding.btnBack.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_home)
        }

        fragmentMarkpunchoutBinding.llMarkoutattendance.btnPunchout.setOnClickListener {

            sessionManager?.setPunchin("punchout")
            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_home)
        }


        return root
    }

}