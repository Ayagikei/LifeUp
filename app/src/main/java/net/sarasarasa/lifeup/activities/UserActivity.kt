package net.sarasarasa.lifeup.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_user.*
import kotlinx.android.synthetic.main.foot_view_to_do.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.UserActivityListAdapter
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.FOLLOWER_MEMBER
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.FOLLOWING_MEMBER
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_ACTIVITIES_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_USER_DETAIL_SUCCESS
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.fragment.TeamListFragment
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.PageVO
import net.sarasarasa.lifeup.vo.TeamActivityListVO
import net.sarasarasa.lifeup.vo.UserDetailVO
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, BGANinePhotoLayout.Delegate {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("授权失效，请重试")
            }
            MSG_GET_USER_DETAIL_SUCCESS -> {
                if (msg.obj != null) {
                    val userDetailVO = msg.obj as UserDetailVO
                    initData(userDetailVO)
                }
            }
            MSG_GET_USER_ACTIVITIES_SUCCESS -> {
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
                    ToastUtils.showShortToast(msg.obj.toString())
            }

        }

        return@Callback true
    }

    private val userNetworkImpl = UserNetworkImpl(uiHandler)
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val mList: MutableList<TeamActivityListVO> = ArrayList<TeamActivityListVO>().toMutableList()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: UserActivityListAdapter
    private var mUserId = -1L
    private var currentPage = 0L
    private var totalPage: Long? = null
    private var mCurrentClickNpl: BGANinePhotoLayout? = null

    companion object {
        private const val PRC_PHOTO_PREVIEW = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


/*        val viewPager = container
        val tabs = tabs
        viewPager.adapter = mSectionsPagerAdapter


        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))*/


        val userId = intent.getLongExtra("userId", -1)

        LoadingDialogUtils.show(this)
        userNetworkImpl.getUserDetail(userId)


        mUserId = userId

        initView()
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return TeamListFragment()
            //return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }


    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_comm, container, false)

            return rootView
        }

        companion object {

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                fragment.arguments = args
                return fragment
            }
        }
    }


    fun initData(userDetailVO: UserDetailVO) {

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        // TODO:实现所有数据的填充
        tv_userName.text = userDetailVO.nickname
        tv_teamAmount.text = userDetailVO.teamAmount.toString()
        tv_followingAmount.text = userDetailVO.followingAmount.toString()
        tv_followerAmount.text = userDetailVO.followerAmount.toString()

        tv_teamAmount.setOnClickListener {
            val intent = Intent(this, UserTeamActivity::class.java)
            intent.putExtra("userId", mUserId)
            startActivity(intent)
        }

        tv_followingAmount.setOnClickListener {
            val intent = Intent(this, TeamMemberActivity::class.java)
            intent.putExtra("typeId", mUserId)
                    .putExtra("memberType", FOLLOWING_MEMBER)
            startActivity(intent)
        }

        tv_followerAmount.setOnClickListener {
            val intent = Intent(this, TeamMemberActivity::class.java)
            intent.putExtra("typeId", mUserId)
                    .putExtra("memberType", FOLLOWER_MEMBER)
            startActivity(intent)
        }

        if (mUserId == -1L) {
            btn_sign_next.visibility = View.VISIBLE
            btn_sign_next.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        } else {
            btn_sign_next.visibility = View.GONE
        }


        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)
        Glide.with(this).asBitmap().load(userDetailVO.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(iv_avatar) {
            override fun setResource(resource: Bitmap?) {
                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@UserActivity.resources, resource)
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
    }

    private fun initRecyclerView() {
        mRecyclerView = rv
        mAdapter = UserActivityListAdapter(R.layout.item_user_activity, mList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //mRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        mRecyclerView.adapter = mAdapter
        getNewList()
        mAdapter.setOnLoadMoreListener({ getNewList() }, mRecyclerView)
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        mAdapter.isFirstOnly(true)
        mAdapter.emptyView = getEmptyView()

    }

    private fun getNewList() {
        val pageVO = PageVO<TeamActivityListVO>()
        pageVO.size = 5
        pageVO.currentPage = ++currentPage
        Log.i("PageVO", pageVO.toString())

        userNetworkImpl.getUserActivities(pageVO, mUserId)
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

    private fun getEmptyView(): View {
        val view = layoutInflater.inflate(R.layout.foot_view_to_do, null)
        view.textView11.text = "该用户暂时没有动态"
        return view
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
            val downloadDir = File(Environment.getExternalStorageDirectory(), "LifeUp")
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


}