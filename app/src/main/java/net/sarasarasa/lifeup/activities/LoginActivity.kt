package net.sarasarasa.lifeup.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import com.tencent.connect.UserInfo
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_sign_up.view.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.AttributeConstants
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_GET_PROFILE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_PHONE_REGISTER_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_QQ_LOGIN_SUCCESS
import net.sarasarasa.lifeup.instance.RetrofitInstance.Companion.gson
import net.sarasarasa.lifeup.network.impl.AttributeNetworkImpl
import net.sarasarasa.lifeup.network.impl.LoginNetworkImpl
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.MD5Util
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.QQLoginVO
import net.sarasarasa.lifeup.vo.QQUserInfoVO
import net.sarasarasa.lifeup.vo.SignUpVO
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            MSG_QQ_LOGIN_SUCCESS -> {
                LoadingDialogUtils.show(this@LoginActivity)
                userNetworkImpl.getUserProfile()
            }
            MSG_GET_PROFILE_SUCCESS -> {
                LoadingDialogUtils.show(this@LoginActivity)
                attributeNetworkImpl.getAttribute()
            }
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast(getString(R.string.network_invalid_token))
            }
            AttributeConstants.MSG_ATTR_GET_FAILED -> {
                ToastUtils.showShortToast(getString(R.string.network_attr_get_fail))
            }
            AttributeConstants.MSG_ATTR_GET_SUCCESS -> {
                ToastUtils.showShortToast(getString(R.string.network_attr_get_success))
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            MSG_PHONE_REGISTER_SUCCESS -> {
                LoadingDialogUtils.show(this@LoginActivity)
                userNetworkImpl.getUserProfile()
            }
            NetworkConstants.MSG_QQ_LOGIN_FAILED -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }
            else -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }

        }

        return@Callback true
    }


    private var mAuthTask: UserLoginTask? = null
    private val mTencent = Tencent.createInstance("101492659", LifeUpApplication.getLifeUpApplication())
    private val loginUiListener = LoginUiListener()
    private val getUserInfoListener = GetUserInfoUiListener()
    private val userNetworkImpl = UserNetworkImpl(uiHandler)
    private val attributeNetworkImpl = AttributeNetworkImpl(uiHandler)
    private val loginNetworkImpl = LoginNetworkImpl(uiHandler)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(login_toolbar)

