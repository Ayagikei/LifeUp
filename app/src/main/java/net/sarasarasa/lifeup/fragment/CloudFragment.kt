package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_comm.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.LoginActivity
import net.sarasarasa.lifeup.activities.MainActivity
import net.sarasarasa.lifeup.service.impl.UserServiceImpl

class CloudFragment : Fragment() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private val userService = UserServiceImpl()

    override fun onCreateView(inflater: LayoutInflater, mContainer: ViewGroup?, savedInstanceState: Bundle?): View? {



        val view = inflater.inflate(R.layout.fragment_cloud, null)

        //设置toolbar
        (activity as MainActivity).initToolBar(view.findViewById(R.id.toolbar))

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        // Set up the ViewPager with the sections adapter.
        val viewPager = view.findViewById<ViewPager>(R.id.container)
        val tabs = view.findViewById<TabLayout>(R.id.tabs)
        viewPager.adapter = mSectionsPagerAdapter


        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        viewPager.offscreenPageLimit = 0
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        return view
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            when {
                userService.getToken().isEmpty() -> return PlaceholderFragment.newInstance(position + 1, true)
                position == 0 -> return TeamListFragment()
                position == 1 -> return MomentsFragment()
                position == 2 -> return BoardFragment()
                else -> return PlaceholderFragment.newInstance(position + 1, false)
            }

        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /** 刷新数据 **/
    override fun onResume() {
        super.onResume()

        mSectionsPagerAdapter?.notifyDataSetChanged()
    }

    /** 刷新数据 **/
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mSectionsPagerAdapter?.notifyDataSetChanged()
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_comm, container, false)

            arguments?.let {
                if (it.getBoolean("isWithoutToken")) {
                    rootView.tv_error2.text = "您需要登陆才能使用本功能！\n点击此处登陆！"
                    rootView.setOnClickListener {
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    rootView.tv_error2.text = "本功能未开放！"
                }
            }


            //rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }


        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int, isWithoutToken: Boolean): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                args.putBoolean("isWithoutToken", isWithoutToken)

                fragment.arguments = args
                return fragment
            }
        }
    }
}
