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
import com.app.hcsassist.databinding.FragmentChatlistBinding
import com.example.hllapplication.Adapter.ChatAdapter

class ChatlistFragment : Fragment() {

    lateinit var fragmentChatlistBinding: FragmentChatlistBinding
    lateinit var mainActivity: MainActivity
    lateinit var chatAdapter: ChatAdapter
    var arrayList :ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatlistBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_chatlist,container,false)
        val root = fragmentChatlistBinding.root
        mainActivity=activity as MainActivity

        chatAdapter = ChatAdapter(requireContext(), arrayList)
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(requireContext(), 1)
        fragmentChatlistBinding.recChat.layoutManager = mLayoutManager
        fragmentChatlistBinding.recChat.itemAnimator = DefaultItemAnimator()
        fragmentChatlistBinding.recChat.adapter = chatAdapter

        fragmentChatlistBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        return root
    }

}