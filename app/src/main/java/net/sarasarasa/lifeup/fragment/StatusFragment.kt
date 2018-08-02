package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity

class StatusFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_status, null)
        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))
        return view
    }

    override fun onResume() {
        super.onResume()

    }


}
