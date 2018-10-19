package net.sarasarasa.lifeup.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_team_list.*
import kotlinx.android.synthetic.main.fragment_team_list.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.TeamActivity
import net.sarasarasa.lifeup.adapters.MomentsAdapter
import net.sarasarasa.lifeup.base.RecyclerViewNoBugLinearLayoutManager
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamActivityListVO
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class MomentsFragment : Fragment(), EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
            }
            NetworkConstants.MSG_GET_MOMENTS_SUCCESS -> {
                if (msg.obj != null) {
                    val pageVO = msg.obj as PageVO<*>
                    val list = pageVO.list as List<TeamActivityListVO>

                    totalPage = pageVO.totalPage

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


    private val userNetworkImpl = UserNetworkImpl(uiHandler)
    private val mList: MutableList<TeamActivityListVO> = ArrayList<TeamActivityListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MomentsAdapter
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var mCurrentClickNpl: BGANinePhotoLayout? = null

    companion object {
        private const val PRC_PHOTO_PREVIEW = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_moments_list, container, false)



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

    }

    private fun initRecyclerView(rootView: View) {
        mRecyclerView = rootView.rv
        mAdapter = MomentsAdapter(R.layout.item_activity, mList,this)
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
        mAdapter.setOnItemClickListener { adapter, view, position ->

            val item = adapter.getItem(position) as TeamActivityListVO

            val intent = Intent(context, TeamActivity::class.java)
            intent.putExtra("teamId", item.teamId)

            startActivity(intent)
        }
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamActivityListVO>()
        pageVO.size = 8
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        userNetworkImpl.getMoments(pageVO)
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
        if (context?.let { EasyPermissions.hasPermissions(it, *perms) } == true) {
            val downloadDir = File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerDownload")
            val photoPreviewIntentBuilder = BGAPhotoPreviewActivity.IntentBuilder(context)
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

}