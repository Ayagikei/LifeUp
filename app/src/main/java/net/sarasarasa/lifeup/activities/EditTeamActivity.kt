package net.sarasarasa.lifeup.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_edit_team.*
import kotlinx.android.synthetic.main.content_edit_team.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.CHOOSE_PICTURE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.TAKE_PICTURE
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_EDIT_TEAM_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_EDIT_TEAM_SUCCESS
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.network.impl.UploadNetworkImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.TeamEditVO
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException


open class EditTeamActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast(getString(R.string.network_invalid_token))
            }
            NetworkConstants.MSG_UPDATE_AVATAR_SUCCESS -> {
                if (msg.obj != null)
                    newTeamHeadUrl = msg.obj as String

                val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)

                Glide.with(this).asBitmap().load(newTeamHeadUrl).apply(requestOptions).into(object : BitmapImageViewTarget(iv_team_avatar) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@EditTeamActivity.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        iv_team_avatar.setImageDrawable(circularBitmapDrawable)
                    }
                })
            }
            MSG_EDIT_TEAM_SUCCESS -> {
                ToastUtils.showShortToast(getString(R.string.network_edit_team_success))
                finish()
            }
            MSG_EDIT_TEAM_FAILED -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(getString(R.string.network_edit_team_fail) + msg.obj.toString())
            }
            else -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }
        }

        return@Callback true
    }

    protected val todoService = TodoServiceImpl()

    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)
    private val uploadNetworkImpl = UploadNetworkImpl(uiHandler)
    private val userService = UserServiceImpl()

    private var avatarFileName = "teamAvatar.jpg"
    private var avatarOriginFileName = "teamAvatarOrigin.jpg"
    private var newTeamHeadUrl: String = ""

    private var teamTitle: String? = ""
    private var teamDesc: String? = ""
    private var teamHead: String? = null
    private var mTeamId: Long = -1L

    companion object {
        private const val RC_CAMERA = 200
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_team)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        teamTitle = intent.getStringExtra("teamTitle")
        teamDesc = intent.getStringExtra("teamDesc")
        teamHead = intent.getStringExtra("teamHead")
        mTeamId = intent.getLongExtra("teamId", -1L)

        if (mTeamId == -1L) {
            ToastUtils.showShortToast(getString(R.string.edit_team_not_exist))
            finish()
        }
        initView()
    }

    private fun initView() {
        iv_team_avatar.setOnClickListener {
            showChoosePicDialog()
        }

        val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)
        if (!this.isDestroyed)
            Glide.with(this).asBitmap().load(teamHead).apply(requestOptions).into(object : BitmapImageViewTarget(iv_team_avatar) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@EditTeamActivity.resources, resource)
                    circularBitmapDrawable.isCircular = true
                    iv_team_avatar.setImageDrawable(circularBitmapDrawable)
                }
            })

        til_toDoText.editText?.setText(teamTitle)
        til_remark.editText?.setText(teamDesc)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_to_do_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_finish -> {
                if (check()) {
                    editTeam()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** 修改团队的响应 **/
    private fun editTeam() {
        // 将表单转换为对象
        val content = til_toDoText.editText?.text.toString()
        val remark = til_remark.editText?.text.toString()


        val teamEditVO = TeamEditVO()
        with(teamEditVO) {
            teamTitle = content
            teamDesc = remark
            teamHead = newTeamHeadUrl
            teamId = mTeamId
        }

        Log.i("TeamVO", teamEditVO.toString())

        teamNetworkImpl.editTeam(teamEditVO)
        LoadingDialogUtils.show(this)
    }

    /** 提交前对表单进行检测 **/
    protected fun check(): Boolean {
        if (TextUtils.isEmpty(til_toDoText.editText?.text)) {
            til_toDoText.error = getString(R.string.edit_text_empty_error)
            return false
        }

        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * 显示修改图片的对话框
     */
    @AfterPermissionGranted(RC_CAMERA)
    fun showChoosePicDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.team_add_change_head))
        val items = arrayOf(getString(R.string.team_add_choose_local_photo), getString(R.string.team_add_take_photo))
        builder.setNegativeButton(getString(R.string.btn_cancel), null)
        builder.setItems(items) { _, which ->
            when (which) {
                0 // 选择本地照片
                -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    startActivityForResult(intent, CHOOSE_PICTURE)
                }
                1 // 拍照
                -> {
                    val perms = arrayOf(Manifest.permission.CAMERA)

                    if (EasyPermissions.hasPermissions(this, *perms)) {
                        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        val file = getAvatarFile(avatarOriginFileName)

                        if (file.exists())
                            file.delete()

                        val fileUri = getUriByOsVersion(file)

                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        startActivityForResult(openCameraIntent, TAKE_PICTURE)
                    } else {
                        EasyPermissions.requestPermissions(this, getString(R.string.team_add_photo_permission), RC_CAMERA, *perms)
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
                // 对拍照返回的图片进行裁剪处理
                TAKE_PICTURE -> {
                    val imgUriSel = getUriByOsVersion(getAvatarFile(avatarOriginFileName))
                    cutImageByuCrop(imgUriSel)
                }
                // 对在图库选择的图片进行裁剪处理
                CHOOSE_PICTURE -> cutImageByuCrop(data?.data)
                // 上传裁剪成功的文件
                UCrop.REQUEST_CROP -> {
                    data?.let { uploadFile(it) }
                }
                // 输出裁剪
                UCrop.RESULT_ERROR -> {
                    val cropError = data?.let { UCrop.getError(it) }
                    ToastUtils.showShortToast(cropError.toString())
                }

            }
        }
    }

    /**
     * 使用uCrop框架对指定[uri]的文件进行裁剪
     */
    private fun cutImageByuCrop(uri: Uri?) {
        val outputImage = getAvatarFile(avatarFileName)
        val outputUri = Uri.fromFile(outputImage)

        uri?.let {
            UCrop.of(it, outputUri)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(256, 256)
                    .start(this)
        }
    }

    /**
     *  获得指定[filename]的[File]对象
     */
    private fun getAvatarFile(filename: String): File {
        // 使用 APP 内部储存空间
        val appDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath, "Avatar")

        // 这句是使用外部存储空间的
        //val appDir = File(Environment.getExternalStorageDirectory().absolutePath, "LifeUp")

        if (!appDir.exists())
            appDir.mkdir()

        return File(appDir, filename)
    }

    private fun getUriByOsVersion(file: File): Uri {
        val currentApiVersion = android.os.Build.VERSION.SDK_INT
        return if (currentApiVersion < 24) {
            Uri.fromFile(file)
        } else {
            FileProvider.getUriForFile(this, "$packageName.provider", file)
        }
    }


    /**
     * 上传裁剪后的头像
     */
    @Throws(IOException::class)
    fun uploadFile(data: Intent) {
        val file = getAvatarFile(avatarFileName)
        LoadingDialogUtils.show(this)
        uploadNetworkImpl.uploadImages(file)
    }
}
