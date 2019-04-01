package net.sarasarasa.lifeup.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.MainActivity
import java.lang.ref.WeakReference

class MessageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_temp, container, false)
        (activity as MainActivity).initToolBar(WeakReference(rootView.findViewById(R.id.toolbar)))

        return rootView
    }
}