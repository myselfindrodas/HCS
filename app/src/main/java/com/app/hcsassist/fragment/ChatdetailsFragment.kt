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
import com.app.hcsassist.databinding.FragmentChatdetailsBinding
import com.example.hllapplication.Adapter.ChatDetailsAdapter

class ChatdetailsFragment : Fragment() {

    lateinit var fragmentChatdetailsBinding: FragmentChatdetailsBinding
    lateinit var mainActivity: MainActivity
    lateinit var chatDetailsAdapter: ChatDetailsAdapter
    var arrayList :ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatdetailsBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_chatdetails,container,false)
        val root = fragmentChatdetailsBinding.root
        mainActivity=activity as MainActivity

        chatDetailsAdapter = ChatDetailsAdapter(mainActivity, arrayList)
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(mainActivity, 1)
        fragmentChatdetailsBinding.recChatDetails.layoutManager = mLayoutManager
        fragmentChatdetailsBinding.recChatDetails.itemAnimator = DefaultItemAnimator()
        fragmentChatdetailsBinding.recChatDetails.adapter = chatDetailsAdapter

        mainActivity=activity as MainActivity

        fragmentChatdetailsBinding.backIcon.setOnClickListener {

            mainActivity.onBackPressed()
        }


        return root
    }

}