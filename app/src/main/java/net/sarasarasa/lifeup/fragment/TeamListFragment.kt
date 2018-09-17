package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_team_list.*
import kotlinx.android.synthetic.main.fragment_team_list.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.TeamActivity
import net.sarasarasa.lifeup.adapters.TeamListAdapter
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamListVO

class TeamListFragment : Fragment() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->
        when (msg.what) {
            NetworkConstants.INVAILD_TOKEN -> {
            }
            300 -> {
                if (msg.obj != null) {
                    val pageVO = msg.obj as PageVO<*>
                    val list = pageVO.list as List<TeamListVO>

                    totalPage = pageVO.totalPage

                    if (swipe_refresh_layout.isRefreshing)
                        swipe_refresh_layout.isRefreshing = false

                    mAdapter.setEnableLoadMore(true)

                    setNewData(list.toMutableList())
                }
            }
            else -> {
                if (msg.obj != null) {
                    mAdapter.loadMoreFail()
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



        initView(rootView)
        return rootView
    }

    private fun initView(rootView: View) {
        initRecyclerView(rootView)

        rootView.swipe_refresh_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        rootView.swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L
            mAdapter.data.clear()
            mAdapter.setEnableLoadMore(false)
            getNewList()
        }
    }

    private fun initRecyclerView(rootView: View) {
        mRecyclerView = rootView.rv
        mAdapter = TeamListAdapter(R.layout.item_team, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({ getNewList() }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(false)
        mAdapter.setOnItemClickListener { adapter, view, position ->


            val item = adapter.getItem(position) as TeamListVO

            val intent = Intent(context, TeamActivity::class.java)
            intent.putExtra("teamId", item.teamId)

            startActivity(intent)
        }
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamListVO>()
        pageVO.size = 8
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        teamNetworkImpl.getTeamList(pageVO)
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


}