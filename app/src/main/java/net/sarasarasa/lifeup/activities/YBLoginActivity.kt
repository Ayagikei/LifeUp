package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_yblogin.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_PROFILE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_URL_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_URL_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_YB_LOGIN_CONNECT_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_YB_LOGIN_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_YB_LOGIN_SUCCESS
import net.sarasarasa.lifeup.network.impl.AttributeNetworkImpl
import net.sarasarasa.lifeup.network.impl.LoginNetworkImpl
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils


class YBLoginActivity : AppCompatActivity() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            MSG_URL_SUCCESS -> this.webView.loadUrl(msg.obj as String)
            MSG_URL_FAILED -> {
                layout_error.visibility = View.VISIBLE
                ToastUtils.showShortToast("加载失败，请检查你的网络！")
            }
            MSG_YB_LOGIN_SUCCESS -> {
                LoadingDialogUtils.show(this@YBLoginActivity)
                userNetworkImpl.getUserProfile()
            }
            MSG_YB_LOGIN_FAILED -> {
                ToastUtils.showShortToast("出现错误：" + msg.obj as String)
                this.webView.reload()
            }
            MSG_YB_LOGIN_CONNECT_FAILED -> {
                ToastUtils.showShortToast("注册失败，请重试")
                this.webView.reload()
            }
            MSG_GET_PROFILE_SUCCESS -> {
                LoadingDialogUtils.show(this@YBLoginActivity)
                attributeNetworkImpl.getAttribute()
            }
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("授权失效，请重试")
                this.webView.reload()
            }
            AttributeConstants.MSG_ATTR_GET_FAILED -> {
                ToastUtils.showShortToast("获取信息失败，请重试")
                this.webView.reload()
            }
            AttributeConstants.MSG_ATTR_GET_SUCCESS -> {
                ToastUtils.showShortToast("登陆成功")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {

            }

        }

        return@Callback true
    }

    val loginNetworkImpl = LoginNetworkImpl(uiHandler)
    val userNetworkImpl = UserNetworkImpl(uiHandler)
    val attributeNetworkImpl = AttributeNetworkImpl(uiHandler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yblogin)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        LoadingDialogUtils.show(this)
        loginNetworkImpl.getYBLoginUrl()

        with(webView) {
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE

            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                    if (url != null)
                        if (url.toString().contains("net.sarasarasa.lifeup/redirect?code=")) {
                            Toast.makeText(context, "授权成功，正在注册信息", Toast.LENGTH_LONG).show()

                            val uri = Uri.parse(url)
                            Log.e("CODE", uri.getQueryParameter("code"))

                            LoadingDialogUtils.show(this@YBLoginActivity)
                            loginNetworkImpl.getYBLoginInfo(uri.getQueryParameter("code"))

                            return true
                        }

                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                    Log.e("ErrorTest", request?.url.toString())

                    if (request != null)
                        if (request.url.toString().contains("net.sarasarasa.lifeup/redirect?code=")) {
                            Toast.makeText(context, "授权成功，正在注册信息", Toast.LENGTH_LONG).show()

                            Log.e("CODE", request.url.getQueryParameter("code"))

                            LoadingDialogUtils.show(this@YBLoginActivity)
                            loginNetworkImpl.getYBLoginInfo(request.url.getQueryParameter("code"))

                            return true
                        }

                    //Toast.makeText(context, "授权操作失败，请重试", Toast.LENGTH_LONG).show()
                    return true
                }

            }

        }
    }

    fun retry(view: View) {
        layout_error.visibility = View.INVISIBLE
        loginNetworkImpl.getYBLoginUrl()
    }


}
