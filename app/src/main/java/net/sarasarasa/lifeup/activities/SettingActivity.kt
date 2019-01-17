package net.sarasarasa.lifeup.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*
import net.sarasarasa.lifeup.R
import net.sarasarasa.lifeup.service.impl.TodoServiceImpl
import net.sarasarasa.lifeup.service.impl.UserServiceImpl
import net.sarasarasa.lifeup.utils.ToastUtils
import net.sarasarasa.lifeup.utils.WidgetUtils


class SettingActivity : AppCompatActivity() {

    val userService = UserServiceImpl()
    val todoService = TodoServiceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

/*        var svReopenSplash = findViewById<SettingView>(R.id.setting_item_reopenSplash)
        svReopenSplash.setOnItemViewClick {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/

        val sharedPreferences = getSharedPreferences("options", Context.MODE_PRIVATE)
        val isShowRepeatDialog = sharedPreferences.getBoolean("isShowRepeatDialog", true)
        val isIgnoreActivitySubmitDialog = sharedPreferences.getBoolean("isIgnoreActivitySubmitDialog", true)
        val isDefaultRemake = sharedPreferences.getBoolean("isDefaultRemake", true)
        val isWidgetDarkTheme = sharedPreferences.getBoolean("isWidgetDarkTheme", false)
        val isWidgetDarkThemeWhiteIconAndFonts = sharedPreferences.getBoolean("isWidgetDarkThemeWhiteIconAndFonts", false)
        val isHideNotBegunItem = sharedPreferences.getBoolean("isHideNotBegunItem", false)
        val isStatusPlayAnimation = sharedPreferences.getBoolean("isStatusPlayAnimation", false)
        val isHideCommunity = sharedPreferences.getBoolean("isHideCommunity", false)
        val editor = sharedPreferences.edit()


        switch_default_repeat.isChecked = !isShowRepeatDialog
        switch_default_repeat.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isShowRepeatDialog", !isChecked)
            editor.apply()
        }

        switch_ignore_activity_submit_dialog.isChecked = isIgnoreActivitySubmitDialog
        switch_ignore_activity_submit_dialog.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isIgnoreActivitySubmitDialog", isChecked)
            editor.apply()
        }

        switch_default_remake_overdue.isChecked = isDefaultRemake
        switch_default_remake_overdue.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isDefaultRemake", isChecked)
            editor.apply()
        }

        switch_widget_dark_theme.isChecked = isWidgetDarkTheme
        switch_widget_dark_theme.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isWidgetDarkTheme", isChecked)
            editor.apply()
            WidgetUtils.updateWidgets(applicationContext)
        }

        switch_hide_item.isChecked = isHideNotBegunItem
        switch_hide_item.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isHideNotBegunItem", isChecked)
            editor.apply()
            WidgetUtils.updateWidgets(applicationContext)
        }

        switch_status_play_animation.isChecked = isStatusPlayAnimation
        switch_status_play_animation.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isStatusPlayAnimation", isChecked)
            editor.apply()
        }

        switch_hide_community.isChecked = isHideCommunity
        switch_hide_community.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isHideCommunity", isChecked)
            editor.apply()
        }

        switch_widget_dark_theme_white_icon_and_fonts.isChecked = isWidgetDarkThemeWhiteIconAndFonts
        switch_widget_dark_theme_white_icon_and_fonts.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("isWidgetDarkThemeWhiteIconAndFonts", isChecked)
            editor.apply()
            WidgetUtils.updateWidgets(applicationContext)
        }

        setting_item_guide.setOnItemViewClick {
            val statusSharedPreferences = getSharedPreferences("status", Context.MODE_PRIVATE)
            val statusEditor = statusSharedPreferences.edit()
            statusEditor.putBoolean("isShowGuide", false)
            statusEditor.apply()
            ToastUtils.showShortToast("重新开启指引成功")
        }
/*        setting_item_change_widget_font_color.setOnItemViewClick {
            val mOnColorPickerListener = object : OnColorPickerListener {
                override fun onColorCancel(dialog: ColorPickerDialog) {//取消选择的颜色

                }

                override fun onColorChange(dialog: ColorPickerDialog, color: Int) {//实时监听颜色变化

                }

                override fun onColorConfirm(dialog: ColorPickerDialog, color: Int) {//确定的颜色


                    val hexColor = "#" + Integer.toHexString(color)
                    ToastUtils.showShortToast(hexColor.toString())

                    try {
                        tv_widget.setTextColor(Color.parseColor(hexColor))
                    }catch (e:Exception){
                        ToastUtils.showShortToast(e.toString())
                    }
                }
            }

            val mColorPickerDialog = ColorPickerDialog(
                    this,
                    resources.getColor(R.color.colorPrimary),
                    false,
                    mOnColorPickerListener
            ).show()

        }*/


        setting_item_logout.setOnItemViewClick {
            //清空token和非本地事项
            userService.saveToken("")
            todoService.deleteTeamTask()

            ToastUtils.showShortToast("退出成功")
        }

/*        setting_item_account.setOnItemViewClick {
            ToastUtils.showShortToast("此功能暂不可用！")
        }*/

    }
}
