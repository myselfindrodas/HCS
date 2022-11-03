package com.app.hcsassist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentLeaveBinding
import com.app.hcsassist.modelfactory.LeaveModelFactory
import com.app.hcsassist.modelfactory.UserdetailsModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.LeaveListViewModel
import com.app.hcsassist.viewmodel.UserdetailsViewModel
import com.example.hllapplication.Adapter.AllLeavesAdapter
import com.example.hllapplication.Adapter.AttandanceAdapter
import com.example.hllapplication.Adapter.LeavesListAdapter
import com.example.hllapplication.Adapter.TypesOfLeavesAdapter
import com.example.wemu.session.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LeaveFragment : Fragment() {

    lateinit var fragmentLeaveBinding: FragmentLeaveBinding
    lateinit var typesOfLeavesAdapter:TypesOfLeavesAdapter
    lateinit var allLeavesAdapter: AllLeavesAdapter
    var arrayList :ArrayList<String> = ArrayList()
    lateinit var mainActivity: MainActivity
    var sessionManager: SessionManager? = null
    private lateinit var leaveListViewModel: LeaveListViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLeaveBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_leave, container, false)
        val root = fragmentLeaveBinding.root
        mainActivity=activity as MainActivity
        sessionManager = SessionManager(mainActivity)

        val vm: LeaveListViewModel by viewModels {
            LeaveModelFactory(ApiHelper(ApiClient.apiService))
        }

        leaveListViewModel = vm


        fragmentLeaveBinding.floatingBtn.setOnClickListener {

            val navController = Navigation.findNavController(it)
            navController.navigate(R.id.nav_applyleavefragment)
        }

        fragmentLeaveBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()

        }


        val horizontaLayoutManagaer =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        fragmentLeaveBinding.recLeftoverLeave.setLayoutManager(
            horizontaLayoutManagaer
        )
        fragmentLeaveBinding.recLeftoverLeave.setAdapter(allLeavesAdapter)



        typesOfLeavesAdapter = TypesOfLeavesAdapter(
            requireContext(),
            arrayList
        )

        val horizontaLayoutManagaer1 =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        fragmentLeaveBinding.recTypesleaves.setLayoutManager(
            horizontaLayoutManagaer1
        )
        fragmentLeaveBinding.recTypesleaves.setAdapter(typesOfLeavesAdapter)



        return root
    }



    private fun allLeave(){


    }


}