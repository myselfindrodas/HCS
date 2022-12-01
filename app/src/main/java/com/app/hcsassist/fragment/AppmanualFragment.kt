package com.app.hcsassist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentAppmanualBinding

class AppmanualFragment : Fragment() {

    lateinit var fragmentAppmanualBinding: FragmentAppmanualBinding
    lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAppmanualBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_appmanual,container,false)
        val root = fragmentAppmanualBinding.root
        mainActivity=activity as MainActivity

        fragmentAppmanualBinding.btnDownload.setOnClickListener {
            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_chatlistfragment)
        }

        fragmentAppmanualBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        return root
    }

}