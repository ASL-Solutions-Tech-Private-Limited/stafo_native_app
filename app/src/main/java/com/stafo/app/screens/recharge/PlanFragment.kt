package com.stafo.app.screens.recharge

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.screens.recharge.adapter.AdapterMobilePlan
import com.stafo.app.screens.recharge.dataclass.RechargeInfo

class PlanFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val ARG_DATA = "data"

        fun newInstance(data: List<RechargeInfo>): PlanFragment {
            val fragment = PlanFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_DATA, ArrayList(data))
            fragment.arguments = args
            return fragment
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data: List<RechargeInfo>? = arguments?.getParcelableArrayList(ARG_DATA)

        if (data != null) {
            recyclerView.adapter = AdapterMobilePlan(requireActivity(),data)
        }

        return view
    }


}