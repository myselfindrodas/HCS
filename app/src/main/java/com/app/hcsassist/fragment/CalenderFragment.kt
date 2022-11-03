package com.app.hcsassist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentCalenderBinding


class CalenderFragment : Fragment() {

    lateinit var fragmentCalenderBinding: FragmentCalenderBinding
    lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCalenderBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_calender,container,false)
        val root = fragmentCalenderBinding.root
        mainActivity=activity as MainActivity


        fragmentCalenderBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        return root
    }

}