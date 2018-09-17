package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_team.*
import kotlinx.android.synthetic.main.content_team.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.TeamActivityListAdapter
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamActivityListVO
import net.sarasarasa.lifeup.vo.TeamDetailVO
import java.text.SimpleDateFormat
import java.util.*

class TeamActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->
        when (msg.what) {
            NetworkConstants.INVAILD_TOKEN -> {
                ToastUtils.showShortToast(this, "授权失效，请重试")
            }
            200 -> {
                ToastUtils.showShortToast(this, "查询成功")
                if (msg.obj != null) {
                    val teamDetailVO = msg.obj as TeamDetailVO
                    initData(teamDetailVO)
                }
            }
            400 -> {
                if (msg.obj != null) {
                    val pageVO = msg.obj as PageVO<*>
                    val list = pageVO.list as List<TeamActivityListVO>

                    totalPage = pageVO.totalPage

/*                    if(swipe_refresh_layout.isRefreshing)
                        swipe_refresh_layout.isRefreshing = false*/

                    mAdapter.setEnableLoadMore(true)

                    setNewData(list.toMutableList())
                }
            }
            else -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(this, msg.obj.toString())
            }

        }

        return@Callback true
    }

    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)

    private val mList: MutableList<TeamActivityListVO> = ArrayList<TeamActivityListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TeamActivityListAdapter
    private var mTeamId = -1L
    private var currentPage = 0L
    private var totalPage: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val teamId = intent.getLongExtra("teamId", -1)

        if (teamId != -1L)
            teamNetworkImpl.getTeamDetail(teamId)

        mTeamId = teamId

        initView()

    }

    fun initData(teamDetailVO: TeamDetailVO) {

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        // TODO:实现所有数据的填充
        tv_userName.text = teamDetailVO.teamTitle
        tv_teamDesc.text = teamDetailVO.teamDesc
        tv_userDesc.text = "创建者：${teamDetailVO.owner?.nickname}"
        btn_members.setText("成员 | " + teamDetailVO.memberAmount)

        tv_startDateText.text = dateFormat.format(teamDetailVO.nextStartTime)
        tv_finishTimeText.text = "${timeFormat.format(teamDetailVO.nextStartTime)} - ${timeFormat.format(teamDetailVO.nextEndTime)}"
        iv_iconSkillFrist.setImageResource(getAbbrIconDrawable(teamDetailVO.rewardAttrs.getOrNull(0)))
        iv_iconSkillSecond.setImageResource(getAbbrIconDrawable(teamDetailVO.rewardAttrs.getOrNull(1)))
        iv_iconSkillThird.setImageResource(getAbbrIconDrawable(teamDetailVO.rewardAttrs.getOrNull(2)))

        tw_expText.text = "${teamDetailVO.rewardExp.toString()}点"
        tw_repeatText.text = TodoItemConverter.iFrequencyToNormalString(teamDetailVO.teamFreq)
        tw_ddlText.text = dateFormat.format(teamDetailVO.startDate)


        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
        Glide.with(this).asBitmap().load(teamDetailVO.teamHead).apply(requestOptions).into(object : BitmapImageViewTarget(iv_avatar) {
            override fun setResource(resource: Bitmap?) {
                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@TeamActivity.resources, resource)
                circularBitmapDrawable.isCircular = true
                iv_avatar.setImageDrawable(circularBitmapDrawable)
            }
        })

    }

    /** 获得[abbr]属性图标的[Drawable Id] **/
    private fun getAbbrIconDrawable(abbr: String?): Int {
        return TodoItemConverter.strAbbrToDrawableId(abbr)
    }


    private fun initView() {
        initRecyclerView()

        btn_members.setOnClickListener {
            val intent = Intent(this, TeamMemberActivity::class.java)
            intent.putExtra("teamId", mTeamId)
            startActivity(intent)
        }

/*        rootView.swipe_refresh_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        rootView.swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L
            mAdapter.data.clear()
            mAdapter.setEnableLoadMore(false)
            getNewList()
        }*/
    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = TeamActivityListAdapter(R.layout.item_activity, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({ getNewList() }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(false)
/*        mAdapter.setOnItemClickListener { adapter, view, position ->


            val item = adapter.getItem(position) as TeamListVO

            val intent = Intent(this,TeamActivity::class.java)
            intent.putExtra("teamId",item.teamId)

            startActivity(intent)
        }*/
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamActivityListVO>()
        pageVO.size = 5
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        teamNetworkImpl.getTeamActivityList(pageVO, mTeamId)
    }

    private fun setNewData(list: MutableList<TeamActivityListVO>) {
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