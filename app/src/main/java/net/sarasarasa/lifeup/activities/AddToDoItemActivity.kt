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
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.activity_add_to_do_item.*
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import java.util.*


class AddToDoItemActivity : AppCompatActivity() {

    private var iCheckedItemIndex = 0
    private var seekBar1 = 0
    private var arrSkillBtn: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)

    private val _selectedCnt = 0
    private val _strengthIndex = 1
    private val _learningIndex = 2
    private val _charmIndex = 3
    private val _enduranceIndex = 4
    private val _vitalityIndex = 5
    private val _creativeIndex = 6
    private val _maxSelectable = 3

    private val _selected = 1
    private val _unselected = 0

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

    fun switchBtn(view: View) {
        val index = when (view.id) {
            R.id.iv_strength -> _strengthIndex
            R.id.iv_learning -> _learningIndex
            R.id.iv_charm -> _charmIndex
            R.id.iv_endurance -> _enduranceIndex
            R.id.iv_vitality -> _vitalityIndex
            R.id.iv_creative -> _creativeIndex
            else -> return
        }

        //当前选中的话
        if (arrSkillBtn[index] == _selected) {
            val cm = ColorMatrix()
            cm.setSaturation(0f) // 设置饱和度
            val grayColorFilter = ColorMatrixColorFilter(cm)

            if (view is ImageView)
                view.colorFilter = grayColorFilter

            arrSkillBtn[index] = _unselected
            arrSkillBtn[_selectedCnt]--
        } else {
            //不能选择超过3个
            if (arrSkillBtn[_selectedCnt] < _maxSelectable) {
                //当前没有选中，恢复颜色
                if (view is ImageView)
                    view.colorFilter = null

                arrSkillBtn[index] = _selected
                arrSkillBtn[_selectedCnt]++
            }
        }
    }

    private fun initSeekBar() {
        sb_urgence.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                //四个刻度对应的数值为0，33,66,100
                seekBar1 = leftValue.toInt()
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
                //四个刻度对应的数值为0，33,66,100
                seekBar1 = leftValue.toInt()
            }

            override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                //start tracking touch
            }

            override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                //stop tracking touch
            }
        })
    }

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
                if (check())
                    addItem()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun addItem() {
        // TODO:将表单转换为对象
    }

    private fun check(): Boolean {
        var isAllCheckPassed = true

        if (TextUtils.isEmpty(til_toDoText.editText?.text)) {
            til_toDoText.error = "不能为空"
            isAllCheckPassed = false
        }

        return isAllCheckPassed
    }
}
