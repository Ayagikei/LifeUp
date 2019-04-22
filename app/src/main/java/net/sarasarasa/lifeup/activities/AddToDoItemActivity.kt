package net.sarasarasa.lifeup.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.activity_add_to_do_item.*
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.dialog_ignore.view.*
import kotlinx.android.synthetic.main.dialog_repeat.view.*
import mehdi.sakout.fancybuttons.FancyButton
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.adapters.CategoryAdapter
import net.sarasarasa.lifeup.application.LifeUpApplication
import net.sarasarasa.lifeup.constants.ToDoItemConstants
import net.sarasarasa.lifeup.constants.ToDoItemConstants.Companion.SELECTED_CNT
import net.sarasarasa.lifeup.converter.ExpRewardConverter
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.CategoryModel
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.models.TaskTargetModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.utils.CalendarUtil
import net.sarasarasa.lifeup.utils.ClickUtils
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


open class AddToDoItemActivity : AppCompatActivity() {

    protected val todoService = TodoServiceImpl()
    protected var iCheckedItemIndex = 0
    protected var iUrgency = 0
    protected var iDifficulty = 0

    //没按下确定键时候的频次选择
    protected var iTempFrequency = 0
    protected var iFrequency = 0
    protected var lCategoryId = 0L
    protected var arrAbbrBtn: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)
    protected var arrIgnoreDayOfWeek: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

    protected var isUseSpecificExpireTime = false
    protected val simpleDateTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    protected val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    protected var isSelectedAttrs = false


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
        initCategoryTextView()
        btn_ddl_set_spec_time.setOnClickListener { showExpireTimePickerDialog() }
        iv_extra_question.setOnClickListener { scrollToBottomBeforeShowGuide() }
        iv_bouns_question.setOnClickListener { ToastUtils.showShortToast(getString(R.string.add_to_do_bouns_question)) }
        iv_basic_question.setOnClickListener { scrollToTopBeforeShowGuide() }
        tv_category.setOnClickListener { showCategorySheetDialog() }
    }

    private fun initCategoryTextView() {
        val optionSharedPreferences = LifeUpApplication.getLifeUpApplication().getSharedPreferences("options", Context.MODE_PRIVATE)
        val currentCategoryId = optionSharedPreferences.getLong("categoryId", 0L)
        lCategoryId = currentCategoryId
        if (lCategoryId == -1L) lCategoryId = 0L
        tv_category.text = todoService.getCategoryNameById(lCategoryId)
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
        et_expire_time.inputType = InputType.TYPE_NULL
        //第一次点击首先响应Focus，下同
        et_expire_time.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                view.post { showExpireTimeMenu(view) }
        }

        et_expire_time.setOnClickListener {
            it.post { showExpireTimeMenu(it) }
        }

        et_remindDate.inputType = InputType.TYPE_NULL
        et_remindDate.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                view.post { showRemindTimeMenu(view) }
        }

        et_remindDate.setOnClickListener {
            it.post { showRemindTimeMenu(it) }
        }

        et_startTime.inputType = InputType.TYPE_NULL
        et_startTime.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                view.post { showStartDatePickerDialog() }
        }

        et_startTime.setOnClickListener {
            it.post { showStartDatePickerDialog() }
        }

    }

    private fun showExpireTimeMenu(focusView: View) {
        val mPopupMenu = PopupMenu(this, focusView)
        val cal = Calendar.getInstance()
        mPopupMenu.menuInflater.inflate(R.menu.menu_expire_time, mPopupMenu.menu)
        mPopupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.expire_today -> {
                    et_expire_time.setText(simpleDateFormat.format(cal.time))
                    btn_ddl_reset.visibility = View.VISIBLE
                    btn_ddl_set_spec_time.visibility = View.VISIBLE
                    return@setOnMenuItemClickListener true
                }
                R.id.expire_tomorrow -> {
                    cal.add(Calendar.DATE, 1)
                    et_expire_time.setText(simpleDateFormat.format(cal.time))
                    btn_ddl_reset.visibility = View.VISIBLE
                    btn_ddl_set_spec_time.visibility = View.VISIBLE
                    return@setOnMenuItemClickListener true
                }
                R.id.expire_weekend -> {
                    val dateShouldAdd = (8 - cal.get(Calendar.DAY_OF_WEEK)) % 7
                    cal.add(Calendar.DATE, dateShouldAdd)
                    et_expire_time.setText(simpleDateFormat.format(cal.time))
                    btn_ddl_reset.visibility = View.VISIBLE
                    btn_ddl_set_spec_time.visibility = View.VISIBLE
                    return@setOnMenuItemClickListener true
                }
                R.id.custom_item -> {
                    showDatePickerDialog()
                    return@setOnMenuItemClickListener true
                }
                else -> true
            }
        }
        mPopupMenu.show()
    }

    private fun showRemindTimeMenu(focusView: View) {
        val mPopupMenu = PopupMenu(this, focusView)
        mPopupMenu.menuInflater.inflate(R.menu.menu_remind_time, mPopupMenu.menu)
        mPopupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.before_expire_10min_item -> {
                    et_remindDate.setText(getString(R.string.add_to_do_menu_before_expire_10min_item))
                    btn_remind_reset.visibility = View.VISIBLE
                    return@setOnMenuItemClickListener true
                }
                R.id.before_expire_30min_item -> {
                    et_remindDate.setText(getString(R.string.add_to_do_menu_before_expire_30min_item))
                    btn_remind_reset.visibility = View.VISIBLE
                    return@setOnMenuItemClickListener true
                }
                R.id.before_expire_1h_item -> {
                    et_remindDate.setText(getString(R.string.add_to_do_menu_before_expire_1h_item))
                    btn_remind_reset.visibility = View.VISIBLE
                    return@setOnMenuItemClickListener true
                }
                R.id.custom_item -> {
                    showRemindDatePickerDialog()
                    return@setOnMenuItemClickListener true
                }
                else -> true
            }
        }
        mPopupMenu.show()
    }

    /** 初始化重复频次选择 **/
    private fun initRepeater() {
        et_repeat.inputType = InputType.TYPE_NULL
        et_repeat.setText("单次")
        et_repeat.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showRepeaterDialog()
        }

        et_repeat.setOnClickListener { showRepeaterDialog(); }
        til_repeat.visibility = View.VISIBLE

        btn_repeat_set_ignore_day_of_week.setOnClickListener {
            showIgnoreDayOfWeekDialog()
        }
    }

    /** 重置期限日期 **/
    fun expireTimeReset(view: View) {
        et_expire_time.setText("")
        isUseSpecificExpireTime = false
        view.visibility = View.INVISIBLE
        btn_ddl_set_spec_time.visibility = View.INVISIBLE
    }

    /** 重置开始时间 **/
    fun startTimeReset(view: View) {
        et_startTime.setText("")
        view.visibility = View.INVISIBLE
    }

    /** 重置提醒日期 **/
    fun finishRemindReset(view: View) {
        et_remindDate.setText("")
        //et_startTime.setText("")
        //使重置按钮[不可见]
        view.visibility = View.INVISIBLE
    }

    /**
     * 展示期限日期选择对话框
     */
    @SuppressLint("SetTextI18n")
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val strMonthOfYear: String = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
            val strDayOfMonth: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()

            et_expire_time.setText("$year/$strMonthOfYear/$strDayOfMonth")

            til_repeat.visibility = View.VISIBLE
            btn_ddl_reset.visibility = View.VISIBLE
            btn_ddl_set_spec_time.visibility = View.VISIBLE
            btn_ddl_set_spec_time.setOnClickListener { showExpireTimePickerDialog() }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //最小日期限制
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    /**
     * 展示期限时间选择对话框
     */
    @SuppressLint("SetTextI18n")
    private fun showExpireTimePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

            val strHourOfDay: String = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            val strMinute: String = if (minute < 10) "0$minute" else minute.toString()

            et_expire_time.setText(et_expire_time.text.toString().split(" ")[0] + " $strHourOfDay:$strMinute:00")
            isUseSpecificExpireTime = true
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        datePickerDialog.setOnCancelListener {
        }
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
            btn_remind_reset.visibility = View.VISIBLE
            showRemindTimePickerDialog()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //最小日期限制
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    /**
     * 展示提醒时间选择对话框
     */
    @SuppressLint("SetTextI18n")
    private fun showRemindTimePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            val strHourOfDay: String = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            val strMinute: String = if (minute < 10) "0$minute" else minute.toString()

            btn_remind_reset.visibility = View.VISIBLE
            et_remindDate.setText(et_remindDate.text.toString() + " $strHourOfDay:$strMinute:00")
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        datePickerDialog.setOnCancelListener {
            et_remindDate.setText("")
        }
        datePickerDialog.show()
    }


    /**
     * 展示开始日期选择对话框
     */
    private fun showStartDatePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val strMonthOfYear: String = if (monthOfYear + 1 < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
            val strDayOfMonth: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()

            et_startTime.setText("$year/$strMonthOfYear/$strDayOfMonth")
            btn_start_time_reset.visibility = View.VISIBLE
            showStartTimePickerDialog()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    /**
     * 展示开始时间选择对话框
     */
    @SuppressLint("SetTextI18n")
    private fun showStartTimePickerDialog() {
        val c = Calendar.getInstance()
        val datePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            //et_startTime.setText("${hourOfDay}:${minute}:00")
            btn_start_time_reset.visibility = View.VISIBLE

            val strHourOfDay: String = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            val strMinute: String = if (minute < 10) "0$minute" else minute.toString()

            et_startTime.setText(et_startTime.text.toString() + " $strHourOfDay:$strMinute:00")
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

        datePickerDialog.setOnCancelListener {
            et_startTime.setText("")
        }
        datePickerDialog.show()
    }

    /**
     * 展示重复频次选择对话框
     */
    private fun showRepeaterDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_repeat, null)
        // 让光标指向最尾部
        dialogView.editText.setSelection(1)
        val arrButton = ArrayList<FancyButton>()
        arrButton.apply {
            add(dialogView.button_fre_none)
            add(dialogView.button_fre_ebbinghaus)
            add(dialogView.button_fre0)
            add(dialogView.button_fre1)
            add(dialogView.button_fre2)
            add(dialogView.button_fre7)
            add(dialogView.button_fre14)
            add(dialogView.button_fre30)
            add(dialogView.button_fre_custom)
        }

        for (button in arrButton) {
            button.setOnClickListener { setTaskFre(button, dialogView) }
        }

        dialogView.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setTaskFre(dialogView.button_fre_custom, dialogView)
            }

        })

        val clickedButton = when (iFrequency) {
            0 -> dialogView.button_fre0
            1 -> dialogView.button_fre1
            2 -> dialogView.button_fre2
            7 -> dialogView.button_fre7
            14 -> dialogView.button_fre14
            30 -> dialogView.button_fre30
            -1 -> dialogView.button_fre_none
            -2 -> dialogView.button_fre_custom
            -3 -> dialogView.button_fre_ebbinghaus
            else -> dialogView.button_fre0
        }
        setTaskFre(clickedButton, dialogView)


        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.team_add_set_repeat))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.btn_yes)) { _, _ ->
                    if (iTempFrequency != -2) {
                        iFrequency = iTempFrequency
                    } else {
                        try {
                            iFrequency = if (!dialogView.editText.text.toString().isBlank())
                                Integer.valueOf(dialogView.editText.text.toString()) ?: 0
                            else 0
                        } catch (e: Exception) {
                            ToastUtils.showShortToast(getString(R.string.illegal_input))
                        }
                    }

                    if (iFrequency == 1) {
                        btn_repeat_set_ignore_day_of_week.visibility = View.VISIBLE
                    } else {
                        btn_repeat_set_ignore_day_of_week.visibility = View.INVISIBLE
                    }
                    //ToastUtils.showShortToast("u choose:$iFrequency")
                    if (iFrequency == 1 && arrIgnoreDayOfWeek.contains(1)) {
                        et_repeat.setText(TodoItemConverter.iFrequencyWithIgnoreToNormalString(arrIgnoreDayOfWeek))
                    } else et_repeat.setText(TodoItemConverter.iFrequencyToNormalString(iFrequency))

                }
                .setNegativeButton(getString(R.string.btn_cancel)) { _, _ ->
                    iTempFrequency = iFrequency
                }
                .create()
        dialog.show()
    }

    private fun setTaskFre(button: FancyButton, dialogView: View) {
        iTempFrequency = when (button.id) {
            R.id.button_fre0 -> 0
            R.id.button_fre1 -> 1
            R.id.button_fre2 -> 2
            R.id.button_fre7 -> 7
            R.id.button_fre14 -> 14
            R.id.button_fre30 -> 30
            R.id.button_fre_none -> -1
            R.id.button_fre_custom -> -2
            R.id.button_fre_ebbinghaus -> -3
            else -> 0
        }

        resetButtonColor(dialogView)

        button.setBackgroundColor(getThemeColor(iTempFrequency))
        button.setTextColor(ContextCompat.getColor(this, R.color.white))

    }

    private fun resetButtonColor(dialogView: View) {
        dialogView.button_fre0.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre0.setTextColor(getThemeColor(0))
        dialogView.button_fre_none.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre_none.setTextColor(getThemeColor(0))
        dialogView.button_fre_ebbinghaus.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre_ebbinghaus.setTextColor(getThemeColor(0))
        dialogView.button_fre1.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre1.setTextColor(getThemeColor(1))
        dialogView.button_fre2.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre2.setTextColor(getThemeColor(2))
        dialogView.button_fre7.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre7.setTextColor(getThemeColor(7))
        dialogView.button_fre14.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre14.setTextColor(getThemeColor(14))
        dialogView.button_fre30.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre30.setTextColor(getThemeColor(30))
        dialogView.button_fre_custom.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        dialogView.button_fre_custom.setTextColor(getThemeColor(-2))
    }

    private fun showIgnoreDayOfWeekDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ignore, null)

        val arrButton = ArrayList<FancyButton>()
        arrButton.apply {
            // index0 用作记录忽略的数目
            add(dialogView.button_day1)
            add(dialogView.button_day1)
            add(dialogView.button_day2)
            add(dialogView.button_day3)
            add(dialogView.button_day4)
            add(dialogView.button_day5)
            add(dialogView.button_day6)
            add(dialogView.button_day7)
        }

        // 根据忽略情况设置按钮样式
        for (i in arrButton.indices) {
            if (i == 0) continue
            if (arrIgnoreDayOfWeek[i] == 0) {
                arrButton[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                arrButton[i].setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                arrButton[i].setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                arrButton[i].setTextColor(ContextCompat.getColor(this, R.color.blue))
            }

            arrButton[i].setOnClickListener {
                if (arrIgnoreDayOfWeek[i] == 0) {
                    // arrIgnoreDayOfWeek[0] 用作计数器，表示忽略的数量，不能让忽略星期一-七
                    if (arrIgnoreDayOfWeek[0] != 6) {
                        arrIgnoreDayOfWeek[i] = 1
                        arrIgnoreDayOfWeek[0]++
                        arrButton[i].setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                        arrButton[i].setTextColor(ContextCompat.getColor(this, R.color.blue))
                    }
                } else {
                    arrIgnoreDayOfWeek[i] = 0
                    arrIgnoreDayOfWeek[0]--
                    arrButton[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
                    arrButton[i].setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
        }

        val dialog = AlertDialog.Builder(this).setTitle(getString(R.string.add_to_do_set_weekday_ignore))
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_yes)) { _, _ ->
                }
                .setOnDismissListener {
                    et_repeat.setText(TodoItemConverter.iFrequencyWithIgnoreToNormalString(arrIgnoreDayOfWeek))
                }
                .create()

        dialog.show()
    }

    /** 根据[taskFrequency: String]获得[color]主题色 **/
    private fun getThemeColor(taskFrequency: Int): Int {
        return ContextCompat.getColor(this, TodoItemConverter.strFrequencyToColorId(taskFrequency, checkPreferences = false))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_to_do_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_finish -> {
                if (ClickUtils.isNotFastClick()) {
                    if (check()) {
                        if (!isSelectedAttrs) {
                            MaterialDialog(this).show {
                                title(text = getString(R.string.add_to_do_not_seleced_attrs_alarm))
                                message(text = getString(R.string.add_to_do_not_seleced_attrs_alarm_message))
                                positiveButton(R.string.btn_yes) {
                                    addItem(getItem(newItem = true))
                                }
                                negativeButton(R.string.btn_cancel)
                                lifecycleOwner(this@AddToDoItemActivity)
                            }
                        } else addItem(getItem(newItem = true))
                    }
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** 创建新待办事项的响应 **/
    protected fun getItem(newItem: Boolean): TaskModel {
        // 将表单转换为对象
        val content = til_toDoText.editText?.text.toString()
        val remark = til_remark.editText?.text.toString()
        // 转换为Date类型
        val taskDeadline = til_deadLine.editText?.text.toString()
        var dateTaskDeadline: Date? = null
        if (!taskDeadline.isBlank()) {
            dateTaskDeadline = if (isUseSpecificExpireTime)
                simpleDateTimeFormat.parse(til_deadLine.editText?.text.toString())
            else simpleDateFormat.parse(til_deadLine.editText?.text.toString())
        }

        val taskRemindDateAndTime = til_remindDate.editText?.text.toString()
        var dateTaskRemindDateAndTime: Date? = null
        if (!taskRemindDateAndTime.isBlank()) {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

            when (taskRemindDateAndTime) {
                getString(R.string.add_to_do_menu_before_expire_10min_item) -> {
                    if (dateTaskDeadline != null) {
                        dateTaskRemindDateAndTime = CalendarUtil.getTimeAfterSeveralMinutesTime(dateTaskDeadline, -10)
                        if (!isUseSpecificExpireTime) {
                            dateTaskRemindDateAndTime = dateTaskRemindDateAndTime.let { CalendarUtil.getTimeAfterSeveralMinutesTime(it, 1440) }
                        }
                    }
                }
                getString(R.string.add_to_do_menu_before_expire_30min_item) -> {
                    if (dateTaskDeadline != null) {
                        dateTaskRemindDateAndTime = CalendarUtil.getTimeAfterSeveralMinutesTime(dateTaskDeadline, -30)
                        if (!isUseSpecificExpireTime) {
                            dateTaskRemindDateAndTime = dateTaskRemindDateAndTime.let { CalendarUtil.getTimeAfterSeveralMinutesTime(it, 1440) }
                        }
                    }
                }
                getString(R.string.add_to_do_menu_before_expire_1h_item) -> {
                    if (dateTaskDeadline != null) {
                        dateTaskRemindDateAndTime = CalendarUtil.getTimeAfterSeveralMinutesTime(dateTaskDeadline, -60)
                        if (!isUseSpecificExpireTime) {
                            dateTaskRemindDateAndTime = dateTaskRemindDateAndTime.let { CalendarUtil.getTimeAfterSeveralMinutesTime(it, 1440) }
                        }
                    }
                }
                else -> dateTaskRemindDateAndTime = simpleDateFormat.parse(taskRemindDateAndTime)
            }
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
        val taskShared = false
        var relatedAttribute = arrayOf<String>()

        val enableEbbinghausMode = (iFrequency == -3)
        val taskFrequency = if (enableEbbinghausMode) 1 else iFrequency
        var targetTimes = 0

        if (iFrequency != 0 || iFrequency != -1 || iFrequency != -3) {
            if (til_target.editText?.text != null && !til_target.editText?.text.isNullOrEmpty()) {
                try {
                    val timesFromText = Integer.valueOf(til_target.editText?.text.toString())
                    if (timesFromText > 0) {
                        targetTimes = timesFromText
                    }
                } catch (e: Exception) {
                    ToastUtils.showShortToast(getString(R.string.add_to_do_goal_illegal_input))
                }
            }
        }

        // 艾宾浩斯记忆模式的事项应该有 7 次循环
        if (enableEbbinghausMode) {
            targetTimes = 7
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
        taskModel.currentTimes = 1

        if (newItem && targetTimes != 0 && iFrequency != 0) {
            val newTarget = TaskTargetModel(targetTimes, taskModel.expReward * targetTimes / 10)
            newTarget.save()
            taskModel.taskTargetId = newTarget.id
        }

        // 开始时间
        val taskStartDateAndTime = til_startTime.editText?.text.toString()
        if (!taskStartDateAndTime.isEmpty()) {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            taskModel.startTime = simpleDateFormat.parse(taskStartDateAndTime)
            taskModel.isUserInputStartTime = true
        } else {
            // 开始时间和期限时间的间隔应该为(重复频次 - 1)
/*            if (taskModel.taskFrequency > 0)
                cal.add(Calendar.DATE, (taskModel.taskFrequency * -1) + 1)*/

            // 为了让Ebbinghaus记忆法的事项的开始时间也依照重复频次往后推移
            if (enableEbbinghausMode) {
                taskModel.startTime = cal.time
                taskModel.isUserInputStartTime = true
            } else {
                taskModel.isUserInputStartTime = false
            }
        }


        // 完成奖励
        taskModel.completeReward = til_complete_reward.editText?.text.toString()
        // 期限具体时间
        taskModel.isUseSpecificExpireTime = isUseSpecificExpireTime
        // 周期忽略
        if (iFrequency == 1 && arrIgnoreDayOfWeek.contains(1)) {
            taskModel.isIgnoreDayOfWeek = arrIgnoreDayOfWeek.toCollection(ArrayList())
        }

        // 清单分类
        taskModel.categoryId = if (lCategoryId == -1L) 0L
        else lCategoryId

        // 艾宾浩斯记忆法
        taskModel.enableEbbinghausMode = enableEbbinghausMode

        return taskModel
    }

    private fun addItem(taskModel: TaskModel) {

        val id = todoService.addTodoItem(taskModel)
        WidgetUtils.updateWidgets(applicationContext)

        //设置提醒
        if (taskModel.taskRemindTime != null && id != null) {
            todoService.setOrUpdateAlarm(taskModel.taskRemindTime!!.time, id, this)
            ToastUtils.showShortToast(getString(R.string.add_to_do_remind_set_success))
        }

        //结束这个Activity
        finish()
    }

    /** 提交前对表单进行检测 **/
    protected fun check(): Boolean {
        var isAllCheckPassed = true

        if (TextUtils.isEmpty(til_toDoText.editText?.text)) {
            til_toDoText.error = getString(R.string.edit_text_empty_error)

            scroll_view.post {
                scroll_view.scrollTo(0, til_toDoText.top)
            }

            isAllCheckPassed = false
        }

        // 允许不选择属性
        isSelectedAttrs = arrAbbrBtn[SELECTED_CNT] != 0

/*        if ((TextUtils.isEmpty(til_remindDate.editText?.text) && !TextUtils.isEmpty(til_remindTime.editText?.text))
                || (!TextUtils.isEmpty(til_remindDate.editText?.text) && TextUtils.isEmpty(til_remindTime.editText?.text))) {
            ToastUtils.showShortToast("提醒日期和时间必须填写完整！")
            isAllCheckPassed = false
        }*/

        try {
            val taskRemindDateAndTime = til_remindDate.editText?.text.toString()
            if (!taskRemindDateAndTime.isBlank()) {
                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

                if (taskRemindDateAndTime != getString(R.string.add_to_do_menu_before_expire_10min_item)
                        && taskRemindDateAndTime != getString(R.string.add_to_do_menu_before_expire_30min_item)
                        && taskRemindDateAndTime != getString(R.string.add_to_do_menu_before_expire_1h_item))
                    simpleDateFormat.parse(taskRemindDateAndTime)
            }
        } catch (e: Exception) {
            ToastUtils.showShortToast(getString(R.string.add_to_do_remind_illegal_input))
            isAllCheckPassed = false
        }

        val taskStartDateAndTime = til_startTime.editText?.text.toString()
        try {
            if (!taskStartDateAndTime.isBlank()) {
                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                simpleDateFormat.parse(taskStartDateAndTime)
            }
        } catch (e: Exception) {
            ToastUtils.showShortToast(getString(R.string.add_to_do_start_time_illegal_input))
            isAllCheckPassed = false
        }

        if (isUseSpecificExpireTime && !TextUtils.isEmpty(til_deadLine.editText?.text)) {
            try {
                val dateTaskDeadline = if (isUseSpecificExpireTime)
                    simpleDateTimeFormat.parse(til_deadLine.editText?.text.toString())
                else simpleDateFormat.parse(til_deadLine.editText?.text.toString())

                if (dateTaskDeadline.before(Date())) {
                    ToastUtils.showShortToast(getString(R.string.add_to_do_end_time_too_early))
                    isAllCheckPassed = false
                }
            } catch (e: Exception) {
                ToastUtils.showShortToast(getString(R.string.add_to_do_end_time_illegal_input))
                isAllCheckPassed = false
            }
        }

        if (!TextUtils.isEmpty(til_deadLine.editText?.text) && !TextUtils.isEmpty(taskStartDateAndTime)) {
            try {
                val dateStartTime = simpleDateTimeFormat.parse(taskStartDateAndTime)
                val dateTaskDeadline = if (isUseSpecificExpireTime)
                    simpleDateTimeFormat.parse(til_deadLine.editText?.text.toString())
                else simpleDateFormat.parse(til_deadLine.editText?.text.toString())

                val deadLine = Calendar.getInstance()
                deadLine.time = dateTaskDeadline

                if (!isUseSpecificExpireTime)
                    deadLine.add(Calendar.DATE, 1)

                if (dateStartTime.after(deadLine.time)) {
                    ToastUtils.showShortToast(getString(R.string.add_to_do_start_time_too_late))
                    isAllCheckPassed = false
                }
            } catch (e: Exception) {
                ToastUtils.showShortToast(getString(R.string.add_to_do_start_time_illegal_input))
                isAllCheckPassed = false
            }
        }


        val isNeedDDl = when (til_repeat.editText?.text.toString()) {
            getString(R.string.add_to_do_not_repeat) -> false
            getString(R.string.add_to_do_repeat_times) -> false
            else -> true
        }

        if (isNeedDDl && TextUtils.isEmpty(til_deadLine.editText?.text)) {
            //容错处理：没填期限日期的时候自动填充为当天
            if (TextUtils.isEmpty(til_startTime.editText?.text)) {
                til_deadLine.editText?.setText(simpleDateFormat.format(Date()))
            }
            // 或者是开始时间当天
            else {
                til_deadLine.editText?.setText(simpleDateFormat.format(simpleDateFormat.parse(taskStartDateAndTime)))
            }
        }

        if (iFrequency != 0 || iFrequency != -1) {
            if (til_target.editText?.text != null && !til_target.editText?.text.isNullOrEmpty()) {
                try {
                    val timesFromText = Integer.valueOf(til_target.editText?.text.toString())
                    if (timesFromText < 0) {
                        til_target.error = getString(R.string.illegal_input)
                        isAllCheckPassed = false
                    }
                } catch (e: Exception) {
                    ToastUtils.showShortToast(getString(R.string.add_to_do_goal_illegal_input))
                    til_target.error = getString(R.string.illegal_input)
                    isAllCheckPassed = false
                }
            }
        }

        return isAllCheckPassed
    }

    fun showDialogAttribution(view: View) {
        val dialog = AlertDialog.Builder(this).setView(R.layout.dialog_abbr_desc).setTitle(getString(R.string.add_to_do_attr_desc_title)).create()

        with(dialog) {
            this.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_yes)) { _, _ ->
                cancel()
            }
            this.show()

        }
    }

    open fun showMoreOptions(view: View) {
        val animation = TranslateAnimation(1, -1.0F, 1, 0.0F, 1, 0.0F, 1, 0.0F)
        animation.duration = 500L
        til_startTime.startAnimation(animation)
        til_startTime.visibility = View.VISIBLE
        til_target.startAnimation(animation)
        til_target.visibility = View.VISIBLE
        til_complete_reward.startAnimation(animation)
        til_complete_reward.visibility = View.VISIBLE

        val disappearAnimation = TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 0.0F, 1, -1.0F)
        disappearAnimation.duration = 500L
        view.startAnimation(disappearAnimation)
        view.visibility = View.GONE
        scroll_view.post {
            scroll_view.scrollTo(0, view.top)
        }
    }

    private fun scrollToBottomBeforeShowGuide() {
        if (btn_show_more.visibility == View.VISIBLE) {
            showMoreOptions(btn_show_more)
            scroll_view.post {
                showExtraGuide()
            }
        } else {
            scroll_view.post {
                scroll_view.scrollTo(0, cw_extra.bottom)
                showExtraGuide()
            }
        }

    }

    private fun showExtraGuide() = TapTargetSequence(this)
            .targets(TapTarget.forView(sp_remind_date, getString(R.string.add_to_do_taptarget_remind_title), getString(R.string.add_to_do_taptarget_remind_content))
                    .outerCircleColor(R.color.blue)
                    .outerCircleAlpha(0.96f)
                    .titleTextSize(18)
                    .titleTextColor(R.color.white)
                    .descriptionTextSize(12)
                    .descriptionTextColor(R.color.white)
                    .textColor(R.color.white)
                    .drawShadow(true)
                    .cancelable(true)
                    .tintTarget(false)
                    .transparentTarget(true)
                    .targetRadius(60),
                    TapTarget.forView(sp_startTime, getString(R.string.add_to_do_taptarget_start_time_title), getString(R.string.add_to_do_taptarget_start_time_content))
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(18)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(12)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(false)
                            .transparentTarget(true)
                            .targetRadius(60),
                    TapTarget.forView(sp_deadLine, getString(R.string.add_to_do_taptarget_deadline_title), getString(R.string.add_to_do_taptarget_deadline_content))
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(18)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(12)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(false)
                            .transparentTarget(true)
                            .targetRadius(60),
                    TapTarget.forView(sp_repeat, getString(R.string.add_to_do_taptarget_repeat_title), getString(R.string.add_to_do_taptarget_repeat_content))
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(18)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(12)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(false)
                            .transparentTarget(true)
                            .targetRadius(60),
                    TapTarget.forView(sp_target, getString(R.string.add_to_do_taptarget_target_title), getString(R.string.add_to_do_taptarget_target_content))
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(18)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(12)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(false)
                            .transparentTarget(true)
                            .targetRadius(60),
                    TapTarget.forView(sp_complete_reward, getString(R.string.add_to_do_taptarget_reward_title), getString(R.string.add_to_do_taptarget_reward_content))
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(18)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(12)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(false)
                            .transparentTarget(true)
                            .targetRadius(60)
            )
            .start()

    private fun scrollToTopBeforeShowGuide() {
        scroll_view.post {
            scroll_view.scrollTo(0, cw_basic.top)
            showBasicGuide()
        }
    }

    private fun showBasicGuide() = TapTargetSequence(this)
            .targets(TapTarget.forView(sp_to_do_text, getString(R.string.add_to_do_taptarget_todo_title), getString(R.string.add_to_do_taptarget_todo_content))
                    .outerCircleColor(R.color.blue)
                    .outerCircleAlpha(0.96f)
                    .titleTextSize(18)
                    .titleTextColor(R.color.white)
                    .descriptionTextSize(14)
                    .descriptionTextColor(R.color.white)
                    .textColor(R.color.white)
                    .drawShadow(true)
                    .cancelable(true)
                    .tintTarget(false)
                    .transparentTarget(true)
                    .targetRadius(60),
                    TapTarget.forView(sp_remark, getString(R.string.add_to_do_taptarget_remark_title), getString(R.string.add_to_do_taptarget_remark_content))
                            .outerCircleColor(R.color.blue)
                            .outerCircleAlpha(0.96f)
                            .titleTextSize(18)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(14)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(false)
                            .transparentTarget(true)
                            .targetRadius(60)
            )
            .start()


    private fun showCategorySheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_category, null)
        val list = todoService.listCategory().toMutableList()
        list.add(0, CategoryModel(getString(R.string.category_default), false))
        val adapter = CategoryAdapter(R.layout.item_category, list)
        view.rv_category.layoutManager = LinearLayoutManager(this)
        view.rv_category.adapter = adapter
        view.ll_category_add.visibility = View.GONE
        adapter.setOnItemClickListener { mAdapter, _, position ->
            val item = mAdapter.getItem(position) as CategoryModel
            if (position == 0) {
                lCategoryId = 0L
            } else {
                item.id?.let {
                    lCategoryId = it
                }
            }

            tv_category.text = todoService.getCategoryNameById(lCategoryId)

            if (bottomSheetDialog.isShowing)
                bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }
}
