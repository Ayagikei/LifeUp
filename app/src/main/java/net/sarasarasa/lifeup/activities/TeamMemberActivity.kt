package net.sarasarasa.lifeup.activities

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_team_member.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.TeamMemberListAdapter
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamMembaerListVO

class TeamMemberActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVAILD_TOKEN -> {
            }
            113 -> {
                if (msg.obj != null) {
                    val pageVO = msg.obj as PageVO<*>
                    val list = pageVO.list as List<TeamMembaerListVO>

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
    private val mList: MutableList<TeamMembaerListVO> = ArrayList<TeamMembaerListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TeamMemberListAdapter
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var mTeamId = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_member)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val teamId = intent.getLongExtra("teamId", -1)


        mTeamId = teamId

        initView()

    }


    private fun initView() {
        initRecyclerView()


        //设置上拉刷新
        swipe_refresh_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L
            mAdapter.data.clear()
            mAdapter.setEnableLoadMore(false)
            getNewList()
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = TeamMemberListAdapter(R.layout.item_team_member, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({ getNewList() }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        mAdapter.isFirstOnly(false)
/*        mAdapter.setOnItemClickListener { adapter, view, position ->


            val item = adapter.getItem(position) as TeamListVO

            val intent = Intent(this, TeamActivity::class.java)
            intent.putExtra("teamId", item.teamId)

            startActivity(intent)
        }*/
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamMembaerListVO>()
        pageVO.size = 10
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        teamNetworkImpl.getTeamMembersList(pageVO, mTeamId)
    }

    private fun setNewData(list: MutableList<TeamMembaerListVO>) {
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
