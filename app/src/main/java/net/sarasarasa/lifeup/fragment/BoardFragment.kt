package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_team_list.*
import kotlinx.android.synthetic.main.fragment_team_list.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.AddTeamActivity
import net.sarasarasa.lifeup.adapters.BoardrListAdapter
import net.sarasarasa.lifeup.base.RecyclerViewNoBugLinearLayoutManager
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_MEMBER_LIST_SUCCESS
import net.sarasarasa.lifeup.network.impl.AchievementNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamMembaerListVO

class BoardFragment : Fragment() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
            }
            MSG_GET_TEAM_MEMBER_LIST_SUCCESS -> {
                if (msg.obj != null) {
                    val pageVO = msg.obj as PageVO<*>
                    val list = pageVO.list as List<TeamMembaerListVO>

                    totalPage = pageVO.totalPage ?: totalPage
                    if (currentPage > totalPage!!) {
                        currentPage = totalPage as Long
                    }

                    if (swipe_refresh_layout.isRefreshing) {
                        swipe_refresh_layout.isRefreshing = false
                        mAdapter.data.clear()
                    }

                    setNewData(list.toMutableList())

                    swipe_refresh_layout.isEnabled = true
                    mAdapter.setEnableLoadMore(true)
                    mAdapter.notifyDataSetChanged()
                }
            }
            AttributeConstants.MSG_CONNECT_FAILED -> {
                if (swipe_refresh_layout.isRefreshing)
                    swipe_refresh_layout.isRefreshing = false

                mAdapter.loadMoreFail()

                ToastUtils.showShortToast("网络错误，请稍后重试。")
            }
            else -> {
                if (msg.obj != null) {
                    mAdapter.loadMoreFail()

                    swipe_refresh_layout.isEnabled = true
                }
            }

        }

        return@Callback true
    }


    private val achievementNetworkImpl = AchievementNetworkImpl(uiHandler)
    private val mList: MutableList<TeamMembaerListVO> = ArrayList<TeamMembaerListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: BoardrListAdapter
    private var currentPage = 0L
    private var totalPage: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_team_list, container, false)



        initView(rootView)
        activity?.let { LoadingDialogUtils.show(it) }
        return rootView
    }

    private fun initView(rootView: View) {
        initRecyclerView(rootView)

        rootView.swipe_refresh_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        rootView.swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L
            mAdapter.setEnableLoadMore(false)
            getNewList()
        }

        rootView.fab.setOnClickListener {
            val intent = Intent(this.context, AddTeamActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView(rootView: View) {
        mRecyclerView = rootView.rv
        mAdapter = BoardrListAdapter(R.layout.item_board, mList)
        mRecyclerView.layoutManager = RecyclerViewNoBugLinearLayoutManager(context)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({
            getNewList()
            swipe_refresh_layout.isEnabled = false
        }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(false)
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamMembaerListVO>()
        pageVO.size = 8
        pageVO.currentPage = ++currentPage

        Log.i("PageVO", pageVO.toString())

        achievementNetworkImpl.getTeamMembersList(pageVO)
    }

    private fun setNewData(list: MutableList<TeamMembaerListVO>) {
        mAdapter.addData(list)

        Log.i("totalPage", totalPage.toString())

        if (totalPage != null) {
            if (currentPage >= totalPage!!) {
                mAdapter.loadMoreEnd()
            } else {
                mAdapter.loadMoreComplete()
            }
        }
    }


}