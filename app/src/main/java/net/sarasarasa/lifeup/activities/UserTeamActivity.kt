package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_user_team.*
import kotlinx.android.synthetic.main.foot_view_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.TeamListAdapter
import net.sarasarasa.lifeup.base.RecyclerViewNoBugLinearLayoutManager
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_TEAM_LIST_SUCCESS
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamListVO

class UserTeamActivity : AppCompatActivity() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
            }
            MSG_GET_USER_TEAM_LIST_SUCCESS -> {
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
            AttributeConstants.MSG_CONNECT_FAILED -> {
                if (swipe_refresh_layout.isRefreshing)
                    swipe_refresh_layout.isRefreshing = false

                mAdapter.loadMoreFail()

                ToastUtils.showShortToast("网络错误，请稍后重试。")
            }
            else -> {
                if (msg.obj != null) {
                    mAdapter.loadMoreFail()
                }
            }

        }

        return@Callback true
    }


    private val userNetworkImpl = UserNetworkImpl(uiHandler)
    private val mList: MutableList<TeamListVO> = ArrayList<TeamListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TeamListAdapter
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var mUserId = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_team)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val userId = intent.getLongExtra("userId", -1)
        LoadingDialogUtils.show(this)

        mUserId = userId

        initView()
    }


    private fun initView() {
        initRecyclerView()

        swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))
        swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L
            mAdapter.data.clear()
            mAdapter.setEnableLoadMore(false)
            getNewList()
        }


    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = TeamListAdapter(R.layout.item_team, mList)
        mRecyclerView.layoutManager = RecyclerViewNoBugLinearLayoutManager(this)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({ getNewList() }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(true)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as TeamListVO

            val intent = Intent(this, TeamActivity::class.java)
            intent.putExtra("teamId", item.teamId)

            startActivity(intent)
        }
        mAdapter.emptyView = getEmptyView()
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamListVO>()
        pageVO.size = 8
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        userNetworkImpl.getUserTeamList(pageVO, mUserId)
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

    private fun getEmptyView(): View {
        val view = layoutInflater.inflate(R.layout.foot_view_to_do, null)
        view.textView11.text = "该用户暂时没有团队"
        return view
    }
}