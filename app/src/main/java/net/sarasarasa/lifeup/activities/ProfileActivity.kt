package net.sarasarasa.lifeup.activities

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.jeff.settingitem.SettingView
import kotlinx.android.synthetic.main.activity_profile.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_SUCCESS
import net.sarasarasa.lifeup.converter.UserConverter
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.ProfileVO


class ProfileActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            MSG_UPDATE_FAILED -> ToastUtils.showShortToast("网络错误，请稍后重试。")
            MSG_UPDATE_SUCCESS -> {
                ToastUtils.showShortToast("修改成功")
                finish()
            }
        }

        return@Callback true
    }

    val userService = UserServiceImpl()
    private val userNetwork = UserNetworkImpl(uiHandler)
    private val profileVO = ProfileVO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_to_do_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                userNetwork.updateUserProfile(profileVO)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val mine = userService.getMine()
        sv_nickname.setItemText("昵称：" + mine.nickName)
        sv_address.setItemText("居住地（学校）:" + mine.userAddress)
        sv_sex.setItemText("性别：" + UserConverter.iSexTostrSex(mine.userSex))
        //sv_phone.setItemText("手机号：" + mine.phone)
        profileVO.nickname = mine.nickName
        profileVO.userAddress = mine.userAddress
        profileVO.userSex = mine.userSex

        sv_nickname.setOnItemViewClick {
            inputDialog(sv_nickname)
        }

        sv_address.setOnItemViewClick {
            inputDialog(sv_address)
        }

        sv_sex.setOnItemViewClick {
            showSexDialog()
        }
    }


    private fun showSexDialog() {
        val checkindex = profileVO.userSex ?: 2
        val items = arrayOf("女", "男", "保密")

        val dialog = AlertDialog.Builder(this).setTitle("设置重复频次")
                .setSingleChoiceItems(items, checkindex, DialogInterface.OnClickListener { dialog, index ->
                    profileVO.userSex = when (items[index]) {
                        "女" -> 0
                        "男" -> 1
                        else -> 2
                    }
                    sv_sex.setItemText(items[index])
                    dialog.dismiss()
                }).create()
        dialog.show()
    }


    private fun inputDialog(view: SettingView) {
        val editText = EditText(this)
        val builder = AlertDialog.Builder(this)
        val title = when (view.id) {
            R.id.sv_nickname -> "设置昵称"
            R.id.sv_address -> "设置居住地（学校）"
            else -> return
        }
        with(builder) {
            setTitle(title)

            setView(editText)

            setPositiveButton("确定") { _, _ ->
                val text = when (view.id) {
                    R.id.sv_nickname -> {
                        profileVO.nickname = editText.text.toString()
                        "昵称:" + editText.text.toString()
                    }
                    R.id.sv_address -> {
                        profileVO.userAddress = editText.text.toString()
                        "居住地（学校）:" + editText.text.toString()
                    }
                    else -> ""
                }
                view.setItemText(text)
            }
            setNegativeButton("取消") { _, _ ->
            }
            show()
        }
    }


}
