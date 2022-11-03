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
import com.app.hcsassist.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    lateinit var fragmentFeedbackBinding: FragmentFeedbackBinding
    lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFeedbackBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_feedback,container,false)
        val root = fragmentFeedbackBinding.root
        mainActivity=activity as MainActivity


        fragmentFeedbackBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }


        fragmentFeedbackBinding.btnFloating.setOnClickListener {


            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_feedbacksubmit)
        }


        return root
    }

}