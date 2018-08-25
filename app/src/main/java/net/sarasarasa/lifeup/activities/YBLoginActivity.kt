package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.graphics.Bitmap
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
import net.sarasarasa.lifeup.constants.LoginConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.network.impl.AttributeNetworkImpl
import net.sarasarasa.lifeup.network.impl.LoginNetworkImpl
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.ToastUtils


class YBLoginActivity : AppCompatActivity() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->
        when (msg.what) {
            LoginConstants.MSG_URL_SUCCESS -> this.webView.loadUrl(msg.obj as String)
            LoginConstants.MSG_URL_FAILED -> {
                layout_error.visibility = View.VISIBLE
                ToastUtils.showShortToast(this, "加载失败，请检查你的网络！")
            }
            LoginConstants.MSG_YB_LOGIN_SUCCESS -> {
                userNetworkImpl.getUserProfile()
            }
            LoginConstants.MSG_YB_LOGIN_FAILED -> {
                ToastUtils.showShortToast(this, "出现错误：" + msg.obj as String)
                this.webView.reload()
            }
            LoginConstants.MSG_YB_LOGIN_CONNECT_FAILED -> {
                ToastUtils.showShortToast(this, "注册失败，请重试")
                this.webView.reload()
            }
            LoginConstants.MSG_GET_PROFILE_SUCCESS -> {
                attributeNetworkImpl.getAttribute()
            }
            NetworkConstants.INVAILD_TOKEN -> {
                ToastUtils.showShortToast(this, "授权失效，请重试")
                this.webView.reload()
            }
            AttributeConstants.MSG_ATTR_GET_FAILED -> {
                ToastUtils.showShortToast(this, "获取信息失败，请重试")
                this.webView.reload()
            }
            AttributeConstants.MSG_ATTR_GET_SUCCESS -> {
                ToastUtils.showShortToast(this, "登陆成功")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
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

        loginNetworkImpl.getYBLoginUrl()

        with(webView) {
            settings.javaScriptEnabled = true;
            settings.cacheMode = WebSettings.LOAD_NO_CACHE

            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    Log.e("URL", request?.url.toString())

                    if (request != null)
                        if (request.url.toString().contains("net.sarasarasa.lifeup/redirect?code=")) {
                            Toast.makeText(context, "授权成功，正在注册信息", Toast.LENGTH_LONG).show()

                            Log.e("CODE", request.url.getQueryParameter("code"))
                            loginNetworkImpl.getYBLoginInfo(request.url.getQueryParameter("code"))

                            return true
                        }

                    //Toast.makeText(context, "授权操作失败，请重试", Toast.LENGTH_LONG).show()
                    return true
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                }
            }

        }
    }

    fun retry(view: View) {
        layout_error.visibility = View.INVISIBLE
        loginNetworkImpl.getYBLoginUrl()
    }


}
