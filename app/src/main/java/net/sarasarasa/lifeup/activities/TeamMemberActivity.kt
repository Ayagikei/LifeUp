package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_team_member.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.TeamMemberListAdapter
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.FOLLOWER_MEMBER
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.FOLLOWING_MEMBER
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.TEAM_MEMBER
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_MEMBER_LIST_SUCCESS
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.USER_ME
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamMembaerListVO
import java.util.*

class TeamMemberActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
            }
            MSG_GET_TEAM_MEMBER_LIST_SUCCESS -> {
                if (msg.obj != null) {
                    val pageVO = msg.obj as PageVO<*>
                    val list = pageVO.list as List<TeamMembaerListVO>

                    totalPage = pageVO.totalPage

                    if (swipe_refresh_layout.isRefreshing) {
                        swipe_refresh_layout.isRefreshing = false
                        mAdapter.data.clear()
                    }

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
    private val userNetworkImpl = UserNetworkImpl(uiHandler)
    private val mList: MutableList<TeamMembaerListVO> = ArrayList<TeamMembaerListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TeamMemberListAdapter
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var typeId = -1L
    private var mMemberType = TEAM_MEMBER


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_member)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        typeId = intent.getLongExtra("typeId", USER_ME)
        mMemberType = intent.getLongExtra("memberType", TEAM_MEMBER)

        initView()
    }


    private fun initView() {
        initRecyclerView()


        //设置上拉刷新
        swipe_refresh_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L

            mAdapter.setEnableLoadMore(false)
            getNewList()
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = TeamMemberListAdapter(R.layout.item_team_member, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        getNewList()
        mAdapter.setOnLoadMoreListener({ getNewList() }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        mAdapter.isFirstOnly(false)

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as TeamMembaerListVO

            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("userId", item.userId)

            startActivity(intent)
        }


        mAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->


            val item = adapter?.getItem(position) as TeamMembaerListVO
            val mView = view as AppCompatButton

            if (item.isFollow == 0) {
                item.userId?.let { userNetworkImpl.followUserById(it) }
                if (mMemberType == FOLLOWER_MEMBER) {
                    mView.text = "互相关注"
                } else {
                    mView.text = "已关注"
                }
                mView.isClickable = false
                item.isFollow = 1
                val colorStateList = ContextCompat.getColorStateList(this, R.color.clicked_btn)
                ViewCompat.setBackgroundTintList(mView, colorStateList)
            } else if (item.isFollow == 1) {
                item.userId?.let { userNetworkImpl.unfollowUserById(it) }
                mView.text = "关注"
                mView.isClickable = true
                item.isFollow = 0
                val colorStateList = ContextCompat.getColorStateList(this, R.color.blue)
                ViewCompat.setBackgroundTintList(mView, colorStateList)
            }


            val timer = Timer()
            val timerTask = object : TimerTask() {
                override fun run() {
                    mView.isClickable = true
                }
            }

            timer.schedule(timerTask, 1500)
        }


        mRecyclerView.adapter = mAdapter

    }


    private fun getNewList() {
        val pageVO = PageVO<TeamMembaerListVO>()
        pageVO.size = 10
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        when (mMemberType) {
            TEAM_MEMBER -> teamNetworkImpl.getTeamMembersList(pageVO, typeId)
            FOLLOWER_MEMBER -> userNetworkImpl.getUserFollower(pageVO, typeId)
            FOLLOWING_MEMBER -> userNetworkImpl.getUserFollowing(pageVO, typeId)
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }
}
