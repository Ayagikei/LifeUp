package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.VersionConstants
import net.sarasarasa.lifeup.network.impl.VersionNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.VersionUtil

class AboutActivity : AppCompatActivity() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            AttributeConstants.MSG_CONNECT_FAILED -> ToastUtils.showShortToast("网络错误，请稍后重试。")
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


    private val versionNetworkImpl = VersionNetworkImpl(uiHandler)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)*/

        val elementCheckUpdate = Element()
        elementCheckUpdate.iconDrawable = R.drawable.ic_system_update_alt_24px
        elementCheckUpdate.title = "检查更新"
        elementCheckUpdate.setOnClickListener {
            versionNetworkImpl.checkUpdate(VersionUtil.getLocalVersion(applicationContext))
        }

        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription("一款「人生游戏化」的待办事项管理应用。\n" +
                        "用「游戏化」的形式让你的人生更加美好。")
                .addItem(Element().setTitle("版本号 v1.30"))
                .addItem(elementCheckUpdate)
                .addGroup("联系")
                .addEmail("AyagiKei@163.com")
                .addWebsite("https://www.pgyer.com/LifeUp")
                .addGitHub("AyagiKei")
                .create()

        setContentView(aboutPage)
    }
}