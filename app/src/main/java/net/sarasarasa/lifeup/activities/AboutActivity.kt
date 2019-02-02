package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.didikee.donate.AlipayDonate
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
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
import net.sarasarasa.lifeup.vo.VersionVO


class AboutActivity : AppCompatActivity() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            AttributeConstants.MSG_CONNECT_FAILED -> ToastUtils.showShortToast(getString(R.string.network_error))
            VersionConstants.MSG_NEW_VERSION -> {
                val versionVO = msg.obj as VersionVO

                AlertDialog.Builder(this).setTitle("${getString(R.string.about_new_version)} ${versionVO.versionName}")
                        .setMessage("${getString(R.string.about_update_content_title)}：\n ${versionVO.versionDesc}")
                        .setPositiveButton(getString(R.string.btn_update)) { _, _ ->
                            val uri = Uri.parse(versionVO.downloadUrl)
                            val intent = Intent()
                            intent.action = "android.intent.action.VIEW"
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.btn_cancel)) { _, _ ->
                        }.show()
            }
            VersionConstants.MSG_NO_NEW_VERSION -> {
                ToastUtils.showShortToast(getString(R.string.has_been_latest_version))
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
        elementCheckUpdate.title = getString(R.string.about_check_update)
        elementCheckUpdate.setOnClickListener {
            versionNetworkImpl.checkUpdate(VersionUtil.getLocalVersion(applicationContext))
        }

        val elementDonate = Element()
        elementDonate.iconDrawable = R.drawable.ic_favorite_border_black_24dp
        elementDonate.title = "捐赠支持一下开发者\n谢谢:)"
        elementDonate.setOnClickListener {
            donateAlipay("tsx06992twgztmrmcisibbd")
        }

        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.about_app_description))
                .addItem(Element().setTitle("${getString(R.string.about_version_name)} v1.53"))
                .addItem(elementCheckUpdate)
                .addItem(elementDonate)
                .addGroup(getString(R.string.contact_title))
                .addEmail("AyagiKei@163.com")
                .addWebsite("https://www.coolapk.com/apk/net.sarasarasa.lifeup")
                .addGitHub("AyagiKei", getString(R.string.github_desc1))
                .addGitHub("hdonghong", getString(R.string.github_desc2))
                .create()

        setContentView(aboutPage)
    }

    /**
     * 支付宝支付
     * @param payCode 收款码后面的字符串
     * 注：不区分大小写
     */
    private fun donateAlipay(payCode: String) {
        val hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(this)
        if (hasInstalledAlipayClient) {
            AlipayDonate.startAlipayClient(this, payCode)
        }
    }
}