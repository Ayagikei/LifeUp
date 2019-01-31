package net.sarasarasa.lifeup.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.VersionConstants
import net.sarasarasa.lifeup.network.impl.AttributeNetworkImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ClickUtils
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.Pedometer
import net.sarasarasa.lifeup.utils.ToastUtils
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            AttributeConstants.MSG_CONNECT_FAILED -> ToastUtils.showShortToast("网络错误，请稍后重试。")
            AttributeConstants.MSG_ATTR_UPDATE_SUCCESS -> {
                //ToastUtils.showShortToast("数据已同步到云端")
            }
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("授权失效，请重新登录。")
                userService.saveToken("")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            VersionConstants.MSG_NEW_VERSION -> {
                val url = msg.obj as String
                val uri = Uri.parse(url)
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = uri
                startActivity(intent)
            }
            VersionConstants.MSG_NO_NEW_VERSION -> {
                ToastUtils.showShortToast("现在是最新版本了！")
            }
        }

        return@Callback true
    }

    private val attributeNetworkImpl = AttributeNetworkImpl(uiHandler)
    private val todoService = TodoServiceImpl()
    private val userService = UserServiceImpl()
    private val attributeService = AttributeServiceImpl()
    private lateinit var pedometer: Pedometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("options", Context.MODE_PRIVATE)
        val isHideCommunity = sharedPreferences.getBoolean("isHideCommunity", false)

        if (!isHideCommunity)
            setContentView(R.layout.activity_main)
        else setContentView(R.layout.activity_main_without_community)

        pedometer = Pedometer(this)
        todoService.resetAllRemind(applicationContext)
    }

    fun initToolBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_achievement -> {
                val intent = Intent(this, AchievementActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_help -> {
                val url = "http://sarasarasa.net/post/157a032e.html"
                val uri = Uri.parse(url)
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = uri
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun login() {
        if (userService.getToken().isBlank()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun openUserMine() {
        val intent = Intent(this, UserMineActivity::class.java)
        startActivity(intent)
    }

    fun openUser(view: View) {
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
    }

    fun openHistory(view: View) {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    fun openWelcome(view: View) {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }

    fun openSetting(view: View) {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }

    fun openProfile(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun openAchievement(view: View) {
        val intent = Intent(this, AchievementActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        pedometer.register()

        val mine = userService.getMine()
        //设置昵称
        val headLayout = nav_view.getHeaderView(0)

        headLayout.tv_userName.text = mine.nickName
        headLayout.iv_avatar.setOnClickListener {
            login()
        }
        when {
            mine.userAddress == null -> headLayout.tv_userDesc.text = "请点击上方头像登录"
            userService.getToken().isBlank() -> headLayout.tv_userDesc.text = "${mine.userAddress}\n授权失效，请重新登录。"
            else -> {
                headLayout.tv_userDesc.text = mine.userAddress
                headLayout.iv_avatar.setOnClickListener {
                    if (ClickUtils.isNotFastClick()) {
                        ToastUtils.showShortToast(getRandomTips())
                    }
                }
            }
        }

        val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)

        if (!mine.userHead.isNullOrBlank())
            Glide.with(this).asBitmap().load(mine.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(headLayout.iv_avatar) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@MainActivity.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    headLayout.iv_avatar.setImageDrawable(circularBitmapDrawable)
                }
            })
    }

    fun syncData() {
        if (!userService.getToken().isBlank()) {
            //保存数据到云端
            attributeNetworkImpl.updateAttribute(attributeService.getAttributeVO())
        }
    }


    fun getStep(): Float {
        return pedometer.stepCount
    }

    fun getPedometerIsAvailable(): Boolean {
        return pedometer.isAvailable
    }

/*    fun getAchievementView(): AchievementView{
        return achievement_view
    }*/

    // 让菜单同时显示图标和文字
    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        if (menu != null) {
            if (menu.javaClass.simpleName.contentEquals("MenuBuilder")) {
                try {
                    val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
                    method.isAccessible = true
                    method.invoke(menu, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    private fun getRandomTips(): String {
        val random = Random(Calendar.getInstance().timeInMillis)
        return when (random.nextInt(17)) {
            0 -> "Tips：登陆之后才可以访问社区，领取到其他用户建立的团队事项。"
            1 -> "Tips：在「历史」页面可以撤销今天完成的事项。"
            2 -> "Tips：步数统计依赖于计步传感器，每天至少早晚各开一次APP可以提高计步准确度。"
            3 -> "Tips：对于APP的建议和反馈可以通过「邮件」、「Github」、「酷安」等渠道反馈。"
            4 -> "Tips：APP支持桌面小部件，并且支持主题设置哦。"
            5 -> "Tips：社区等功能暂时还不支持Emoji，这是个短时间难以解决的bug。"
            6 -> "Tips：本地事项可以设置目标次数，最后一次的时候能获得额外经验值奖励。"
            7 -> "Tips：现在你可以设置具体的「期限时间」了，和「开始时间」搭配起来，可以设置复杂的事项。"
            8 -> "Tips：目前的提醒依赖于系统的闹钟机制，可能需要后台运行才能正常提醒，最好不要太依赖。"
            9 -> "Tips：在「主页面」的右上角可以进行事项的「时间范围」的筛选和「排序」哦。"
            10 -> "Tips：在「统计」页面可以查看到经验值的收支详情。"
            11 -> "Tips：目前应用内的图标大都来自于 iconfont。如果你有意帮忙重新设计图标，欢迎联系我们。"
            12 -> "Tips：长按「待办事项卡片」可以对事项进行置顶、编辑、移至、放弃和删除等多种操作。"
            13 -> "Tips：如果你是用手环等设备计步的，可以手动输入计步数据。"
            14 -> "Tips：在「历史」页面点击时间，可以删除历史记录，以及将逾期事项修改为完成状态。"
            15 -> "Tips：事项的经验值由「紧张程度」、「困难程度」和「相关属性个数」共同决定。"
            else -> "Tips：在「主页面」的右上角可以切换不同的清单哦。"
        }
    }

    fun notifyNaviDrawerUpdate() {
        try {
            val mine = userService.getMine()
            //设置昵称
            val headLayout = nav_view.getHeaderView(0)

            headLayout.tv_userName.text = mine.nickName
            headLayout.iv_avatar.setOnClickListener {
                login()
            }
            when {
                mine.userAddress == null -> headLayout.tv_userDesc.text = "请点击上方头像登录"
                userService.getToken().isBlank() -> headLayout.tv_userDesc.text = "${mine.userAddress}\n授权失效，请重新登录。"
                else -> {
                    headLayout.tv_userDesc.text = mine.userAddress
                    headLayout.iv_avatar.setOnClickListener {
                        if (ClickUtils.isNotFastClick()) {
                            ToastUtils.showShortToast(getRandomTips())
                        }
                    }
                }
            }

            val requestOptions = RequestOptions.placeholderOf(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)

            if (!mine.userHead.isNullOrBlank())
                Glide.with(this).asBitmap().load(mine.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(headLayout.iv_avatar) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@MainActivity.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        headLayout.iv_avatar.setImageDrawable(circularBitmapDrawable)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
