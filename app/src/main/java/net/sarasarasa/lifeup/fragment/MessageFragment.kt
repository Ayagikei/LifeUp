package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity

class MessageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_temp, container, false)
        (activity as MainActivity).initToolBar(rootView.findViewById(R.id.toolbar))


        return rootView
    }
}