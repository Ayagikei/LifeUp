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
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
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
            AttributeConstants.MSG_CONNECT_FAILED -> ToastUtils.showShortToast(getString(R.string.network_connect_error))
            AttributeConstants.MSG_ATTR_UPDATE_SUCCESS -> {
                //ToastUtils.showShortToast("数据已同步到云端")
            }
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast(getString(R.string.network_invalid_token))
                userService.saveToken("")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        return@Callback true
    }

    private val attributeNetworkImpl = AttributeNetworkImpl(uiHandler)
    private val todoService = TodoServiceImpl()
    private val userService = UserServiceImpl()
    private val attributeService = AttributeServiceImpl()
    private lateinit var pedometer: Pedometer
    private var currentToolbar: Toolbar? = null
    private val sharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isHideCommunity = sharedPreferences.getBoolean("isHideCommunity", false)
        if (!isHideCommunity)
            setContentView(R.layout.activity_main)
        else setContentView(R.layout.activity_main_without_community)

        pedometer = Pedometer(this)
        todoService.resetAllRemind(applicationContext)
    }

    fun initToolBar(toolbar: Toolbar) {
        //setSupportActionBar(toolbar)

        currentToolbar = toolbar
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun getCurrentToolbar(): Toolbar? {
        return currentToolbar
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


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
            mine.userAddress == null -> headLayout.tv_userDesc.text = getString(R.string.main_drawer_login_head)
            userService.getToken().isBlank() -> headLayout.tv_userDesc.text = "${mine.userAddress}\n${getString(R.string.main_drawer_need_to_relogin)}"
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
        val isHidePedometer = sharedPreferences.getBoolean("isHidePedometer", false)
        if (isHidePedometer) return 0f

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
            0 -> getString(R.string.main_tips_1)
            1 -> getString(R.string.main_tips_2)
            2 -> getString(R.string.main_tips_3)
            3 -> getString(R.string.main_tips_4)
            4 -> getString(R.string.main_tips_5)
            5 -> getString(R.string.main_tips_6)
            6 -> getString(R.string.main_tips_7)
            7 -> getString(R.string.main_tips_8)
            8 -> getString(R.string.main_tips_9)
            9 -> getString(R.string.main_tips_10)
            10 -> getString(R.string.main_tips_11)
            11 -> getString(R.string.main_tips_12)
            12 -> getString(R.string.main_tips_13)
            13 -> getString(R.string.main_tips_14)
            14 -> getString(R.string.main_tips_15)
            15 -> getString(R.string.main_tips_16)
            else -> getString(R.string.main_tips_17)
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
                mine.userAddress == null -> headLayout.tv_userDesc.text = getString(R.string.main_drawer_login_head)
                userService.getToken().isBlank() -> headLayout.tv_userDesc.text = "${mine.userAddress}\n${getString(R.string.main_drawer_need_to_relogin)}"
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
