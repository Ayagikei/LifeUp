package net.sarasarasa.lifeup.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.activity_add_to_do_item.*
import kotlinx.android.synthetic.main.content_add_to_do_item.*
import net.sarasarasa.lifeup.R
import java.util.*


class AddToDoItemActivity : AppCompatActivity() {

    private var iCheckedItemIndex = 0;
    private var seekBar1 = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_do_item)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        iCheckedItemIndex = 0
        initDDDL()
        initRepeater()
        initSeekBar()

        // TODO:将表单转换为对象


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
        dDDL.inputType = InputType.TYPE_NULL;
        dDDL.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                showDatePickerDialog()
        }

        dDDL.setOnClickListener {
            showDatePickerDialog();
        }
    }

    private fun initRepeater() {
        et_repeat.inputType = InputType.TYPE_NULL;
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
            dDDL.setText("${year.toString()}/${monthOfYear + 1}/$dayOfMonth")
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
        return when (item.itemId) {
            R.id.action_finish -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
