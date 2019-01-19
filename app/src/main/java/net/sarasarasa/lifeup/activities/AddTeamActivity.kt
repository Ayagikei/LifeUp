package net.sarasarasa.lifeup.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.design.widget.TextInputEditText
import android.support.v4.content.FileProvider
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_add_team.*
import kotlinx.android.synthetic.main.content_add_team.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.CHOOSE_PICTURE
import net.sarasarasa.lifeup.constants.CommonConstants.Companion.TAKE_PICTURE
import net.sarasarasa.lifeup.constants.NetworkConstants
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_ADD_TEAM_FAILED
import net.sarasarasa.lifeup.constants.NetworkConstants.Companion.MSG_ADD_TEAM_SUCCESS
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.SELECTED_CNT
import net.sarasarasa.lifeup.converter.ExpRewardConverter
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.network.impl.TeamNetworkImpl
import net.sarasarasa.lifeup.network.impl.UploadNetworkImpl
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.LoadingDialogUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.vo.TeamTaskVO
import net.sarasarasa.lifeup.vo.TeamVO
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


open class AddTeamActivity : AppCompatActivity() {

    private val uiHandler: Handler.Callback = Handler.Callback { msg ->

        LoadingDialogUtils.dismiss()

        when (msg.what) {
            NetworkConstants.INVALID_TOKEN -> {
                ToastUtils.showShortToast("授权失效，请重试")
            }
            MSG_ADD_TEAM_SUCCESS -> {
                ToastUtils.showShortToast("新建团队成功")
                val teamTaskVO = msg.obj as TeamTaskVO
                val intent = Intent(this, TeamActivity::class.java)
                intent.putExtra("teamId", teamTaskVO.teamId)
                startActivity(intent)
                finish()
            }
            NetworkConstants.MSG_UPDATE_AVATAR_SUCCESS -> {
                if (msg.obj != null) {
                    newTeamHeadUrl = msg.obj as String
                }

                val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)

                Glide.with(this).asBitmap().load(newTeamHeadUrl).apply(requestOptions).into(object : BitmapImageViewTarget(iv_team_avatar) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@AddTeamActivity.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        iv_team_avatar.setImageDrawable(circularBitmapDrawable)
                    }
                })
            }
            MSG_ADD_TEAM_FAILED -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast("创建团队失败：" + msg.obj.toString())
            }
            else -> {
                if (msg.obj != null)
                    ToastUtils.showShortToast(msg.obj.toString())
            }
        }

        return@Callback true
    }

    protected val todoService = TodoServiceImpl()
    protected var iCheckedItemIndex = 0
    protected var iUrgency = 0
    protected var iDifficulty = 0
    protected var arrAbbrBtn: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)

    private val teamNetworkImpl = TeamNetworkImpl(uiHandler)
    private val uploadNetworkImpl = UploadNetworkImpl(uiHandler)
    private val userService = UserServiceImpl()

    private var avatarFileName = "teamAvatar.jpg"
    private var avatarOriginFileName = "teamAvatarOrigin.jpg"
    private var newTeamHeadUrl: String? = userService.getMine().userHead

    val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val simpleDateTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

    companion object {
        private const val RC_CAMERA = 200
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_team)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()

    }

    private fun initView() {
        iCheckedItemIndex = 0
        initDDDL()
        initRepeater()
        initSeekBar()
        initAbbrBtn()

        iv_team_avatar.setOnClickListener {
            showChoosePicDialog()
        }

        val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_pic_loading).error(R.drawable.ic_pic_error)
        val mine = userService.getMine()
        Glide.with(this).asBitmap().load(mine.userHead).apply(requestOptions).into(object : BitmapImageViewTarget(iv_team_avatar) {
            override fun setResource(resource: Bitmap?) {
                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(this@AddTeamActivity.resources, resource)
                circularBitmapDrawable.isCircular = true
                iv_team_avatar.setImageDrawable(circularBitmapDrawable)
            }
        })
    }

    /** 将技能图标初始化为灰色 **/
    private fun initAbbrBtn() {
        val cm = ColorMatrix()
        cm.setSaturation(0f) // 设置饱和度
        val grayColorFilter = ColorMatrixColorFilter(cm)


        iv_strength.colorFilter = grayColorFilter // 如果想恢复彩色显示，设置为null即可
        iv_learning.colorFilter = grayColorFilter
        iv_charm.colorFilter = grayColorFilter
        iv_endurance.colorFilter = grayColorFilter
        iv_vitality.colorFilter = grayColorFilter
        iv_creative.colorFilter = grayColorFilter
    }


    /** 属性按钮选择的相关响应 **/
    fun switchBtn(view: View) {
        val index = TodoItemConverter.viewToIndex(view)
        //当前选中的话
        if (arrAbbrBtn[index] == ToDoItemConstants.SELECTED) {
            val cm = ColorMatrix()
            cm.setSaturation(0f) // 设置饱和度
            val grayColorFilter = ColorMatrixColorFilter(cm)

            if (view is ImageView)
                view.colorFilter = grayColorFilter

            arrAbbrBtn[index] = ToDoItemConstants.UNSELECTED
            arrAbbrBtn[ToDoItemConstants.SELECTED_CNT]--
        } else {
            //不能选择超过3个
            if (arrAbbrBtn[ToDoItemConstants.SELECTED_CNT] < ToDoItemConstants.MAX_SELECTABLE_NUM) {
                //当前没有选中，恢复颜色
                if (view is ImageView)
                    view.colorFilter = null

                arrAbbrBtn[index] = ToDoItemConstants.SELECTED
                arrAbbrBtn[ToDoItemConstants.SELECTED_CNT]++
            }
        }
    }

    /** 初始化奖励设置的两个SeekBar **/
    private fun initSeekBar() {
        sb_urgence.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                //四个刻度对应的数值为0，33,66,100
                iUrgency = leftValue.toInt()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                //start tracking touch
            }

            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                //stop tracking touch
            }
        })

        sb_difficulty.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                //四个刻度对应的数值为0，33,66,99
                iDifficulty = leftValue.toInt()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                //start tracking touch
            }

            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                //stop tracking touch
            }
        })
    }

    /** 初始化日期选择 **/
    private fun initDDDL() {

        til_remindTime.editText?.setText("00:00:00")
        til_startTimeEnd.editText?.setText("23:59:59")

        //禁用输入法输入，下同
        et_expire_time.inputType = InputType.TYPE_NULL
        //第一次点击首先响应Focus，下同
        et_expire_time.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showDatePickerDialog()
        }

        et_expire_time.setOnClickListener {
            showDatePickerDialog()
        }

        et_remindDate.inputType = InputType.TYPE_NULL
        et_remindDate.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showRemindDatePickerDialog()
        }

        et_remindDate.setOnClickListener {
            showRemindDatePickerDialog()
        }

        et_startTime.inputType = InputType.TYPE_NULL
        et_startTime.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                showRemindTimePickerDialog(view, false)
        }

        et_startTime.setOnClickListener {
            showRemindTimePickerDialog(it, false)
        }

        et_startTimeEnd.inputType = InputType.TYPE_NULL
        et_startTimeEnd.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                showRemindTimePickerDialog(view, true)
        }

        et_startTimeEnd.setOnClickListener {
            showRemindTimePickerDialog(it, true)
        }

    }

    /** 初始化重复频次选择 **/
    private fun initRepeater() {
        et_repeat.inputType = InputType.TYPE_NULL
        et_repeat.setText("不重复")
        et_repeat.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showRepeaterDialog()
        }

        et_repeat.setOnClickListener { showRepeaterDialog(); }
    }

    /** 重置期限日期 **/
    fun finishDateReset(view: View) {
        et_expire_time.setText("")
        view.visibility = View.INVISIBLE
    }

    /** 重置提醒日期 **/
    fun finishRemindReset(view: View) {
        et_remindDate.setText("")
        et_startTime.setText("")
        //使重置按钮[不可见]
        view.visibility = View.INVISIBLE
    }

    /**
     * 展示期限日期选择对话框
     */
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val strMonthOfYear: String = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
            val strDayOfMonth: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()

            et_expire_time.setText("$year/$strMonthOfYear/$strDayOfMonth")
            til_repeat.visibility = View.VISIBLE
            btn_ddl_reset.visibility = View.VISIBLE

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //最小日期限制
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    /**
     * 展示提醒日期选择对话框
     */
    @SuppressLint("SetTextI18n")
    private fun showRemindDatePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val strMonthOfYear: String = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
            val strDayOfMonth: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()

            et_remindDate.setText("$year/$strMonthOfYear/$strDayOfMonth")
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //最小日期限制
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    /**
     * 展示提醒时间选择对话框
     */
    @SuppressLint("SetTextI18n")
    private fun showRemindTimePickerDialog(view: View, isMaxSec: Boolean) {
        val c = Calendar.getInstance()
        val datePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            val strHourOfDay: String = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            val strMinute: String = if (minute < 10) "0$minute" else minute.toString()

            if (view is TextInputEditText)
                if (isMaxSec) {
                    view.setText("$strHourOfDay:$strMinute:59")
                } else {
                    view.setText("$strHourOfDay:$strMinute:00")
                }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)


        datePickerDialog.show()
    }

    /**
     * 展示重复频次选择对话框
     */
    private fun showRepeaterDialog() {
        val items = arrayOf("不重复", "每日", "每两日", "每周", "每两周", "每月")

        val dialog = AlertDialog.Builder(this).setTitle("设置重复频次")
                .setSingleChoiceItems(items, iCheckedItemIndex) { dialog, index ->
                    iCheckedItemIndex = index
                    et_repeat.setText(items[index])
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_to_do_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                if (check()) {
                    getItem()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** 创建新团队的响应 **/
    private fun getItem() {
        // 将表单转换为对象
        val content = til_toDoText.editText?.text.toString()
        val remark = til_remark.editText?.text.toString()

        val taskDeadline = til_deadLine.editText?.text.toString()
        var dateStartDate: Date? = null
        if (!taskDeadline.isBlank()) {
            dateStartDate = simpleDateTimeFormat.parse("$taskDeadline 00:00:00")

        }

        val taskRemindDateAndTime = til_remindDate.editText?.text.toString() + " " + til_remindTime.editText?.text.toString()
        var dateFirstStartTime: Date? = null
        if (!taskRemindDateAndTime.isBlank()) {
            dateFirstStartTime = simpleDateTimeFormat.parse(taskRemindDateAndTime)
        }

        val taskRemindDateAndTimeEnd = til_remindDate.editText?.text.toString() + " " + til_startTimeEnd.editText?.text.toString()
        var dateFirstEndTime: Date? = null
        if (!taskRemindDateAndTime.isBlank()) {
            dateFirstEndTime = simpleDateTimeFormat.parse(taskRemindDateAndTimeEnd)
        }

        val taskUrgencyLevel = when (iUrgency) {
            0 -> 1
            33 -> 2
            66 -> 3
            99 -> 4
            else -> 0
        }
        val taskDifficultyLevel = when (iDifficulty) {
            0 -> 1
            33 -> 2
            66 -> 3
            99 -> 4
            else -> 0
        }

        val taskFrequency = when (til_repeat.editText?.text.toString()) {
            "不重复" -> 0
            "每日" -> 1
            "每两日" -> 2
            "每周" -> 7
            "每两周" -> 14
            "每月" -> 30
            else -> 0
        }


        val teamVO = TeamVO()
        with(teamVO) {
            teamTitle = content
            teamDesc = remark
            teamFreq = taskFrequency
            rewardExp = ExpRewardConverter.getExpReward(arrAbbrBtn[SELECTED_CNT], taskUrgencyLevel, taskDifficultyLevel)
            startDate = dateStartDate
            firstStartTime = dateFirstStartTime
            firstEndTime = dateFirstEndTime
            teamHead = newTeamHeadUrl

            for (i in arrAbbrBtn.indices) {
                if (i == 0) continue
                if (arrAbbrBtn[i] == ToDoItemConstants.SELECTED) {
                    val strRes = TodoItemConverter.indexToString(i)
                    rewardAttrs.add(strRes)
                }
            }

        }

        Log.i("TeamVO", teamVO.toString())

        teamNetworkImpl.addTeam(teamVO)
        LoadingDialogUtils.show(this)
    }

    /** 提交前对表单进行检测 **/
    protected fun check(): Boolean {

        if (TextUtils.isEmpty(til_toDoText.editText?.text)) {
            til_toDoText.error = "不能为空"
            return false
        }

        if (TextUtils.isEmpty(til_remindTime.editText?.text)) {
            til_remindTime.error = "不能为空"
            return false
        }

        if (TextUtils.isEmpty(til_remindDate.editText?.text)) {
            til_remindDate.error = "不能为空"
            return false
        }

        if (TextUtils.isEmpty(til_startTimeEnd.editText?.text)) {
            til_startTimeEnd.error = "不能为空"
            return false
        }

        if (TextUtils.isEmpty(til_deadLine.editText?.text)) {
            til_deadLine.error = "不能为空"
            return false
        }

        if (arrAbbrBtn[SELECTED_CNT] == 0) {
            ToastUtils.showShortToast("你至少需要选择一个相关属性！")
            return false
        }

        if ((TextUtils.isEmpty(til_remindDate.editText?.text) && !TextUtils.isEmpty(til_remindTime.editText?.text))
                || (!TextUtils.isEmpty(til_remindDate.editText?.text) && TextUtils.isEmpty(til_remindTime.editText?.text))) {
            ToastUtils.showShortToast("提醒日期和时间必须填写完整！")
            return false
        }

        val taskRemindDateAndTime = til_remindDate.editText?.text.toString() + " " + til_remindTime.editText?.text.toString()
        var dateFirstStartTime: Date? = null
        if (!taskRemindDateAndTime.isBlank()) {
            dateFirstStartTime = simpleDateTimeFormat.parse(taskRemindDateAndTime)
        }



        val taskRemindDateAndTimeEnd = til_remindDate.editText?.text.toString() + " " + til_startTimeEnd.editText?.text.toString()
        var dateFirstEndTime: Date? = null
        if (!taskRemindDateAndTime.isBlank()) {
            dateFirstEndTime = simpleDateTimeFormat.parse(taskRemindDateAndTimeEnd)
        }

        if (dateFirstEndTime != null) {
            if (dateFirstEndTime.before(dateFirstStartTime)) {
                til_startTimeEnd.error = "结束时间必须晚于开始时间"
                til_startTimeEnd.requestFocus()
                return false
            }
        }


        return true
    }

    fun showDialogAttribution(view: View) {
        val dialog = AlertDialog.Builder(this).setView(R.layout.dialog_abbr_desc).setTitle("属性值介绍").create()

        with(dialog) {
            this.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "确定") { _, _ ->
                cancel()
            }
            this.show()

        }
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
        builder.setTitle("修改头像")
        val items = arrayOf("选择本地照片", "拍照")
        builder.setNegativeButton("取消", null)
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
                        EasyPermissions.requestPermissions(this, "拍照需要系统摄像头权限授权", RC_CAMERA, *perms)
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
            FileProvider.getUriForFile(this, packageName + ".provider", file)
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
