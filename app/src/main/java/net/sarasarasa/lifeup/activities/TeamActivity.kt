package net.sarasarasa.lifeup.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_team.*
import kotlinx.android.synthetic.main.content_team.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.TeamActivityListAdapter
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_NEXT_TEAM_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_REPORT_TYPE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_TEAM_DETAIL_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_JOIN_TEAM_SUCCESS
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.network.impl.ReportNetworkImpl
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TeamActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("授权失效，请重试")
            }
            MSG_GET_TEAM_DETAIL_SUCCESS -> {
                if (msg.obj != null) {
                    val teamDetailVO = msg.obj as TeamDetailVO
                    mTeamDetailVO = teamDetailVO
                    initData(teamDetailVO)
                }
            }
            MSG_JOIN_TEAM_SUCCESS -> {
                ToastUtils.showShortToast("加入成功")
            }
            MSG_GET_TEAM_ACTIVITIES_SUCCESS -> {
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
            MSG_GET_NEXT_TEAM_ACTIVITIES_SUCCESS -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }
            MSG_GET_REPORT_TYPE_SUCCESS ->{
                if(msg.obj != null){
                    val returnList = msg.obj as ArrayList<ReportTypeVO>

                    showReportDialog(returnList)
                }
            }
            else -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }

        }

        return@Callback true
    }

    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)
    private val reportNetworkImpl = ReportNetworkImpl(uiHandler)

    private val mList: MutableList<TeamActivityListVO> = ArrayList<TeamActivityListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TeamActivityListAdapter
    private var mTeamDetailVO = TeamDetailVO()
    private var mTeamId = -1L
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var mCurrentClickNpl: BGANinePhotoLayout? = null

    companion object {
        private const val PRC_PHOTO_PREVIEW = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val teamId = intent.getLongExtra("teamId", -1)

        if (teamId != -1L) {
            LoadingDialogUtils.show(this)
            teamNetworkImpl.getTeamDetail(teamId)
        }

        mTeamId = teamId

        initView()

    }

    fun initData(teamDetailVO: TeamDetailVO) {

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        tv_userName.text = teamDetailVO.teamTitle
        tv_teamDesc.text = teamDetailVO.teamDesc
        tv_userDesc.text = "${teamDetailVO.owner?.nickname}"
        btn_members.setText("成员 | " + teamDetailVO.memberAmount)

        val nowCal = Calendar.getInstance()
        val ddl = Calendar.getInstance()
        ddl.time = teamDetailVO.startDate
        ddl.add(Calendar.DATE, 1)
        ddl.set(Calendar.HOUR_OF_DAY, 0)
        ddl.set(Calendar.MINUTE, 0)
        ddl.set(Calendar.SECOND, 0)

        if (nowCal.after(ddl)) {
            btn_join.isEnabled = false
            btn_join.setText("已截止")
        } else {
            if (teamDetailVO.isMember != 0) {
                btn_join.isEnabled = false
                btn_join.setText("已加入")
            } else {
                btn_join.setOnClickListener {
                    teamNetworkImpl.joinTheTeam(teamDetailVO)
                    btn_join.isEnabled = false
                    btn_join.setText("已加入")

                    val memberAmount = teamDetailVO.memberAmount?.plus(1)
                    btn_members.setText("成员 | " + memberAmount)
                }
            }

        }

        if (teamDetailVO.isMember != 0) {
            btn_sign_next.setOnClickListener {
                teamNetworkImpl.getNextTeamTask(mTeamId)
                it.isEnabled = false
                btn_sign_next.setText("已领取")
            }
            btn_sign_next.isEnabled = true
            btn_sign_next.setText("领取")
        } else {
            btn_sign_next.isEnabled = false
            btn_sign_next.setText("不可领取")
        }


        if (teamDetailVO.nextStartTime != null && teamDetailVO.nextEndTime != null) {
            tv_startDateText.text = dateFormat.format(teamDetailVO.nextStartTime)
            tv_finishTimeText.text = "${timeFormat.format(teamDetailVO.nextStartTime
                    ?: "已经结束")} - ${timeFormat.format(teamDetailVO.nextEndTime ?: "")}"
        } else {
            tv_startDateText.text = "已经结束"
            tv_finishTimeText.text = "已经结束"

            btn_sign_next.isEnabled = false
            btn_sign_next.setText("已结束")
        }

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
            intent.putExtra("typeId", mTeamId)
                    .putExtra("memberType", 0)
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
        mAdapter.isFirstOnly(true)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_team, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_report -> {
                reportNetworkImpl.getReportType()
                return true
            }
            R.id.action_quit -> {
                ToastUtils.showShortToast("此功能暂不可用！")
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }

        }
        return true
    }



    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PRC_PHOTO_PREVIEW) {
            ToastUtils.showShortToast("您拒绝了「图片预览」所需要的相关权限!")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onClickNinePhotoItem(ninePhotoLayout: BGANinePhotoLayout, view: View, position: Int, model: String, models: MutableList<String>) {
        mCurrentClickNpl = ninePhotoLayout
        photoPreviewWrapper()
    }

    /**
     * 图片预览，兼容6.0动态权限
     */
    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private fun photoPreviewWrapper() {

        if (mCurrentClickNpl == null) {
            return
        }

        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            val downloadDir = File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerDownload")
            val photoPreviewIntentBuilder = BGAPhotoPreviewActivity.IntentBuilder(this)
                    .saveImgDir(downloadDir) // 保存图片的目录，如果传 null，则没有保存图片功能

            if (mCurrentClickNpl!!.itemCount == 1) {
                // 预览单张图片
                photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl!!.currentClickItem)
            } else if (mCurrentClickNpl!!.itemCount > 1) {
                // 预览多张图片
                photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl!!.data)
                        .currentPosition(mCurrentClickNpl!!.currentClickItemPosition) // 当前预览图片的索引
            }
            startActivity(photoPreviewIntentBuilder.build())
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", PRC_PHOTO_PREVIEW, *perms)
        }
    }

    private fun showReportDialog(reportTypeList :ArrayList<ReportTypeVO>) {
        var arrTypeName = emptyArray<String>()

        for(reportTypeVO in reportTypeList)
            arrTypeName = arrTypeName.plus(reportTypeVO.typeName.toString())

        val reportDetailVO = ReportDetailVO()
        val dialog = AlertDialog.Builder(this).setTitle("举报")
                .setSingleChoiceItems(arrTypeName, 0) { dialog, index ->

                    with(reportDetailVO){
                        criminalUserId = mTeamDetailVO.owner?.userId
                        itemId = mTeamId
                        reportItem = "team"
                        reportTypeId = index.toLong() + 1
                    }

                }.setPositiveButton("确定") { _, _ ->
                    reportNetworkImpl.report(reportDetailVO)
                }.create()
        dialog.show()
    }


}