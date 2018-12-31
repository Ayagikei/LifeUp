package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_team_list.*
import kotlinx.android.synthetic.main.fragment_team_list.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddTeamActivity
import net.sarasarasa.lifeup.activities.TeamActivity
import net.sarasarasa.lifeup.adapters.TeamListAdapter
import net.sarasarasa.lifeup.base.RecyclerViewNoBugLinearLayoutManager
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_LIST_SUCCESS
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamListVO


class TeamListFragment : Fragment() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
            }
            MSG_GET_TEAM_LIST_SUCCESS -> {
                if (msg.obj != null) {

                    try {
                        val pageVO = msg.obj as PageVO<*>

                        if (pageVO.list != null) {
                            val list = pageVO.list as List<TeamListVO>

                            totalPage = pageVO.totalPage

                            if (swipe_refresh_layout != null)
                                if (swipe_refresh_layout.isRefreshing) {
                                    swipe_refresh_layout.isRefreshing = false
                                    mAdapter.data.clear()
                                }

                            setNewData(list.toMutableList())

                            if (swipe_refresh_layout != null)
                                swipe_refresh_layout.isEnabled = true

                            mAdapter.setEnableLoadMore(true)
                            mAdapter.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        ToastUtils.showShortToast(e.toString())
                    }
                }
            }
            AttributeConstants.MSG_CONNECT_FAILED -> {
                if (swipe_refresh_layout != null && swipe_refresh_layout.isRefreshing)
                    swipe_refresh_layout.isRefreshing = false

                mAdapter.loadMoreFail()

                ToastUtils.showShortToast("网络错误，请稍后重试。")
            }
            else -> {
                if (msg.obj != null) {
                    mAdapter.loadMoreFail()

                    if (swipe_refresh_layout != null)
                        swipe_refresh_layout.isEnabled = true
                }
            }

        }

        return@Callback true
    }


    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)
    private val mList: MutableList<TeamListVO> = ArrayList<TeamListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TeamListAdapter
    private var currentPage = 0L
    private var totalPage: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_team_list, container, false)
        setHasOptionsMenu(true)


        initView(rootView)
        activity?.let { LoadingDialogUtils.show(it) }

        return rootView
    }

    private fun initView(rootView: View) {
        initRecyclerView(rootView)

        rootView.swipe_refresh_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        rootView.swipe_refresh_layout.setOnRefreshListener {

            mAdapter.setOnLoadMoreListener({
                getNewList()
                swipe_refresh_layout.isEnabled = false
            }, mRecyclerView)


            currentPage = 0L
            totalPage = null
            mAdapter.setEnableLoadMore(false)
            mAdapter.data.clear()
            getNewList()
        }

        rootView.fab.setOnClickListener {
            val intent = Intent(this.context, AddTeamActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView(rootView: View) {
        mRecyclerView = rootView.rv
        mAdapter = TeamListAdapter(R.layout.item_team, mList)
        mRecyclerView.layoutManager = RecyclerViewNoBugLinearLayoutManager(context)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({
            getNewList()
            swipe_refresh_layout.isEnabled = false
        }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(true)
        mAdapter.setOnItemClickListener { adapter, view, position ->


            val item = adapter.getItem(position) as TeamListVO

            val intent = Intent(context, TeamActivity::class.java)
            intent.putExtra("teamId", item.teamId)

            startActivity(intent)
        }
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamListVO>()
        pageVO.size = 15
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        teamNetworkImpl.getTeamList(pageVO)
    }

    private fun getNewList(searchText: String) {
        val pageVO = PageVO<TeamListVO>()
        pageVO.size = 15
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        teamNetworkImpl.getTeamList(pageVO, searchText)
    }

    private fun setNewData(list: MutableList<TeamListVO>) {
        mAdapter.addData(list)


        if (totalPage != null) {
            if (currentPage >= totalPage!!) {
                mAdapter.loadMoreEnd()
            } else {
                mAdapter.loadMoreComplete()
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_team_list, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        val mSearchView = MenuItemCompat.getActionView(searchItem) as SearchView
        //mSearchView.setIconifiedByDefault(false)

        //搜索框文字变化监听
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                mAdapter.setEnableLoadMore(false)
                mAdapter.setOnLoadMoreListener({
                    getNewList(s)
                    swipe_refresh_layout.isEnabled = false
                }, mRecyclerView)

                if (swipe_refresh_layout != null)
                    swipe_refresh_layout.isEnabled = false
                currentPage = 0L

                mAdapter.data.clear()
                getNewList(s)


                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        mSearchView.setOnCloseListener {
            mAdapter.setEnableLoadMore(false)
            mAdapter.setOnLoadMoreListener({
                getNewList()
                swipe_refresh_layout.isEnabled = false
            }, mRecyclerView)

            if (swipe_refresh_layout != null)
                swipe_refresh_layout.isEnabled = false
            currentPage = 0L

            mAdapter.data.clear()
            getNewList()

            false
        }


        val editText = mSearchView.findViewById<EditText>(R.id.search_src_text)
        editText.setHintTextColor(ContextCompat.getColor(this.context!!, R.color.light_gray))


        super.onCreateOptionsMenu(menu, inflater)
    }

}