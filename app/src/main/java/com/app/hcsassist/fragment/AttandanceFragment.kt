package com.app.hcsassist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentAttandanceBinding
import com.example.hllapplication.Adapter.AttandanceAdapter


class AttandanceFragment : Fragment() {

    lateinit var fragmentAttandanceBinding: FragmentAttandanceBinding
    lateinit var mainActivity: MainActivity
    lateinit var attandanceAdapter:AttandanceAdapter

    var arrayList :ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAttandanceBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_attandance,container,false)
        val root = fragmentAttandanceBinding.root
        mainActivity=activity as MainActivity

        attandanceAdapter = AttandanceAdapter(mainActivity, arrayList)
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(requireContext(), 1)
        fragmentAttandanceBinding.recAttandance.layoutManager = mLayoutManager
        fragmentAttandanceBinding.recAttandance.itemAnimator = DefaultItemAnimator()
        fragmentAttandanceBinding.recAttandance.adapter = attandanceAdapter

        fragmentAttandanceBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }


        return root
    }


}