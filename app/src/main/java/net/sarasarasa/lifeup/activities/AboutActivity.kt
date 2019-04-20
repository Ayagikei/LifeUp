package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.didikee.donate.AlipayDonate
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
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

                MaterialDialog(this).show {
                    title(text = "${getString(R.string.about_new_version)} ${versionVO.versionName}")
                    message(text = "${getString(R.string.about_update_content_title)}：\n ${versionVO.versionDesc}")
                    positiveButton(R.string.btn_update) {
                        val uri = Uri.parse(versionVO.downloadUrl)
                        val intent = Intent()
                        intent.action = "android.intent.action.VIEW"
                        intent.data = uri
                        startActivity(intent)
                    }
                    negativeButton(R.string.btn_cancel)
                    lifecycleOwner(this@AboutActivity)
                }

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
        elementCheckUpdate.apply {
            iconDrawable = R.drawable.ic_system_update_alt_24px
            title = getString(R.string.about_check_update)
            setOnClickListener {
                versionNetworkImpl.checkUpdate(VersionUtil.getLocalVersion(applicationContext))
            }
        }


        val elementDonate = Element()
        elementDonate.apply {
            iconDrawable = R.drawable.ic_favorite_border_black_24dp
            title = getString(R.string.about_donate_element)
            setOnClickListener {
                donateAlipay("tsx06992twgztmrmcisibbd")
            }
        }


        val elementRate = Element()
        elementRate.apply {
            iconDrawable = R.drawable.ic_favorite_border_black_24dp
            title = getString(R.string.about_rate_us_element)
            setOnClickListener {
                intentToRate()
            }
        }

        val elementIconDesigner = Element()
        elementIconDesigner.apply {
            iconDrawable = R.drawable.ic_account_circle_black_24dp
            title = getString(R.string.about_icon_designer_element)
        }


        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_remake_round)
                .setDescription(getString(R.string.about_app_description))
                .addItem(Element().setTitle("${getString(R.string.about_version_name)} v${VersionUtil.getLocalVersionName(this)}"))
                .addItem(elementCheckUpdate)
                .addWebsite("https://green-android.org/", getString(R.string.about_green_android_website))
                .addItem(elementRate)
                .addItem(elementDonate)
                .addGroup(getString(R.string.contact_title))
                .addEmail("AyagiKei@163.com")
                .addWebsite("https://www.coolapk.com/apk/net.sarasarasa.lifeup")
                .addGitHub("AyagiKei", getString(R.string.github_desc1))
                .addGitHub("hdonghong", getString(R.string.github_desc2))
                .addItem(elementIconDesigner)
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

    private fun intentToRate() {
        try {
            val uri = Uri.parse("market://details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            ToastUtils.showShortToast(getString(R.string.about_not_found_android_store))
            e.printStackTrace()
        }

    }
}