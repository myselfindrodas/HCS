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
import com.app.hcsassist.databinding.FragmentFeedbacksubmitBinding

class FeedbacksubmitFragment : Fragment() {

    lateinit var fragmentFeedbacksubmitBinding: FragmentFeedbacksubmitBinding
    lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFeedbacksubmitBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_feedbacksubmit,container,false)
        val root = fragmentFeedbacksubmitBinding.root
        mainActivity=activity as MainActivity

        fragmentFeedbacksubmitBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentFeedbacksubmitBinding.btnSubmit.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_home)
        }

        return root
    }
}