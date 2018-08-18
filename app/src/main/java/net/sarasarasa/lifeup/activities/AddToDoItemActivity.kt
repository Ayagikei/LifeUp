package net.sarasarasa.lifeup.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.activity_add_to_do_item.*
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.constants.AddToDoItemConstants
import net.sarasarasa.lifeup.converter.TodoItemConverter
import net.sarasarasa.lifeup.models.TaskModel
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
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
        if (arrAbbrBtn[index] == AddToDoItemConstants.SELECTED) {
            val cm = ColorMatrix()
            cm.setSaturation(0f) // 设置饱和度
            val grayColorFilter = ColorMatrixColorFilter(cm)

            if (view is ImageView)
                view.colorFilter = grayColorFilter

            arrAbbrBtn[index] = AddToDoItemConstants.UNSELECTED
            arrAbbrBtn[AddToDoItemConstants.SELECTED_CNT]--
        } else {
            //不能选择超过3个
            if (arrAbbrBtn[AddToDoItemConstants.SELECTED_CNT] < AddToDoItemConstants.MAX_SELECTABLE_NUM) {
                //当前没有选中，恢复颜色
                if (view is ImageView)
                    view.colorFilter = null

                arrAbbrBtn[index] = AddToDoItemConstants.SELECTED
                arrAbbrBtn[AddToDoItemConstants.SELECTED_CNT]++
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
        dDDL.inputType = InputType.TYPE_NULL
        dDDL.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showDatePickerDialog()
        }

        dDDL.setOnClickListener {
            showDatePickerDialog()
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

    /**
     * 展示日期选择对话框
     */
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            dDDL.setText("$year/${monthOfYear + 1}/$dayOfMonth")
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
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
                    addItem()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** 创建新待办事项的响应 **/
    private fun addItem() {
        // 将表单转换为对象
        val content = til_toDoText.editText?.text.toString()
        val remark = til_remark.editText?.text.toString()
        // TODO:转换为Date类型
        val taskDeadline = til_deadLine.editText?.text.toString()
        val taskUrgencyLevel = when (iUrgency) {
            0 -> 0
            33 -> 1
            66 -> 2
            99 -> 3
            else -> 0
        }
        val taskDifficultyLevel = when (iDifficulty) {
            0 -> 0
            33 -> 1
            66 -> 2
            99 -> 3
            else -> 0
        }
        val taskShared = switch1.isChecked
        var relatedAttribute = arrayOf<String>()

        for (i in arrAbbrBtn.indices) {
            if (i == 0) continue
            if (arrAbbrBtn[i] == AddToDoItemConstants.SELECTED) {
                val strRes = TodoItemConverter.indexToString(i)
                relatedAttribute = relatedAttribute.plusElement(strRes)
            }
        }

        val taskModel = TaskModel(
                content,
                remark,
                null,
                relatedAttribute.getOrElse(0) { "" },
                relatedAttribute.getOrElse(1) { "" },
                relatedAttribute.getOrElse(2) { "" },
                taskUrgencyLevel,
                taskDifficultyLevel,
                null,
                taskShared,
                null
        )

        Toast.makeText(baseContext, taskModel.toString(), Toast.LENGTH_LONG).show()
        todoService.addTodoItem(taskModel)

        //结束这个Activity
        finish()
    }

    /** 提交前对表单进行检测 **/
    private fun check(): Boolean {
        var isAllCheckPassed = true

        if (TextUtils.isEmpty(til_toDoText.editText?.text)) {
            til_toDoText.error = "不能为空"
            isAllCheckPassed = false
        }

        return isAllCheckPassed
    }
}
