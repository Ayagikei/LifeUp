package net.sarasarasa.lifeup.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_team_list.view.*
import kotlinx.android.synthetic.main.head_view_moments.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.activities.UserActivity
import net.sarasarasa.lifeup.adapters.MomentsAdapter
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.base.RecyclerViewNoBugLinearLayoutManager
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamActivityListVO
import java.io.File
import java.lang.ref.WeakReference

class
MomentsFragment : Fragment(), BGANinePhotoLayout.Delegate {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
            }
            NetworkConstants.MSG_GET_MOMENTS_SUCCESS -> {
                if (msg.obj != null) {
                    try {
                        val pageVO = msg.obj as PageVO<*>

                        if (pageVO.list != null) {
                            val list = pageVO.list as List<TeamActivityListVO>

                            totalPage = pageVO.totalPage

                            if (rootView.swipe_refresh_layout != null)
                                if (rootView.swipe_refresh_layout.isRefreshing) {
                                    rootView.swipe_refresh_layout.isRefreshing = false
                                    mAdapter.data.clear()
                                }

                            setNewData(list.toMutableList())

                            if (rootView.swipe_refresh_layout != null)
                                rootView.swipe_refresh_layout.isEnabled = true

                            mAdapter.setEnableLoadMore(true)
                            mAdapter.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        ToastUtils.showShortToast(e.toString())
                    }
                }
            }
            AttributeConstants.MSG_CONNECT_FAILED -> {
                if (rootView.swipe_refresh_layout != null && rootView.swipe_refresh_layout.isRefreshing)
                    rootView.swipe_refresh_layout.isRefreshing = false

                mAdapter.loadMoreFail()

                ToastUtils.showShortToast("网络错误，请稍后重试。")
            }
            else -> {
                if (msg.obj != null) {
                    mAdapter.loadMoreFail()

                    if (rootView.swipe_refresh_layout != null)
                        rootView.swipe_refresh_layout.isEnabled = true
                }
            }

        }

        return@Callback true
    }


    private val userNetworkImpl = UserNetworkImpl(uiHandler)
    private val mList: MutableList<TeamActivityListVO> = ArrayList<TeamActivityListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MomentsAdapter
    private lateinit var rootView: View
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var mCurrentClickNpl: BGANinePhotoLayout? = null
    private var isGetAll = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_moments_list, container, false)

        initView(rootView)
        activity?.let { LoadingDialogUtils.show(WeakReference(it)) }
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingDialogUtils.dismissAndClearReference()
    }

    private fun initView(rootView: View) {
        initRecyclerView(rootView)

        context?.let { rootView.swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(it, R.color.colorPrimary)) }
        rootView.swipe_refresh_layout.setOnRefreshListener {
            currentPage = 0L
            mAdapter.setEnableLoadMore(false)
            getNewList()
        }

    }

    private fun getHeaderView(): View {
        val headerView = layoutInflater.inflate(R.layout.head_view_moments, null)
        headerView.button_all.setOnClickListener {
            if (context != null) {
                headerView.button_all.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                headerView.button_all.setBackgroundColor(ContextCompat.getColor(context!!, R.color.blue))
                headerView.button_follow.setTextColor(ContextCompat.getColor(context!!, R.color.blue))
                headerView.button_follow.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            }

            mAdapter.setEnableLoadMore(false)
            isGetAll = true
            if (rootView.swipe_refresh_layout != null)
                rootView.swipe_refresh_layout.isEnabled = false
            currentPage = 0L
            mAdapter.data.clear()
            getNewList()
        }
        headerView.button_follow.setOnClickListener {
            if (context != null) {
                headerView.button_all.setTextColor(ContextCompat.getColor(context!!, R.color.blue))
                headerView.button_all.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
                headerView.button_follow.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                headerView.button_follow.setBackgroundColor(ContextCompat.getColor(context!!, R.color.blue))
            }

            mAdapter.setEnableLoadMore(false)
            isGetAll = false
            if (rootView.swipe_refresh_layout != null)
                rootView.swipe_refresh_layout.isEnabled = false
            currentPage = 0L
            mAdapter.data.clear()
            getNewList()
        }
        return headerView
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
            rootView.swipe_refresh_layout.isEnabled = false
        }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(true)
        mAdapter.setOnItemClickListener { adapter, view, position ->

            val item = adapter.getItem(position) as? TeamActivityListVO
            if (item != null) {
                val intent = Intent(context, UserActivity::class.java)
                intent.putExtra("userId", item.userId)
                startActivity(intent)
            }
        }
        mAdapter.setHeaderView(getHeaderView())
    }

    private fun getNewList() {
        val pageVO = PageVO<TeamActivityListVO>()
        pageVO.size = 30
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        userNetworkImpl.getMoments(pageVO, isGetAll)
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

    override fun onClickNinePhotoItem(ninePhotoLayout: BGANinePhotoLayout, view: View, position: Int, model: String, models: MutableList<String>) {
        mCurrentClickNpl = ninePhotoLayout
        photoPreviewWrapper()
    }

    /**
     * 图片预览
     */
    private fun photoPreviewWrapper() {

        if (mCurrentClickNpl == null) {
            return
        }

        val downloadDir = File(LifeUpApplication.getLifeUpApplication().externalMediaDirs[0], "LifeUp")

        val photoPreviewIntentBuilder = BGAPhotoPreviewActivity.IntentBuilder(context)
                .saveImgDir(LifeUpApplication.getLifeUpApplication().externalMediaDirs[0]) // 保存图片的目录，如果传 null，则没有保存图片功能

        if (mCurrentClickNpl!!.itemCount == 1) {
            // 预览单张图片
            photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl!!.currentClickItem)
        } else if (mCurrentClickNpl!!.itemCount > 1) {
            // 预览多张图片
            photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl!!.data)
                    .currentPosition(mCurrentClickNpl!!.currentClickItemPosition) // 当前预览图片的索引
        }
        startActivity(photoPreviewIntentBuilder.build())

    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            if (rootView.swipe_refresh_layout != null)
                rootView.swipe_refresh_layout.isRefreshing = true
            currentPage = 0L
            mAdapter.setEnableLoadMore(false)
            mAdapter.data.clear()
            getNewList()
        }
        super.onHiddenChanged(hidden)
    }
}