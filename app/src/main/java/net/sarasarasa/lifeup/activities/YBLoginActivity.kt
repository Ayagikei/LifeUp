package net.sarasarasa.lifeup.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_yblogin.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.LoginConstants
import net.sarasarasa.lifeup.network.impl.LoginNetworkImpl
import net.sarasarasa.lifeup.utils.ToastUtils


class YBLoginActivity : AppCompatActivity() {


    private val uiHandler: Handler.Callback = Handler.Callback { msg ->
        when (msg.what) {
            LoginConstants.MSG_URL_SUCCESS -> this.webView.loadUrl(msg.obj as String)
            LoginConstants.MSG_URL_FAILED -> {
                layout_error.visibility = View.VISIBLE
                ToastUtils.showShortToast(this, "加载失败，请检查你的网络！")
            }
        }

        return@Callback true
    }
    val loginNetworkImpl = LoginNetworkImpl(uiHandler)

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
                    if (request != null)
                        if (request.url.toString().contains("net.sarasarasa.lifeup/redirect?code=")) {
                            Toast.makeText(context, "授权成功，正在注册信息", Toast.LENGTH_LONG).show()
                            loginNetworkImpl.getYBLoginInfo(request.url.getQueryParameter("code"))

                            return true
                        }

                    return super.shouldOverrideUrlLoading(view, request)
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
