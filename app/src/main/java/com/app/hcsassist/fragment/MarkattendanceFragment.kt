package com.app.hcsassist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentMarkattendanceBinding
import com.example.wemu.session.SessionManager

class MarkattendanceFragment : Fragment() {

    lateinit var fragmentMarkattendanceBinding: FragmentMarkattendanceBinding
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMarkattendanceBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_markattendance,container,false)
        val root = fragmentMarkattendanceBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)

        fragmentMarkattendanceBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentMarkattendanceBinding.llMarkattendance.btnPunchin.setOnClickListener {

            sessionManager?.setPunchin("punchin")
            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_facerecognination)
        }


        return root
    }

}