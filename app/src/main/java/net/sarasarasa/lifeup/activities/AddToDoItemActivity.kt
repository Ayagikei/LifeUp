package net.sarasarasa.lifeup.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.activity_add_to_do_item.*
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.SELECTED_CNT
import net.sarasarasa.lifeup.converter.ExpRewardConverter
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.DensityUtil
import net.sarasarasa.lifeup.utils.ToastUtils
import java.text.SimpleDateFormat
import java.util.*


open class AddToDoItemActivity : AppCompatActivity() {

    protected val todoService = TodoServiceImpl()
    protected var iCheckedItemIndex = 0
    protected var iUrgency = 0
    protected var iDifficulty = 0
    protected var arrAbbrBtn: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_do_item)
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
        //禁用输入法输入，下同
        dDDL.inputType = InputType.TYPE_NULL
        //第一次点击首先响应Focus，下同
        dDDL.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showDatePickerDialog()
        }

        dDDL.setOnClickListener {
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
        et_startTime.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showRemindTimePickerDialog()
        }

        et_startTime.setOnClickListener {
            showRemindTimePickerDialog()
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
        dDDL.setText("")
        view.visibility = View.INVISIBLE
        til_repeat.visibility = View.INVISIBLE

        val set = ConstraintSet()
        set.clone(layout_extra)
        set.connect(switch1.id, ConstraintSet.TOP, til_deadLine.id, ConstraintSet.BOTTOM, DensityUtil.dp2px(this, 8f))
        set.applyTo(layout_extra)
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
            dDDL.setText("$year/${monthOfYear + 1}/$dayOfMonth")
            til_repeat.visibility = View.VISIBLE
            btn_ddl_reset.visibility = View.VISIBLE

            //显示重复
            val set = ConstraintSet()
            set.clone(layout_extra)
            set.connect(switch1.id, ConstraintSet.TOP, til_repeat.id, ConstraintSet.BOTTOM, DensityUtil.dp2px(this, 8f))
            set.applyTo(layout_extra)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //最小日期限制
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    /**
     * 展示提醒日期选择对话框
     */
    private fun showRemindDatePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            et_remindDate.setText("$year/${monthOfYear + 1}/$dayOfMonth")
            btn_remind_reset.visibility = View.VISIBLE
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //最小日期限制
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    /**
     * 展示提醒时间选择对话框
     */
    private fun showRemindTimePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            et_startTime.setText("${hourOfDay}:${minute}:00")
            btn_remind_reset.visibility = View.VISIBLE
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        datePickerDialog.show()
    }

    /**
     * 展示重复频次选择对话框
     */
    private fun showRepeaterDialog() {
        val items = arrayOf("不重复", "每日", "每两日", "每周", "每两周", "每月")

        val dialog = AlertDialog.Builder(this).setTitle("设置重复频次")
                .setSingleChoiceItems(items, iCheckedItemIndex, DialogInterface.OnClickListener { dialog, index ->
                    iCheckedItemIndex = index
                    et_repeat.setText(items[index])
                    dialog.dismiss()
                }).create()
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
                    addItem(getItem())
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** 创建新待办事项的响应 **/
    protected fun getItem(): TaskModel {
        // 将表单转换为对象
        val content = til_toDoText.editText?.text.toString()
        val remark = til_remark.editText?.text.toString()
        // 转换为Date类型
        val taskDeadline = til_deadLine.editText?.text.toString()
        var dateTaskDeadline: Date? = null
        if (!taskDeadline.isBlank()) {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            dateTaskDeadline = simpleDateFormat.parse(taskDeadline)
        }

        val taskRemindDateAndTime = til_remindDate.editText?.text.toString() + " " + til_remindTime.editText?.text.toString()
        var dateTaskRemindDateAndTime: Date? = null
        if (!taskRemindDateAndTime.isBlank()) {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            dateTaskRemindDateAndTime = simpleDateFormat.parse(taskRemindDateAndTime)
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
        val taskShared = switch1.isChecked
        var relatedAttribute = arrayOf<String>()

        val taskFrequency = when (til_repeat.editText?.text.toString()) {
            "不重复" -> 0
            "每日" -> 1
            "每两日" -> 2
            "每周" -> 7
            "每两周" -> 14
            "每月" -> 30
            else -> 0
        }

        for (i in arrAbbrBtn.indices) {
            if (i == 0) continue
            if (arrAbbrBtn[i] == ToDoItemConstants.SELECTED) {
                val strRes = TodoItemConverter.indexToString(i)
                relatedAttribute = relatedAttribute.plusElement(strRes)
            }
        }

        val taskModel = TaskModel(
                content,
                remark,
                dateTaskDeadline,
                dateTaskRemindDateAndTime,
                relatedAttribute.getOrElse(0) { "" },
                relatedAttribute.getOrElse(1) { "" },
                relatedAttribute.getOrElse(2) { "" },
                taskUrgencyLevel,
                taskDifficultyLevel,
                taskFrequency,
                0,
                taskShared,
                null
        )

        //设置默认开始时间为当天0点
        val cal = Calendar.getInstance()
        with(cal) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        taskModel.expReward = ExpRewardConverter.getExpReward(arrAbbrBtn[SELECTED_CNT], taskUrgencyLevel, taskDifficultyLevel)
        return taskModel
    }

    private fun addItem(taskModel: TaskModel) {

        val id = todoService.addTodoItem(taskModel)

        //设置提醒
        if (taskModel.taskRemindTime != null && id != null) {
            todoService.setOrUpdateAlarm(taskModel.taskRemindTime!!.time, id, this)
            ToastUtils.showShortToast("提醒设置成功！")
        }

        //结束这个Activity
        finish()
    }

    /** 提交前对表单进行检测 **/
    protected fun check(): Boolean {
        var isAllCheckPassed = true

        if (TextUtils.isEmpty(til_toDoText.editText?.text)) {
            til_toDoText.error = "不能为空"
            isAllCheckPassed = false
        }

        if (arrAbbrBtn[SELECTED_CNT] == 0) {
            ToastUtils.showShortToast("你至少需要选择一个相关属性！")
            isAllCheckPassed = false
        }

        if ((TextUtils.isEmpty(til_remindDate.editText?.text) && !TextUtils.isEmpty(til_remindTime.editText?.text))
                || (!TextUtils.isEmpty(til_remindDate.editText?.text) && TextUtils.isEmpty(til_remindTime.editText?.text))) {
            ToastUtils.showShortToast("提醒日期和时间必须填写完整！")
            isAllCheckPassed = false
        }

        return isAllCheckPassed
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
}