/*        // Set up the login form.
        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })*/

        email_sign_in_button.setOnClickListener { attemptLogin() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
/*        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(phone, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }*/
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    private fun attemptLogin() {

        // Reset errors.
        phone.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = phone.text.toString()
        val passwordStr = password.text.toString()


        val signUpVO = SignUpVO()

        with(signUpVO) {
            authIdentifier = emailStr
            accessToken = MD5Util.encryption("lIFEuP" + passwordStr)
            authType = "phone"
        }

        LoadingDialogUtils.show(this@LoginActivity)
        loginNetworkImpl.loginByPhone(signUpVO)

    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        phone.setAdapter(adapter)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }

            return DUMMY_CREDENTIALS
                    .map { it.split(":") }
                    .firstOrNull { it[0] == mEmail }
                    ?.let {
                        // Account exists, return true if the password matches.
                        it[1] == mPassword
                    } != false
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {
                finish()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }

/*    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return true
    }*/

    fun loginByYB(view: View) {
        val intent = Intent(this, YBLoginActivity::class.java)
        startActivity(intent)
    }

    fun loginByQQ(view: View) {
        mTencent.login(this, "get_user_info", loginUiListener)
    }

    fun signUp(view: View) {
        //sendCode(this, false)
    }

/*
    */
    /** Mob短信验证集成 **//*
    fun sendCode(context: Context, isLoginBySMS: Boolean) {
        val page = RegisterPage()
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null)
        page.setRegisterCallback(object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    val phoneMap = data as HashMap<String, Any>
                    val country = phoneMap["country"] as String // 国家代码，如“86”
                    val phone = phoneMap["phone"] as String // 手机号码，如“13800138000”

                    // TODO 利用国家代码和手机号码进行后续的操作

                    if (isLoginBySMS) {
                        val mobVO = MobVO()
                        mobVO.phone = phone
                        mobVO.zone = country

                        LoadingDialogUtils.show(this@LoginActivity)
                        loginNetworkImpl.loginOrSignUpBySMS(mobVO)
                        //mobVO.code = page.contentView.findViewById<>()
                    } else inputDialog(phone)


                } else {
                    // TODO 处理错误的结果
                }
            }
        })
        page.show(context)
    }*/

    private fun inputDialog(phone: String) {

        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_sign_up, null)

        with(builder) {
            setTitle(getString(R.string.login_sign_up_information))

            setView(view)

            setPositiveButton(getString(R.string.btn_yes)) { _, _ ->
                val signUpVO = SignUpVO()

                with(signUpVO) {
                    nickname = view.til_nickname.editText?.text.toString()
                    accessToken = MD5Util.encryption("lIFEuP" + view.til_password.editText?.text.toString())
                    authIdentifier = phone
                    authType = "phone"
                }

                LoadingDialogUtils.show(this@LoginActivity)
                loginNetworkImpl.registerByPhone(signUpVO)
            }

            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Tencent.onActivityResultData(requestCode, resultCode, data, loginUiListener)

        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE ||
                    resultCode == Constants.REQUEST_QZONE_SHARE ||
                    resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, getUserInfoListener)
            }
        }
    }

    fun tencentGetUserInfo() {
        //尝试获取信息
        val userInfo = UserInfo(this, mTencent.qqToken)
        userInfo.getUserInfo(getUserInfoListener)
    }


    inner class LoginUiListener : IUiListener {
        override fun onComplete(p0: Any?) {
            ToastUtils.showShortToast(getString(R.string.login_complete_qq_auth))

            //取得token和openid
            val res = gson.fromJson(p0.toString(), QQLoginVO::class.java)
            Log.i("json", res.toString())
            mTencent.setAccessToken(res.access_token, res.expires_time.toString())
            mTencent.openId = res.openid.toString()

            //进行获取信息操作
            tencentGetUserInfo()
        }

        override fun onCancel() {
            ToastUtils.showShortToast(getString(R.string.login_cancel_qq_auth))
        }

        override fun onError(p0: UiError?) {
            ToastUtils.showShortToast(getString(R.string.login_exception_qq_auth) + p0.toString())
        }

    }

    inner class GetUserInfoUiListener : IUiListener {
        override fun onComplete(p0: Any?) {
            val res = gson.fromJson(p0.toString(), QQUserInfoVO::class.java)

            val signUpVO = SignUpVO()

            with(signUpVO) {
                authIdentifier = mTencent.openId
                authType = "qq"
                nickname = if (res.nickname.isNullOrEmpty()) {
                    getQQDefaultNickname()
                } else {
                    res.nickname
                }
                userAddress = res.city

                userHead = if (res.figureurl_qq_2?.isNotEmpty() == true) {
                    res.figureurl_qq_2
                } else {
                    res.figureurl_qq_1
                }
                userSex = when (res.gender) {
                    "女" -> 0
                    "男" -> 1
                    else -> 2
                }
            }

            LoadingDialogUtils.show(this@LoginActivity)
            loginNetworkImpl.loginOrSignUpByQQ(signUpVO)
        }

        override fun onCancel() {
            ToastUtils.showShortToast(getString(R.string.login_cancel_qq_information))

            val signUpVO = SignUpVO()
            with(signUpVO) {
                authIdentifier = mTencent.openId
                authType = "qq"
                nickname = getQQDefaultNickname()
                userSex = 2
            }

            LoadingDialogUtils.show(this@LoginActivity)
            loginNetworkImpl.loginOrSignUpByQQ(signUpVO)
        }

        override fun onError(p0: UiError?) {
            ToastUtils.showShortToast(getString(R.string.login_exception_qq_information) + p0.toString())

            val signUpVO = SignUpVO()
            with(signUpVO) {
                authIdentifier = mTencent.openId
                authType = "qq"
                nickname = getQQDefaultNickname()
                userSex = 2
            }

            LoadingDialogUtils.show(this@LoginActivity)
            loginNetworkImpl.loginOrSignUpByQQ(signUpVO)
        }


    }

    private fun getQQDefaultNickname(): String {
        return "QQ用户_" + MD5Util.encryption(Calendar.getInstance().toString()).substring(0, 10)
    }

/*    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return true
    }*/

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_login_by_sms -> {
                sendCode(this, true)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }*/
}
