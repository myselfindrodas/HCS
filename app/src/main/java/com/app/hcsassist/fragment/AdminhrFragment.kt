package com.app.hcsassist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentAdminhrBinding

class AdminhrFragment : Fragment() {

    lateinit var fragmentAdminhrBinding: FragmentAdminhrBinding
    lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAdminhrBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_adminhr,container,false)
        val root = fragmentAdminhrBinding.root
        mainActivity=activity as MainActivity


        fragmentAdminhrBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }



        return root
    }

}