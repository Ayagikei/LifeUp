package net.sarasarasa.lifeup.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.jeff.settingitem.SettingView
import kotlinx.android.synthetic.main.activity_profile.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.CHOOSE_PICTURE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.CROP_SMALL_PICTURE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.TAKE_PICTURE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.USER_SEX_FEMALE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.USER_SEX_MALE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.USER_SEX_SECRET
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_AVATAR_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_PROFILE_SUCCESS
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_UPDATE_SUCCESS
import net.sarasarasa.lifeup.converter.UserConverter
import net.sarasarasa.lifeup.network.impl.UserNetworkImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.PictureUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.ProfileVO
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.*


class ProfileActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            MSG_UPDATE_FAILED -> ToastUtils.showShortToast("网络错误，请稍后重试。")
            MSG_UPDATE_PROFILE_SUCCESS -> {
                ToastUtils.showShortToast("修改成功")
                finish()
            }
            MSG_UPDATE_AVATAR_SUCCESS -> {
                ToastUtils.showShortToast("头像修改成功")
            }
            MSG_UPDATE_AVATAR_FAILED -> {
                ToastUtils.showShortToast("头像修改失败：网络错误，请稍后重试。")
            }
        }

        return@Callback true
    }

    val userService = UserServiceImpl()
    private val userNetwork = UserNetworkImpl(uiHandler)
    private val profileVO = ProfileVO()

    private var tempUri:Uri? = null
    private lateinit var tempFile:File
    private lateinit var mBitmap:Bitmap
    private var fileName = "avatar.jpg"

    companion object {
        private const val RC_CAMERA = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        initView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
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

        sv_avatar.setOnItemViewClick {
            showChoosePicDialog()
        }
    }


    private fun showSexDialog() {
        val checkindex = profileVO.userSex ?: USER_SEX_SECRET
        val items = arrayOf("女", "男", "保密")

        val dialog = AlertDialog.Builder(this).setTitle("设置性别")
                .setSingleChoiceItems(items, checkindex) { dialog, index ->
                    profileVO.userSex = when (items[index]) {
                        "女" -> USER_SEX_FEMALE
                        "男" -> USER_SEX_MALE
                        else -> USER_SEX_SECRET
                    }
                    sv_sex.setItemText(items[index])
                    dialog.dismiss()
                }.create()
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


    /**
     * 显示修改图片的对话框
     */
    @AfterPermissionGranted(RC_CAMERA)
    protected fun showChoosePicDialog() {
        val currentApiVersion = android.os.Build.VERSION.SDK_INT
        val builder = android.app.AlertDialog.Builder(this)

        builder.setTitle("修改头像")
        val items = arrayOf("选择本地照片", "拍照")
        builder.setNegativeButton("取消", null)
        builder.setItems(items) { dialog, which ->
            when (which) {
                0 // 选择本地照片
                -> {
                    val intent = Intent(Intent.ACTION_PICK)//返回被选中项的URI
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")//得到所有图片的URI
                    startActivityForResult(intent, CHOOSE_PICTURE)
                }
                1 // 拍照
                -> {
                    val perms = Manifest.permission.CAMERA

                    if(EasyPermissions.hasPermissions(this,perms)) {
                        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        val appDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath, "Avatar")
                        if(!appDir.exists())
                            appDir.mkdir()

                        val file = File(appDir,fileName)
                        if(file.exists())
                            file.delete()
                        tempFile = file

                        tempUri = if(currentApiVersion < 24) {
                            Uri.fromFile(file)
                        } else{
                            FileProvider.getUriForFile(this, getPackageName() + ".provider", file)
                        }

                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        startActivityForResult(openCameraIntent, TAKE_PICTURE)
                    }
                    else{
                        EasyPermissions.requestPermissions(this,"拍照需要系统摄像头权限授权",RC_CAMERA,perms)
                    }
                }
            }
        }
        builder.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TAKE_PICTURE -> {
                    val imgUriSel = FileProvider.getUriForFile(this, "$packageName.provider", tempFile)
                    cutImage(imgUriSel)
                } // 对图片进行裁剪处理
                CHOOSE_PICTURE -> cutImage(data!!.data) // 对图片进行裁剪处理
                CROP_SMALL_PICTURE -> if (data != null) {
                    try {
                        setImageToView(data) // 让刚才选择裁剪得到的图片显示在界面上
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    /**
     * 裁剪图片方法实现
     */
    protected fun cutImage(uri: Uri?) {
        ToastUtils.showShortToast("heelp")

        if (uri == null) {
            Log.i("tip", "The uri is not exist.")
        }
        tempUri = uri!!
        val intent = Intent("com.android.camera.action.CROP")
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*")
        // 设置裁剪
        intent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 256)
        intent.putExtra("outputY", 256)
        intent.putExtra("return-data", true)

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, CROP_SMALL_PICTURE)
    }

    /**
     * 保存裁剪之后的图片数据
     */
    @Throws(IOException::class)
    protected fun setImageToView(data: Intent) {
        val extras = data.extras
        if (extras != null) {
            mBitmap = extras.getParcelable("data")
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            //mImage.setImageBitmap(mBitmap)//显示图片
        }

        val file = saveFile(mBitmap)

        userNetwork.updateAvatar(file)

    }


    fun saveFile(bitmap: Bitmap): File {
        val appDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath, "Avatar")
        if(!appDir.exists())
            appDir.mkdir()

        val file = File(appDir,fileName)
        if(file.exists())
            file.delete()

        file.createNewFile()

        try{
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
            fos.flush()
            fos.close()
        }catch (e:Exception){
            e.printStackTrace()
        }

        Log.e("file",file.absolutePath)
        return file
    }
}
