package com.breadcrumbsapp.view.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.PendingFriendRequestAdapter
import com.breadcrumbsapp.interfaces.FriendRequestListener
import com.breadcrumbsapp.model.GetFriendsListModel
import com.breadcrumbsapp.view.NewFriendRequestAct
import com.breadcrumbsapp.viewmodel.NewFriendRequestViewModel
import kotlinx.android.synthetic.main.fragment_pending_friend_request.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PendingFriendRequestFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class PendingFriendRequestFrag : Fragment(), FriendRequestListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var viewModel : NewFriendRequestViewModel
    private var requestList = ArrayList<GetFriendsListModel.Message>()
    lateinit var requestAdapter : PendingFriendRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_friend_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (requireActivity() as NewFriendRequestAct).viewModel

        requestAdapter = PendingFriendRequestAdapter(requireActivity(), requestList, this)
        pr_rv.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            adapter = requestAdapter
        }

        viewModel.sentFriendRequestList.observe(viewLifecycleOwner, Observer {
            println("Check::::::::::::::::::::: ")
            requestList.clear()
            if(it != null){
                requestList.addAll(it)
                requestAdapter.notifyDataSetChanged()
                println("Frag Pending:: ${requestList.size}")
                if(requestList.size==0)
                {
                    pr_no_data.visibility=View.VISIBLE
                    pr_rv.visibility=View.GONE
                }
                else
                {
                    pr_no_data.visibility=View.GONE
                    pr_rv.visibility=View.VISIBLE
                }
            }
        })

        viewModel.requestStatus.observe(viewLifecycleOwner, Observer {
            println("Check::::::::::::::: Pending Request " )
            it?.let {
                println("Check::::::::::::::: Pending Request $it" )
                var newList = ArrayList<GetFriendsListModel.Message>()
                requestList.forEach{ item ->
                    if(it != item.uf_id.toInt()){
                        newList.add(item)
                    }
                }
                requestList.clear()
                requestList.addAll(newList)
                requestAdapter.notifyDataSetChanged()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PendingFriendRequestFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PendingFriendRequestFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAcceptItemClick(id: String, status: Boolean) {
        viewModel.acceptORCancelFriendRequest(id, status)
    }
}