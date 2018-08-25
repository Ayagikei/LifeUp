package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.AttributeNetworkImpl
import net.sarasarasa.lifeup.service.impl.AttributeServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->
        when (msg.what) {
            AttributeConstants.MSG_CONNECT_FAILED -> ToastUtils.showShortToast(this, "网络错误，请稍后重试。")
            AttributeConstants.MSG_ATTR_UPDATE_SUCCESS -> {
                ToastUtils.showShortToast(this, "数据已同步到云端")
            }
            NetworkConstants.INVAILD_TOKEN -> {
                ToastUtils.showShortToast(this, "授权失效，请重新登陆。")
                userService.saveToken("")
                val intent = Intent(this, YBLoginActivity::class.java)
                startActivity(intent)
            }
        }

        return@Callback true
    }

    val attributeNetworkImpl = AttributeNetworkImpl(uiHandler)
    val userService = UserServiceImpl()
    val attributeService = AttributeServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, YBLoginActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_achievement -> {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_about -> {
                val intent = Intent(this, AddTeamActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun login() {
        if (userService.getToken().isBlank()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        val mine = userService.getMine()
        //设置昵称
        val headLayout = nav_view.getHeaderView(0)

        headLayout.tv_userName.text = mine.nickName
        headLayout.iv_avatar.setOnClickListener { login() }
        when {
            mine.userAddress == null -> headLayout.tv_userDesc.text = "请点击上方头像登陆"
            userService.getToken().isBlank() -> headLayout.tv_userDesc.text = "mine.userAddress \n 授权失效，请重新登陆。"
            else -> {
                headLayout.tv_userDesc.text = mine.userAddress
                headLayout.iv_avatar.setOnClickListener { openProfile() }
            }
        }
    }

    fun syncData() {
        if (!userService.getToken().isBlank()) {
            //保存数据到云端
            attributeNetworkImpl.updateAttribute(attributeService.getAttributeVO())
        }
    }

}
